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
package net.ymate.module.fileuploader.impl;

import net.ymate.module.fileuploader.*;
import net.ymate.module.fileuploader.annotation.FileUploaderConf;
import net.ymate.platform.commons.util.ImageUtils;
import net.ymate.platform.core.configuration.IConfigReader;
import net.ymate.platform.core.module.IModuleConfigurer;
import net.ymate.platform.webmvc.base.Type;
import net.ymate.platform.webmvc.util.WebUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author 刘镇 (suninformation@163.com) on 16/3/30 下午5:46
 * @version 1.0
 */
public final class DefaultFileUploaderConfig implements IFileUploaderConfig {

    private boolean enabled = true;

    private String nodeId;

    private String cacheNamePrefix;

    private int cacheTimeout;

    private String servicePrefix;

    private boolean serviceEnabled;

    private String fileStoragePath;

    private String thumbStoragePath;

    private String resourcesBaseUrl;

    private IResourcesProcessor resourcesProcessor;

    private int resourcesCacheTimeout;

    private IFileStorageAdapter fileStorageAdapter;

    private IImageProcessor imageProcessor;

    private boolean proxyMode;

    private String proxyServiceBaseUrl;

    private String proxyServiceAuthKey;

    private boolean thumbCreateOnUploaded;

    private boolean allowCustomThumbSize;

    private final List<String> thumbSizeList = new ArrayList<>();

    private float thumbQuality;

    private final List<String> allowContentTypes = new ArrayList<>();

    private boolean initialized;

    public static DefaultFileUploaderConfig defaultConfig() {
        return builder().build();
    }

    public static DefaultFileUploaderConfig create(IModuleConfigurer moduleConfigurer) {
        return new DefaultFileUploaderConfig(null, moduleConfigurer);
    }

    public static DefaultFileUploaderConfig create(Class<?> mainClass, IModuleConfigurer moduleConfigurer) {
        return new DefaultFileUploaderConfig(mainClass, moduleConfigurer);
    }

    public static Builder builder() {
        return new Builder();
    }

    private DefaultFileUploaderConfig() {
    }

    private DefaultFileUploaderConfig(Class<?> mainClass, IModuleConfigurer moduleConfigurer) {
        IConfigReader configReader = moduleConfigurer.getConfigReader();
        //
        FileUploaderConf confAnn = mainClass == null ? null : mainClass.getAnnotation(FileUploaderConf.class);
        //
        enabled = configReader.getBoolean(ENABLED, confAnn == null || confAnn.enabled());
        proxyMode = configReader.getBoolean(PROXY_MODE, confAnn != null && confAnn.proxyMode());
        fileStoragePath = configReader.getString(FILE_STORAGE_PATH, confAnn == null ? null : confAnn.fileStoragePath());
        thumbStoragePath = configReader.getString(THUMB_STORAGE_PATH, confAnn == null ? null : confAnn.thumbStoragePath());
        nodeId = StringUtils.defaultIfBlank(configReader.getString(NODE_ID, confAnn != null ? confAnn.nodeId() : null), Type.Const.UNKNOWN);
        cacheNamePrefix = configReader.getString(CACHE_NAME_PREFIX, confAnn != null ? confAnn.cacheNamePrefix() : null);
        cacheTimeout = configReader.getInt(CACHE_TIMEOUT, confAnn != null ? confAnn.cacheTimeout() : 0);
        servicePrefix = StringUtils.trimToNull(configReader.getString(SERVICE_PREFIX, confAnn != null ? confAnn.servicePrefix() : null));
        serviceEnabled = configReader.getBoolean(SERVICE_ENABLED, confAnn == null || confAnn.serviceEnabled());
        resourcesBaseUrl = StringUtils.trimToNull(configReader.getString(RESOURCES_BASE_URL, confAnn != null ? confAnn.resourcesBaseUrl() : null));
        resourcesProcessor = configReader.getClassImpl(RESOURCES_PROCESSOR_CLASS, confAnn == null || confAnn.resourcesProcessorClass().equals(IResourcesProcessor.class) ? null : confAnn.resourcesProcessorClass().getName(), IResourcesProcessor.class);
        resourcesCacheTimeout = configReader.getInt(RESOURCES_CACHE_TIMEOUT, confAnn != null ? confAnn.resourcesCacheTimeout() : 0);
        fileStorageAdapter = configReader.getClassImpl(FILE_STORAGE_ADAPTER_CLASS, confAnn == null || confAnn.fileStorageAdapterClass().equals(IFileStorageAdapter.class) ? null : confAnn.fileStorageAdapterClass().getName(), IFileStorageAdapter.class);
        imageProcessor = configReader.getClassImpl(IMAGE_PROCESSOR_CLASS, confAnn == null || confAnn.imageProcessorClass().equals(IImageProcessor.class) ? null : confAnn.imageProcessorClass().getName(), IImageProcessor.class);
        proxyServiceBaseUrl = StringUtils.trimToNull(configReader.getString(PROXY_SERVICE_BASE_URL, confAnn != null ? confAnn.proxyServiceBaseUrl() : null));
        proxyServiceAuthKey = StringUtils.trimToEmpty(configReader.getString(PROXY_SERVICE_AUTH_KEY, confAnn != null ? confAnn.proxyServiceAuthKey() : null));
        //
        thumbCreateOnUploaded = configReader.getBoolean(THUMB_CREATE_ON_UPLOADED, confAnn != null && confAnn.thumbCreateOnUploaded());
        allowCustomThumbSize = configReader.getBoolean(ALLOW_CUSTOM_THUMB_SIZE, confAnn != null && confAnn.allowCustomThumbSize());
        //
        String[] tmpArr = configReader.getArray(THUMB_SIZE_LIST, confAnn != null ? confAnn.thumbSizeList() : null);
        if (tmpArr != null && tmpArr.length > 0) {
            for (String thumbSize : tmpArr) {
                String[] tmpThumb = StringUtils.split(thumbSize, '_');
                if (tmpThumb != null && tmpThumb.length == 2) {
                    if (StringUtils.isNumeric(tmpThumb[0]) && StringUtils.isNumeric(tmpThumb[1])) {
                        int width = Integer.parseInt(tmpThumb[0]);
                        int height = Integer.parseInt(tmpThumb[1]);
                        if (width >= 0 && height >= 0) {
                            if (width == height && width == 0) {
                                break;
                            }
                            thumbSizeList.add(thumbSize);
                        }
                    }
                }
            }
        }
        //
        thumbQuality = configReader.getFloat(THUMB_QUALITY, confAnn != null ? confAnn.thumbQuality() : 0);
        //
        tmpArr = configReader.getArray(ALLOW_CONTENT_TYPES, confAnn != null ? confAnn.allowContentTypes() : null);
        if (tmpArr != null && tmpArr.length > 0) {
            allowContentTypes.addAll(Arrays.asList(tmpArr));
        }
    }

    @Override
    public void initialize(IFileUploader owner) throws Exception {
        if (!initialized) {
            if (enabled) {
                if (!proxyMode) {
                    if (resourcesProcessor == null) {
                        resourcesProcessor = new DefaultResourcesProcessor();
                    }
                    if (fileStorageAdapter == null) {
                        fileStorageAdapter = new DefaultFileStorageAdapter();
                    }
                } else {
                    if (proxyServiceBaseUrl != null) {
                        proxyServiceBaseUrl = WebUtils.fixUrlWithProtocol(proxyServiceBaseUrl, true);
                    } else {
                        throw new NullArgumentException(PROXY_SERVICE_BASE_URL);
                    }
                }
                if (servicePrefix != null) {
                    servicePrefix = WebUtils.fixUrl(servicePrefix, false, false);
                }
                if (resourcesBaseUrl != null) {
                    resourcesBaseUrl = WebUtils.fixUrlWithProtocol(resourcesBaseUrl, true);
                }
                if (resourcesCacheTimeout <= 0 || resourcesCacheTimeout > ONE_YEAR_SECONDS) {
                    resourcesCacheTimeout = ONE_YEAR_SECONDS;
                }
                if (imageProcessor == null) {
                    imageProcessor = (source, dist, width, height, quality, format) -> ImageUtils.resize(source, dist, width, height, quality);
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
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (!initialized) {
            this.enabled = enabled;
        }
    }

    @Override
    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        if (!initialized) {
            this.nodeId = nodeId;
        }
    }

    @Override
    public String getCacheNamePrefix() {
        return cacheNamePrefix;
    }

    public void setCacheNamePrefix(String cacheNamePrefix) {
        if (!initialized) {
            this.cacheNamePrefix = cacheNamePrefix;
        }
    }

    @Override
    public int getCacheTimeout() {
        return cacheTimeout;
    }

    public void setCacheTimeout(int cacheTimeout) {
        if (!initialized) {
            this.cacheTimeout = cacheTimeout;
        }
    }

    @Override
    public String getServicePrefix() {
        return servicePrefix;
    }

    public void setServicePrefix(String servicePrefix) {
        if (!initialized) {
            this.servicePrefix = servicePrefix;
        }
    }

    @Override
    public boolean isServiceEnabled() {
        return serviceEnabled;
    }

    public void setServiceEnabled(boolean serviceEnabled) {
        if (!initialized) {
            this.serviceEnabled = serviceEnabled;
        }
    }

    @Override
    public String getFileStoragePath() {
        return fileStoragePath;
    }

    public void setFileStoragePath(String fileStoragePath) {
        if (!initialized) {
            this.fileStoragePath = fileStoragePath;
        }
    }

    @Override
    public String getThumbStoragePath() {
        return thumbStoragePath;
    }

    public void setThumbStoragePath(String thumbStoragePath) {
        if (!initialized) {
            this.thumbStoragePath = thumbStoragePath;
        }
    }

    @Override
    public String getResourcesBaseUrl() {
        return resourcesBaseUrl;
    }

    public void setResourcesBaseUrl(String resourcesBaseUrl) {
        if (!initialized) {
            this.resourcesBaseUrl = resourcesBaseUrl;
        }
    }

    @Override
    public IResourcesProcessor getResourcesProcessor() {
        return resourcesProcessor;
    }

    public void setResourcesProcessor(IResourcesProcessor resourcesProcessor) {
        if (!initialized) {
            this.resourcesProcessor = resourcesProcessor;
        }
    }

    @Override
    public int getResourcesCacheTimeout() {
        return resourcesCacheTimeout;
    }

    public void setResourcesCacheTimeout(int resourcesCacheTimeout) {
        if (!initialized) {
            this.resourcesCacheTimeout = resourcesCacheTimeout;
        }
    }

    @Override
    public IFileStorageAdapter getFileStorageAdapter() {
        return fileStorageAdapter;
    }

    public void setFileStorageAdapter(IFileStorageAdapter fileStorageAdapter) {
        if (!initialized) {
            this.fileStorageAdapter = fileStorageAdapter;
        }
    }

    @Override
    public IImageProcessor getImageProcessor() {
        return imageProcessor;
    }

    public void setImageProcessor(IImageProcessor imageProcessor) {
        if (!initialized) {
            this.imageProcessor = imageProcessor;
        }
    }

    @Override
    public boolean isProxyMode() {
        return proxyMode;
    }

    public void setProxyMode(boolean proxyMode) {
        if (!initialized) {
            this.proxyMode = proxyMode;
        }
    }

    @Override
    public String getProxyServiceBaseUrl() {
        return proxyServiceBaseUrl;
    }

    public void setProxyServiceBaseUrl(String proxyServiceBaseUrl) {
        if (!initialized) {
            this.proxyServiceBaseUrl = proxyServiceBaseUrl;
        }
    }

    @Override
    public String getProxyServiceAuthKey() {
        return proxyServiceAuthKey;
    }

    public void setProxyServiceAuthKey(String proxyServiceAuthKey) {
        if (!initialized) {
            this.proxyServiceAuthKey = proxyServiceAuthKey;
        }
    }

    @Override
    public boolean isThumbCreateOnUploaded() {
        return thumbCreateOnUploaded;
    }

    public void setThumbCreateOnUploaded(boolean thumbCreateOnUploaded) {
        if (!initialized) {
            this.thumbCreateOnUploaded = thumbCreateOnUploaded;
        }
    }

    @Override
    public boolean isAllowCustomThumbSize() {
        return allowCustomThumbSize;
    }

    public void setAllowCustomThumbSize(boolean allowCustomThumbSize) {
        if (!initialized) {
            this.allowCustomThumbSize = allowCustomThumbSize;
        }
    }

    @Override
    public List<String> getThumbSizeList() {
        return thumbSizeList;
    }

    public void addThumbSize(String thumbSize) {
        if (!initialized && StringUtils.isNotBlank(thumbSize)) {
            this.thumbSizeList.add(thumbSize);
        }
    }

    public void addThumbSizes(Collection<String> thumbSizes) {
        if (!initialized && thumbSizes != null) {
            this.thumbSizeList.addAll(thumbSizes);
        }
    }

    @Override
    public float getThumbQuality() {
        return thumbQuality;
    }

    public void setThumbQuality(float thumbQuality) {
        this.thumbQuality = thumbQuality;
    }

    @Override
    public List<String> getAllowContentTypes() {
        return allowContentTypes;
    }

    public void addAllowContentType(String allowContentType) {
        if (!initialized && StringUtils.isNotBlank(allowContentType)) {
            this.allowContentTypes.add(allowContentType);
        }
    }

    public void addAllowContentTypes(Collection<String> allowContentTypes) {
        if (!initialized && allowContentTypes != null) {
            this.allowContentTypes.addAll(allowContentTypes);
        }
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public static final class Builder {

        private final DefaultFileUploaderConfig config = new DefaultFileUploaderConfig();

        private Builder() {
        }

        public Builder enabled(boolean enabled) {
            config.setEnabled(enabled);
            return this;
        }

        public Builder nodeId(String nodeId) {
            config.setNodeId(nodeId);
            return this;
        }

        public Builder cacheNamePrefix(String cacheNamePrefix) {
            config.setCacheNamePrefix(cacheNamePrefix);
            return this;
        }

        public Builder cacheTimeout(int cacheTimeout) {
            config.setCacheTimeout(cacheTimeout);
            return this;
        }

        public Builder servicePrefix(String servicePrefix) {
            config.setServicePrefix(servicePrefix);
            return this;
        }

        public Builder serviceEnabled(boolean serviceEnabled) {
            config.setServiceEnabled(serviceEnabled);
            return this;
        }

        public Builder fileStoragePath(String fileStoragePath) {
            config.setFileStoragePath(fileStoragePath);
            return this;
        }

        public Builder thumbStoragePath(String thumbStoragePath) {
            config.setThumbStoragePath(thumbStoragePath);
            return this;
        }

        public Builder resourcesBaseUrl(String resourcesBaseUrl) {
            config.setResourcesBaseUrl(resourcesBaseUrl);
            return this;
        }

        public Builder resourcesProcessor(IResourcesProcessor resourcesProcessor) {
            config.setResourcesProcessor(resourcesProcessor);
            return this;
        }

        public Builder resourcesCacheTimeout(int resourcesCacheTimeout) {
            config.setResourcesCacheTimeout(resourcesCacheTimeout);
            return this;
        }

        public Builder fileStorageAdapter(IFileStorageAdapter fileStorageAdapter) {
            config.setFileStorageAdapter(fileStorageAdapter);
            return this;
        }

        public Builder imageProcessor(IImageProcessor imageProcessor) {
            config.setImageProcessor(imageProcessor);
            return this;
        }

        public Builder proxyMode(boolean proxyMode) {
            config.setProxyMode(proxyMode);
            return this;
        }

        public Builder proxyServiceBaseUrl(String proxyServiceBaseUrl) {
            config.setProxyServiceBaseUrl(proxyServiceBaseUrl);
            return this;
        }

        public Builder proxyServiceAuthKey(String proxyServiceAuthKey) {
            config.setProxyServiceAuthKey(proxyServiceAuthKey);
            return this;
        }

        public Builder thumbCreateOnUploaded(boolean thumbCreateOnUploaded) {
            config.setThumbCreateOnUploaded(thumbCreateOnUploaded);
            return this;
        }

        public Builder allowCustomThumbSize(boolean allowCustomThumbSize) {
            config.setAllowCustomThumbSize(allowCustomThumbSize);
            return this;
        }

        public Builder thumbSizes(Collection<String> thumbSizes) {
            config.addThumbSizes(thumbSizes);
            return this;
        }

        public Builder thumbQuality(float thumbQuality) {
            config.setThumbQuality(thumbQuality);
            return this;
        }

        public Builder allowContentTypes(Collection<String> allowContentTypes) {
            config.addAllowContentTypes(allowContentTypes);
            return this;
        }

        public DefaultFileUploaderConfig build() {
            return config;
        }
    }
}