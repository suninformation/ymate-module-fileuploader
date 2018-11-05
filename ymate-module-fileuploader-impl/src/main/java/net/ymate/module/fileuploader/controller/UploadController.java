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

import net.ymate.framework.core.util.WebUtils;
import net.ymate.framework.webmvc.WebResult;
import net.ymate.module.fileuploader.*;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Date;
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
        try {
            UploadFileMeta _meta = FileUploader.get().upload(new IFileWrapper.NEW(file.getName(), file.getContentType(), file.getFile()));
            _meta.setUrl(__doFixedResourceUrl(_meta.getUrl()));
            if (StringUtils.isNotBlank(type)) {
                IUploadResultProcessor _processor = FileUploader.get().getUploadResultProcessor(type);
                if (_processor != null) {
                    return View.jsonView(_processor.process(_meta));
                }
            }
            if (StringUtils.isNotBlank(_meta.getFilename())) {
                _meta.setFilename(WebUtils.decodeURL(_meta.getFilename()));
            }
            if (StringUtils.isNotBlank(_meta.getTitle())) {
                _meta.setTitle(WebUtils.decodeURL(_meta.getTitle()));
            }
            return WebResult.SUCCESS().data(_meta).toJSON();
        } catch (ContentTypeNotAllowException e) {
            throw new FileUploadBase.InvalidContentTypeException(e.getMessage());
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
        String _sourcePath = __doFixedResourceUrl(FileUploader.get().match(hash));
        if (StringUtils.isNotBlank(_sourcePath)) {
            return WebResult.SUCCESS().attr("matched", true).data(_sourcePath).toJSON();
        }
        return WebResult.SUCCESS().attr("matched", false).toJSON();
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
        IView _returnView = null;
        // 判断资源类型
        try {
            IFileUploader.ResourceType _resourceType = IFileUploader.ResourceType.valueOf(type.toUpperCase());
            IFileWrapper _resourceFile = FileUploader.get().resources(_resourceType, hash);
            if (_resourceFile != null) {
                if (__doBrowserCacheWarp(_resourceFile.getLastModifyTime(), FileUploader.get().getModuleCfg().getResourcesCacheTimeout())) {
                    _returnView = View.httpStatusView(HttpServletResponse.SC_NOT_MODIFIED);
                } else {
                    BinaryView _view = new BinaryView(_resourceFile.getInputStream(), _resourceFile.getContentLength());
                    _view.setContentType(_resourceFile.getContentType());
                    // 判断是否采用强制下载
                    boolean _hasAttach = WebContext.getContext().getParameters().containsKey("attach");
                    if (_hasAttach) {
                        String _fileName = StringUtils.substringAfterLast(_resourceFile.getFileName(), File.separator);
                        _view.useAttachment(_fileName);
                    }
                    _returnView = _view;
                }
            }
        } catch (IllegalArgumentException e) {
            _returnView = View.httpStatusView(HttpServletResponse.SC_BAD_REQUEST);
        } catch (ResourcesAccessException e) {
            _returnView = View.httpStatusView(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
        return _returnView == null ? HttpStatusView.NOT_FOUND : _returnView;
    }

    private String __doFixedResourceUrl(String resourceUrl) {
        if (StringUtils.isNotBlank(resourceUrl) && !StringUtils.startsWithAny(resourceUrl, new String[]{"http://", "https://"})) {
            return WebUtils.buildURL(WebContext.getRequest(), "/uploads/resources/" + resourceUrl, true);
        }
        return resourceUrl;
    }

    private boolean __doBrowserCacheWarp(long lastModifyTime, int cacheTimeout) {
        //
        long _lastModified = TimeUnit.MILLISECONDS.toSeconds(lastModifyTime);
        _lastModified = TimeUnit.SECONDS.toMillis(_lastModified);
        //
        long _expiresTime = System.currentTimeMillis() + cacheTimeout;
        String _eTag = "\"" + (lastModifyTime + cacheTimeout) + "\"";
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
            long _ifModifiedSince = _request.getDateHeader("If-Modified-Since");
            //
            if (_ifModifiedSince != -1) {
                Date _requestDate = new Date(_ifModifiedSince);
                Date _pageDate = new Date(lastModifyTime);
                if (!_requestDate.before(_pageDate)) {
                    _response.setHeader("Last-Modified", _request.getHeader("If-Modified-Since"));
                    return true;
                }
            }
        }
        return false;
    }
}
