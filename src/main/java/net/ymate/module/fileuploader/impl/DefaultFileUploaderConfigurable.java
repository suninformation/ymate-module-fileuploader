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
import net.ymate.platform.core.module.IModuleConfigurer;
import net.ymate.platform.core.module.impl.DefaultModuleConfigurable;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * @author 刘镇 (suninformation@163.com) on 2020/08/06 22:21
 * @since 2.0.0
 */
public final class DefaultFileUploaderConfigurable extends DefaultModuleConfigurable {

    public static Builder builder() {
        return new Builder();
    }

    private DefaultFileUploaderConfigurable() {
        super(IFileUploader.MODULE_NAME);
    }

    public static final class Builder {

        private final DefaultFileUploaderConfigurable configurable = new DefaultFileUploaderConfigurable();

        private Builder() {
        }

        public Builder enabled(boolean enabled) {
            configurable.addConfig(IFileUploaderConfig.ENABLED, String.valueOf(enabled));
            return this;
        }

        public Builder nodeId(String nodeId) {
            configurable.addConfig(IFileUploaderConfig.NODE_ID, nodeId);
            return this;
        }

        public Builder cacheNamePrefix(String cacheNamePrefix) {
            configurable.addConfig(IFileUploaderConfig.CACHE_NAME_PREFIX, cacheNamePrefix);
            return this;
        }

        public Builder cacheTimeout(int cacheTimeout) {
            configurable.addConfig(IFileUploaderConfig.CACHE_TIMEOUT, String.valueOf(cacheTimeout));
            return this;
        }

        public Builder servicePrefix(String servicePrefix) {
            configurable.addConfig(IFileUploaderConfig.SERVICE_PREFIX, servicePrefix);
            return this;
        }

        public Builder serviceEnabled(boolean serviceEnabled) {
            configurable.addConfig(IFileUploaderConfig.SERVICE_ENABLED, String.valueOf(serviceEnabled));
            return this;
        }

        public Builder fileStoragePath(String fileStoragePath) {
            configurable.addConfig(IFileUploaderConfig.FILE_STORAGE_PATH, fileStoragePath);
            return this;
        }

        public Builder resourcesBaseUrl(String resourcesBaseUrl) {
            configurable.addConfig(IFileUploaderConfig.RESOURCES_BASE_URL, resourcesBaseUrl);
            return this;
        }

        public Builder resourcesProcessorClass(Class<? extends IResourcesProcessor> resourcesProcessorClass) {
            configurable.addConfig(IFileUploaderConfig.RESOURCES_PROCESSOR_CLASS, resourcesProcessorClass.getName());
            return this;
        }

        public Builder resourcesCacheTimeout(int resourcesCacheTimeout) {
            configurable.addConfig(IFileUploaderConfig.RESOURCES_CACHE_TIMEOUT, String.valueOf(resourcesCacheTimeout));
            return this;
        }

        public Builder fileStorageAdapterClass(Class<? extends IFileStorageAdapter> fileStorageAdapterClass) {
            configurable.addConfig(IFileUploaderConfig.FILE_STORAGE_ADAPTER_CLASS, fileStorageAdapterClass.getName());
            return this;
        }

        public Builder imageProcessorClass(Class<? extends IImageProcessor> imageProcessorClass) {
            configurable.addConfig(IFileUploaderConfig.IMAGE_PROCESSOR_CLASS, imageProcessorClass.getName());
            return this;
        }

        public Builder proxyMode(boolean proxyMode) {
            configurable.addConfig(IFileUploaderConfig.PROXY_MODE, String.valueOf(proxyMode));
            return this;
        }

        public Builder proxyServiceBaseUrl(String proxyServiceBaseUrl) {
            configurable.addConfig(IFileUploaderConfig.PROXY_SERVICE_BASE_URL, proxyServiceBaseUrl);
            return this;
        }

        public Builder proxyServiceAuthKey(String proxyServiceAuthKey) {
            configurable.addConfig(IFileUploaderConfig.PROXY_SERVICE_AUTH_KEY, String.valueOf(proxyServiceAuthKey));
            return this;
        }

        public Builder allowCustomThumbSize(boolean allowCustomThumbSize) {
            configurable.addConfig(IFileUploaderConfig.ALLOW_CUSTOM_THUMB_SIZE, String.valueOf(allowCustomThumbSize));
            return this;
        }

        public Builder thumbQuality(float thumbQuality) {
            configurable.addConfig(IFileUploaderConfig.THUMB_QUALITY, String.valueOf(thumbQuality));
            return this;
        }

        public Builder thumbSizeList(Collection<String> thumbSizeList) {
            configurable.addConfig(IFileUploaderConfig.THUMB_SIZE_LIST, StringUtils.join(thumbSizeList, '|'));
            return this;
        }

        public Builder allowContentTypes(Collection<String> allowContentTypes) {
            configurable.addConfig(IFileUploaderConfig.ALLOW_CONTENT_TYPES, StringUtils.join(allowContentTypes, '|'));
            return this;
        }

        public IModuleConfigurer build() {
            return configurable.toModuleConfigurer();
        }
    }
}