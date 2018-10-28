/*
 * Copyright 2007-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ymate.module.fileuploader.controller;

import net.ymate.framework.commons.HttpClientHelper;
import net.ymate.framework.commons.IHttpResponse;
import net.ymate.framework.core.util.WebUtils;
import net.ymate.framework.exception.RequestUnauthorizedException;
import net.ymate.framework.webmvc.WebResult;
import net.ymate.module.fileuploader.*;
import net.ymate.module.fileuploader.model.Attachment;
import net.ymate.module.fileuploader.repository.IAttachmentRepository;
import net.ymate.platform.core.beans.annotation.Inject;
import net.ymate.platform.core.lang.BlurObject;
import net.ymate.platform.validation.validate.VLength;
import net.ymate.platform.validation.validate.VRequired;
import net.ymate.platform.webmvc.IUploadFileWrapper;
import net.ymate.platform.webmvc.annotation.*;
import net.ymate.platform.webmvc.base.Type;
import net.ymate.platform.webmvc.context.WebContext;
import net.ymate.platform.webmvc.view.IView;
import net.ymate.platform.webmvc.view.View;
import net.ymate.platform.webmvc.view.impl.BinaryView;
import net.ymate.platform.webmvc.view.impl.HttpStatusView;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.InputStreamBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 文件资源
 *
 * @author 刘镇 (suninformation@163.com) on 16/3/27 上午7:17
 * @version 1.0
 */
@Controller
@RequestMapping("/uploads")
public class UploadController {

    @Inject
    private IAttachmentRepository __repo;

    /**
     * 文件上传
     *
     * @param file 上传的文件
     * @param type 指定结果处理器名称
     * @return 返回文件上传处理结果
     * @throws Exception 可能产生的任何异常
     */
    @RequestMapping(value = "/push", method = {Type.HttpMethod.POST, Type.HttpMethod.OPTIONS})
    @FileUpload
    public IView __doUpload(@VRequired
                            @RequestParam IUploadFileWrapper file, @RequestParam String type) throws Exception {
        // 检查上传的文件ContentType是否在允许列表中
        if (!FileUploader.get().getModuleCfg().getAllowContentTypes().isEmpty()
                && !FileUploader.get().getModuleCfg().getAllowContentTypes().contains(file.getContentType())) {
            throw new FileUploadBase.InvalidContentTypeException("Upload file ContentType invalid.");
        }
        // 非代理模式
        if (!FileUploader.get().getModuleCfg().isProxyMode()) {
            UploadFileMeta _meta = __repo.uploadFile(file);
            if (StringUtils.isNotBlank(type)) {
                IUploadResultProcessor _processor = FileUploader.get().getUploadResultProcessor(type);
                if (_processor != null) {
                    return _processor.process(_meta);
                }
            }
            if (StringUtils.isNotBlank(_meta.getFilename())) {
                _meta.setFilename(WebUtils.decodeURL(_meta.getFilename()));
            }
            if (StringUtils.isNotBlank(_meta.getTitle())) {
                _meta.setTitle(WebUtils.decodeURL(_meta.getTitle()));
            }
            return WebResult.SUCCESS().data(_meta).toJSON();
        } else {
            // 以下是代理模式采用透传
            String _proxyPushUrl = FileUploader.get().getModuleCfg().getProxyServiceBaseUrl().concat("uploads/push");
            if (StringUtils.isNotBlank(type)) {
                _proxyPushUrl = _proxyPushUrl.concat("?type=").concat(type);
            }
            //
            IHttpResponse _result = HttpClientHelper.create().upload(_proxyPushUrl, "file", new InputStreamBody(file.getInputStream(), ContentType.create(file.getContentType()), WebUtils.encodeURL(file.getName())), null);
            if (_result.getStatusCode() != HttpServletResponse.SC_OK) {
                return WebResult.CODE(_result.getStatusCode()).toJSON();
            } else {
                return View.jsonView(_result.getContent());
            }
        }
    }

    /**
     * 文件哈希值检测
     *
     * @param hash 文件哈希值
     * @return 通过匹配文件哈希值判断服务端文件是否已存在
     * @throws Exception 可能产生的任何异常
     */
    @RequestMapping(value = "/match", method = {Type.HttpMethod.POST, Type.HttpMethod.OPTIONS})
    public IView __doMatch(@VRequired
                           @VLength(min = 32, max = 32)
                           @RequestParam String hash) throws Exception {
        // 非代理模式
        if (!FileUploader.get().getModuleCfg().isProxyMode()) {
            String _sourcePath = __repo.matchHash(hash);
            if (StringUtils.isNotBlank(_sourcePath)) {
                return WebResult.SUCCESS().attr("matched", true).data(_sourcePath).toJSON();
            }
            return WebResult.SUCCESS().attr("matched", false).toJSON();
        } else {
            // 以下是代理模式采用透传
            String _proxyPushUrl = FileUploader.get().getModuleCfg().getProxyServiceBaseUrl().concat("uploads/match");
            //
            Map<String, String> _params = new HashMap<String, String>();
            _params.put("hash", hash);
            //
            IHttpResponse _result = HttpClientHelper.create().post(_proxyPushUrl, _params);
            if (_result.getStatusCode() != HttpServletResponse.SC_OK) {
                return WebResult.CODE(_result.getStatusCode()).msg(_result.getContent()).toJSON();
            } else {
                return View.jsonView(_result.getContent());
            }
        }
    }

    /**
     * 文件资源访问<br>
     * 若请求的URL中包含参数'?attach'则强制下载<br>
     * 若请求的资源为未知或APPLICATION类型资源默认全部强制下载<br>
     * 若请求THUMB资源时将判断目标资源类型返回相应的缩略图(排除未知/TEXT/APPLICATION类型)<br>
     *
     * @param type 资源类型
     * @param hash 文件哈希值
     * @return 返回文件资料数据流
     * @throws Exception 可能产生的任何异常
     */
    @RequestMapping("/resources/{type}/{hash}")
    public IView __doResources(@PathVariable String type,
                               @PathVariable String hash) throws Exception {
        // 非代理模式
        if (!FileUploader.get().getModuleCfg().isProxyMode()) {
            IView _returnView = null;
            // 判断资源类型
            try {
                IFileUploader.ResourceType _resType = IFileUploader.ResourceType.valueOf(type.toUpperCase());
                // 判断是否采用强制下载
                boolean _isAttach = WebContext.getContext().getParameters().containsKey("attach");
                // 处理附加参数
                int _width = 0;
                int _height = 0;
                // 只有图片资源或THUMB(视频截图)才支持
                if (_resType.equals(IFileUploader.ResourceType.IMAGE) || _resType.equals(IFileUploader.ResourceType.THUMB)) {
                    String[] _params = StringUtils.split(hash, '_');
                    hash = _params[0];
                    if (_params.length > 1) {
                        _width = BlurObject.bind(_params[1]).toIntValue();
                        if (_params.length > 2) {
                            _height = BlurObject.bind(_params[2]).toIntValue();
                        }
                    }
                }
                //
                Attachment _resource;
                if (_resType.equals(IFileUploader.ResourceType.THUMB)) {
                    _resource = __repo.getResource(IFileUploader.ResourceType.VIDEO, hash);
                } else {
                    _resource = __repo.getResource(_resType, hash);
                }
                if (_resource != null) {
                    IFileUploaderModuleCfg _cfg = FileUploader.get().getModuleCfg();
                    IFileStorageAdapter _storageAdapter = _cfg.getFileStorageAdapter();
                    File _resFile = null;
                    switch (_resType) {
                        case IMAGE:
                        case THUMB:
                            _resFile = _storageAdapter.readThumb(_resType, _resource.getHash(), _resource.getSourcePath(), _width, _height);
                            break;
                        default:
                            _resFile = _storageAdapter.readFile(hash, _resource.getSourcePath());
                    }
                    if (_resFile != null && _resFile.exists()) {
                        if (__doBrowserCacheWarp(_resFile, _cfg.getResourcesCacheTimeout())) {
                            _returnView = View.httpStatusView(HttpServletResponse.SC_NOT_MODIFIED);
                        } else {
                            BinaryView _view = new BinaryView(new FileInputStream(_resFile), _resFile.length());
                            _view.setContentType(_resource.getMimeType());
                            if (_isAttach) {
                                String _fileName = StringUtils.substringAfterLast(_resource.getSourcePath(), File.separator);
                                _view.useAttachment(_fileName);
                            }
                            _returnView = _view;
                        }
                    }
                }
            } catch (IllegalArgumentException e) {
                _returnView = View.httpStatusView(HttpServletResponse.SC_BAD_REQUEST);
            } catch (RequestUnauthorizedException e) {
                _returnView = View.httpStatusView(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            }
            return _returnView == null ? HttpStatusView.NOT_FOUND : _returnView;
        } else {
            // 代理模式将不支持加载本地资源
            return HttpStatusView.NOT_FOUND;
        }
    }

    private boolean __doBrowserCacheWarp(File _resFile, int cacheTimeout) {
        //
        long _lastModified = _resFile.lastModified();
        _lastModified = TimeUnit.MILLISECONDS.toSeconds(_lastModified);
        _lastModified = TimeUnit.SECONDS.toMillis(_lastModified);
        //
        long _expiresTime = System.currentTimeMillis() + cacheTimeout;
        String _eTag = "\"" + (_resFile.lastModified() + cacheTimeout) + "\"";
        //
        HttpServletResponse _response = WebContext.getResponse();
        _response.setDateHeader("Last-Modified", _lastModified);
        _response.setDateHeader("Expires", _expiresTime);
        _response.setHeader("Cache-Control", "max-age=" + cacheTimeout);
        _response.setHeader("ETag", _eTag);
        //
        HttpServletRequest _request = WebContext.getRequest();
        String _ifNoneMatch = _request.getHeader("If-None-Match");
        //
        if (_eTag.equals(_ifNoneMatch)) {
            return true;
        } else {
            long _lastM = _resFile.lastModified();
            long _ifModifiedSince = _request.getDateHeader("If-Modified-Since");
            //
            if (_ifModifiedSince != -1) {
                Date _requestDate = new Date(_ifModifiedSince);
                Date _pageDate = new Date(_lastM);
                if (!_requestDate.before(_pageDate)) {
                    _response.setHeader("Last-Modified", _request.getHeader("If-Modified-Since"));
                    return true;
                }
            }
        }
        return false;
    }
}
