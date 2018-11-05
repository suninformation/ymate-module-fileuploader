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
package net.ymate.module.fileuploader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ymate.framework.commons.HttpClientHelper;
import net.ymate.framework.commons.IHttpResponse;
import net.ymate.module.fileuploader.annotation.UploadResultProcessor;
import net.ymate.module.fileuploader.handle.UploadResultProcessorHandler;
import net.ymate.module.fileuploader.impl.DefaultModuleCfg;
import net.ymate.module.fileuploader.model.Attachment;
import net.ymate.module.fileuploader.repository.IAttachmentRepository;
import net.ymate.platform.cache.Caches;
import net.ymate.platform.cache.ICache;
import net.ymate.platform.core.Version;
import net.ymate.platform.core.YMP;
import net.ymate.platform.core.lang.BlurObject;
import net.ymate.platform.core.module.IModule;
import net.ymate.platform.core.module.annotation.Module;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 16/3/27 上午6:06
 * @version 1.0
 */
@Module
public class FileUploader implements IModule, IFileUploader {

    private static final Log _LOG = LogFactory.getLog(FileUploader.class);

    public static final Version VERSION = new Version(1, 0, 0, FileUploader.class.getPackage().getImplementationVersion(), Version.VersionType.Alphal);

    private static volatile IFileUploader __instance;

    private YMP __owner;

    private IFileUploaderModuleCfg __moduleCfg;

    private boolean __inited;

    private Map<String, IUploadResultProcessor> __resultProcessors;

    private ICache __matchFileHashCache;

    public static IFileUploader get() {
        if (__instance == null) {
            synchronized (VERSION) {
                if (__instance == null) {
                    __instance = YMP.get().getModule(FileUploader.class);
                }
            }
        }
        return __instance;
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public void init(YMP owner) throws Exception {
        if (!__inited) {
            //
            _LOG.info("Initializing ymate-module-fileuploader-" + VERSION);
            //
            __owner = owner;
            __moduleCfg = new DefaultModuleCfg(owner);
            __owner.getEvents().registerEvent(FileUploadEvent.class);
            __owner.registerHandler(UploadResultProcessor.class, new UploadResultProcessorHandler(this));
            //
            __matchFileHashCache = Caches.get().getCacheProvider().getCache(__moduleCfg.getCacheNamePrefix().concat("match_file_hash"));
            __resultProcessors = new HashMap<String, IUploadResultProcessor>();
            //
            __moduleCfg.getFileStorageAdapter().init(this);
            //
            __inited = true;
        }
    }

    @Override
    public boolean isInited() {
        return __inited;
    }

    @Override
    public void registerUploadResultProcessor(String name, Class<? extends IUploadResultProcessor> targetClass) throws Exception {
        if (StringUtils.isNotBlank(name) && targetClass != null) {
            __resultProcessors.put(name, targetClass.newInstance());
        }
    }

    @Override
    public IUploadResultProcessor getUploadResultProcessor(String name) {
        return __resultProcessors.get(name);
    }

    @Override
    public ICache getMatchFileHashCache() {
        return __matchFileHashCache;
    }

    @Override
    public UploadFileMeta upload(IFileWrapper fileWrapper) throws Exception {
        // 检查上传的文件ContentType是否在允许列表中
        if (!__moduleCfg.getAllowContentTypes().isEmpty() && !__moduleCfg.getAllowContentTypes().contains(fileWrapper.getContentType())) {
            throw new ContentTypeNotAllowException("Upload file content type is not allowed.");
        }
        // 非代理模式
        if (!__moduleCfg.isProxyMode()) {
            return getOwner().getBean(IAttachmentRepository.class).uploadFile(fileWrapper);
        } else {
            // 以下是代理模式采用透传
            IHttpResponse _result = HttpClientHelper.create().upload(__moduleCfg.getProxyServiceBaseUrl().concat("uploads/push"), "file", fileWrapper.toContentBody(), null);
            if (_result != null) {
                _LOG.info(_result.toString());
                if (_result.getStatusCode() != 200) {
                    JSONObject _jsonObj = JSON.parseObject(_result.getContent());
                    if (_jsonObj.containsKey("ret")) {
                        Integer _ret = _jsonObj.getInteger("ret");
                        if (_ret != null && _ret == 0 && _jsonObj.containsKey("data")) {
                            return _jsonObj.getObject("data", UploadFileMeta.class);
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String match(String hash) throws Exception {
        // 非代理模式
        if (!__moduleCfg.isProxyMode()) {
            return getOwner().getBean(IAttachmentRepository.class).matchHash(hash);
        } else {
            // 以下是代理模式采用透传
            Map<String, String> _params = new HashMap<String, String>();
            _params.put("hash", hash);
            //
            IHttpResponse _result = HttpClientHelper.create().post(__moduleCfg.getProxyServiceBaseUrl().concat("uploads/match"), _params);
            if (_result != null) {
                _LOG.info(_result.toString());
                if (_result.getStatusCode() != 200) {
                    JSONObject _jsonObj = JSON.parseObject(_result.getContent());
                    if (_jsonObj.containsKey("ret")) {
                        Integer _ret = _jsonObj.getInteger("ret");
                        Boolean _matched = _jsonObj.getBoolean("matched");
                        if (_ret != null && _ret == 0 && _matched != null && _matched && _jsonObj.containsKey("data")) {
                            return _jsonObj.getString("data");
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public IFileWrapper resources(ResourceType resourceType, String hash) throws Exception {
        // 非代理模式
        if (!__moduleCfg.isProxyMode()) {
            // 处理附加参数
            int _width = 0;
            int _height = 0;
            // 只有图片资源或THUMB(视频截图)才支持
            if (resourceType.equals(IFileUploader.ResourceType.IMAGE) || resourceType.equals(IFileUploader.ResourceType.THUMB)) {
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
            if (resourceType.equals(IFileUploader.ResourceType.THUMB)) {
                _resource = getOwner().getBean(IAttachmentRepository.class).getResource(IFileUploader.ResourceType.VIDEO, hash);
            } else {
                _resource = getOwner().getBean(IAttachmentRepository.class).getResource(resourceType, hash);
            }
            if (_resource != null) {
                File _resourceFile;
                switch (resourceType) {
                    case IMAGE:
                    case THUMB:
                        _resourceFile = __moduleCfg.getFileStorageAdapter().readThumb(resourceType, _resource.getHash(), _resource.getSourcePath(), _width, _height);
                        break;
                    default:
                        _resourceFile = __moduleCfg.getFileStorageAdapter().readFile(hash, _resource.getSourcePath());
                }
                if (_resourceFile != null && _resourceFile.exists()) {
                    final long _lastModifyTime = _resourceFile.lastModified();
                    return new IFileWrapper.NEW(_resource.getSourcePath(), _resource.getMimeType(), _resourceFile) {
                        @Override
                        public long getLastModifyTime() {
                            return _lastModifyTime;
                        }
                    };
                }
            }
        }
        return null;
    }

    @Override
    public void destroy() throws Exception {
        if (__inited) {
            __inited = false;
            //
            __moduleCfg = null;
            __owner = null;
        }
    }

    @Override
    public IFileUploaderModuleCfg getModuleCfg() {
        return __moduleCfg;
    }

    @Override
    public YMP getOwner() {
        return __owner;
    }
}
