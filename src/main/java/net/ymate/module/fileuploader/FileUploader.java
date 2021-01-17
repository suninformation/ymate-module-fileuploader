/*
 * Copyright 2007-2020 the original author or authors.
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

import net.ymate.module.fileuploader.controller.UploadController;
import net.ymate.module.fileuploader.impl.DefaultFileUploaderConfig;
import net.ymate.platform.cache.Caches;
import net.ymate.platform.cache.ICache;
import net.ymate.platform.commons.http.HttpRequestBuilder;
import net.ymate.platform.commons.http.IHttpResponse;
import net.ymate.platform.commons.json.JsonWrapper;
import net.ymate.platform.commons.lang.BlurObject;
import net.ymate.platform.commons.util.ClassUtils;
import net.ymate.platform.commons.util.RuntimeUtils;
import net.ymate.platform.core.*;
import net.ymate.platform.core.module.IModule;
import net.ymate.platform.core.module.IModuleConfigurer;
import net.ymate.platform.core.module.impl.DefaultModuleConfigurer;
import net.ymate.platform.webmvc.IWebMvc;
import net.ymate.platform.webmvc.IWebResult;
import net.ymate.platform.webmvc.WebMVC;
import net.ymate.platform.webmvc.util.WebResult;
import net.ymate.platform.webmvc.util.WebUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 2016/03/27 06:06
 * @since 1.0
 */
public final class FileUploader implements IModule, IFileUploader {

    private static final Log LOG = LogFactory.getLog(FileUploader.class);

    private static final Map<String, IUploadResultProcessor> RESULT_PROCESSOR_MAP = new HashMap<>();

    static {
        try {
            ClassUtils.getExtensionLoader(IUploadResultProcessor.class, true)
                    .getExtensions()
                    .forEach(uploadResultProcessor -> RESULT_PROCESSOR_MAP.put(uploadResultProcessor.getName(), uploadResultProcessor));
        } catch (Exception e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn(StringUtils.EMPTY, RuntimeUtils.unwrapThrow(e));
            }
        }
    }

    private static volatile IFileUploader instance;

    private IApplication owner;

    private IFileUploaderConfig config;

    private ICache fileHashCache;

    private boolean initialized;

    public static IFileUploader get() {
        IFileUploader inst = instance;
        if (inst == null) {
            synchronized (FileUploader.class) {
                inst = instance;
                if (inst == null) {
                    instance = inst = YMP.get().getModuleManager().getModule(FileUploader.class);
                }
            }
        }
        return inst;
    }

    public FileUploader() {
    }

    public FileUploader(IFileUploaderConfig config) {
        this.config = config;
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public void initialize(IApplication owner) throws Exception {
        if (!initialized) {
            //
            YMP.showVersion("Initializing ymate-module-fileuploader-${version}", new Version(2, 0, 0, FileUploader.class, Version.VersionType.Release));
            //
            this.owner = owner;
            if (config == null) {
                IApplicationConfigureFactory configureFactory = owner.getConfigureFactory();
                if (configureFactory != null) {
                    IApplicationConfigurer configurer = configureFactory.getConfigurer();
                    IModuleConfigurer moduleConfigurer = configurer == null ? null : configurer.getModuleConfigurer(MODULE_NAME);
                    if (moduleConfigurer != null) {
                        config = DefaultFileUploaderConfig.create(configureFactory.getMainClass(), moduleConfigurer);
                    } else {
                        config = DefaultFileUploaderConfig.create(configureFactory.getMainClass(), DefaultModuleConfigurer.createEmpty(MODULE_NAME));
                    }
                }
                if (config == null) {
                    config = DefaultFileUploaderConfig.defaultConfig();
                }
            }
            if (!config.isInitialized()) {
                config.initialize(this);
            }
            if (config.isEnabled()) {
                owner.getEvents().registerEvent(FileUploadEvent.class);
                //
                String cacheName = String.format("%s%s_file_hash", StringUtils.trimToEmpty(config.getCacheNamePrefix()), MODULE_NAME);
                fileHashCache = owner.getModuleManager().getModule(Caches.class).getConfig().getCacheProvider().getCache(cacheName);
                //
                if (!config.isProxyMode()) {
                    config.getFileStorageAdapter().initialize(this);
                    config.getResourcesProcessor().initialize(this);
                }
                if (config.isServiceEnabled()) {
                    IWebMvc module = owner.getModuleManager().getModule(WebMVC.class);
                    if (module != null) {
                        module.registerController(config.getServicePrefix(), UploadController.class);
                    }
                }
            }
            initialized = true;
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void close() throws Exception {
        if (initialized) {
            initialized = false;
            //
            config = null;
            owner = null;
        }
    }

    @Override
    public IApplication getOwner() {
        return owner;
    }

    @Override
    public IFileUploaderConfig getConfig() {
        return config;
    }

    @Override
    public void registerUploadResultProcessor(Class<? extends IUploadResultProcessor> targetClass) throws Exception {
        if (targetClass != null) {
            IUploadResultProcessor resultProcessor = targetClass.newInstance();
            if (StringUtils.isNotBlank(resultProcessor.getName())) {
                RESULT_PROCESSOR_MAP.put(resultProcessor.getName(), resultProcessor);
            }
        }
    }

    @Override
    public IUploadResultProcessor getUploadResultProcessor(String name) {
        return RESULT_PROCESSOR_MAP.get(name);
    }

    @Override
    public ICache getFileHashCache() {
        return fileHashCache;
    }

    protected String doBuildResourceUrl(String hash, ResourceType type, String sourcePath) {
        String resourcesBaseUrl = config.getResourcesBaseUrl();
        if (resourcesBaseUrl != null) {
            sourcePath = StringUtils.replaceChars(sourcePath, File.separatorChar, '/');
            if (StringUtils.startsWith(sourcePath, IResourcesProcessor.URL_SEPARATOR)) {
                sourcePath = StringUtils.substringAfter(sourcePath, IResourcesProcessor.URL_SEPARATOR);
            }
            return resourcesBaseUrl + sourcePath;
        }
        return String.format("%s/%s", type.name().toLowerCase(), hash);
    }

    @Override
    public UploadFileMeta upload(IFileWrapper fileWrapper) throws Exception {
        // 检查上传的文件ContentType是否在允许列表中
        if (!config.getAllowContentTypes().isEmpty() && !config.getAllowContentTypes().contains(fileWrapper.getContentType())) {
            throw new ContentTypeNotAllowException("Upload file content type is not allowed.");
        }
        UploadFileMeta returnValue = null;
        IResourcesProcessor resourcesProcessor = config.getResourcesProcessor();
        // 非代理模式
        if (!config.isProxyMode()) {
            returnValue = resourcesProcessor.upload(fileWrapper);
        } else {
            // 以下是代理模式采用透传
            boolean useDefault = resourcesProcessor == null;
            if (!useDefault) {
                try {
                    returnValue = resourcesProcessor.proxyUpload(fileWrapper);
                } catch (UnsupportedOperationException e) {
                    useDefault = true;
                }
            }
            if (useDefault) {
                String serviceUrl = String.format("%s%s%s", config.getProxyServiceBaseUrl(), WebUtils.fixUrl(config.getServicePrefix(), false, true), "uploads/push?format=json");
                try (IHttpResponse httpResponse = HttpRequestBuilder.create(serviceUrl).addBody("file", fileWrapper.toContentBody()).build().post()) {
                    if (httpResponse != null) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(httpResponse.toString());
                        }
                        if (httpResponse.getStatusCode() == HttpServletResponse.SC_OK) {
                            IWebResult<?> result = WebResult.builder().fromJson(httpResponse.getContent()).build();
                            if (result.isSuccess()) {
                                JsonWrapper data = JsonWrapper.toJson(result.data());
                                if (data != null && data.isJsonObject()) {
                                    returnValue = JsonWrapper.deserialize(data.getAsJsonObject().toString(), UploadFileMeta.class);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (returnValue != null) {
            returnValue.setUrl(doBuildResourceUrl(returnValue.getHash(), returnValue.getType(), returnValue.getSourcePath()));
        }
        return returnValue;
    }

    @Override
    public UploadFileMeta match(String hash) throws Exception {
        UploadFileMeta returnValue = null;
        IResourcesProcessor resourcesProcessor = config.getResourcesProcessor();
        // 非代理模式
        if (!config.isProxyMode()) {
            returnValue = resourcesProcessor.matchHash(hash);
        } else {
            // 以下是代理模式采用透传
            boolean useDefault = resourcesProcessor == null;
            if (!useDefault) {
                try {
                    returnValue = resourcesProcessor.proxyMatchHash(hash);
                } catch (UnsupportedOperationException e) {
                    useDefault = true;
                }
            }
            if (useDefault) {
                String serviceUrl = String.format("%s%s%s", config.getProxyServiceBaseUrl(), WebUtils.fixUrl(config.getServicePrefix(), false, true), "uploads/match?format=json");
                try (IHttpResponse httpResponse = HttpRequestBuilder.create(serviceUrl).addParam("hash", hash).build().post()) {
                    if (httpResponse != null) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(httpResponse.toString());
                        }
                        if (httpResponse.getStatusCode() == HttpServletResponse.SC_OK) {
                            IWebResult<?> result = WebResult.builder().fromJson(httpResponse.getContent()).build();
                            if (result.isSuccess() && BlurObject.bind(result.attr("matched")).toBooleanValue()) {
                                JsonWrapper data = JsonWrapper.toJson(result.data());
                                if (data != null && data.isJsonObject()) {
                                    returnValue = JsonWrapper.deserialize(data.getAsJsonObject().toString(), UploadFileMeta.class);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (returnValue != null) {
            returnValue.setUrl(doBuildResourceUrl(hash, returnValue.getType(), returnValue.getSourcePath()));
        }
        return returnValue;
    }

    @Override
    public IFileWrapper resources(ResourceType resourceType, String hash) throws Exception {
        // 非代理模式
        if (!config.isProxyMode()) {
            // 处理附加参数
            int width = 0;
            int height = 0;
            // 只有图片资源或THUMB(视频截图)才支持
            if (resourceType.isImage()) {
                String[] params = StringUtils.split(hash, '_');
                hash = params[0];
                if (params.length > 1) {
                    width = BlurObject.bind(params[1]).toIntValue();
                    if (params.length > 2) {
                        height = BlurObject.bind(params[2]).toIntValue();
                    }
                }
            }
            //
            IFileStorageAdapter storageAdapter = config.getFileStorageAdapter();
            UploadFileMeta resourceFileMeta;
            if (resourceType.equals(ResourceType.THUMB)) {
                resourceFileMeta = config.getResourcesProcessor().getResource(ResourceType.VIDEO, hash);
            } else {
                resourceFileMeta = config.getResourcesProcessor().getResource(resourceType, hash);
            }
            if (resourceFileMeta != null) {
                File resourceFile;
                switch (resourceType) {
                    case IMAGE:
                    case THUMB:
                        resourceFile = storageAdapter.readThumb(resourceType, resourceFileMeta.getHash(), resourceFileMeta.getSourcePath(), width, height);
                        break;
                    default:
                        resourceFile = storageAdapter.readFile(hash, resourceFileMeta.getSourcePath());
                }
                if (resourceFile != null && resourceFile.exists()) {
                    return new IFileWrapper.Default(resourceFileMeta.getSourcePath(), resourceFileMeta.getMimeType(), resourceFile);
                }
            }
        }
        return null;
    }
}
