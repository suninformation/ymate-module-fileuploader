/*
 * Copyright 2007-2021 the original author or authors.
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

import net.ymate.platform.commons.util.ParamUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 刘镇 (suninformation@163.com) on 2021/12/3 12:51 下午
 * @since 2.0.0
 */
public class FileUploaderClientConfigBuilder {

    public static FileUploaderClientConfigBuilder create() {
        return new FileUploaderClientConfigBuilder();
    }

    private String serviceBaseUrl;

    private String serviceAuthKey;

    private String uploadServiceUrl;

    private String matchServiceUrl;

    private String resourcesServiceUrl;

    private FileUploaderClientConfigBuilder() {
    }

    public String serviceBaseUrl() {
        return serviceBaseUrl;
    }

    public FileUploaderClientConfigBuilder serviceBaseUrl(String serviceBaseUrl) {
        this.serviceBaseUrl = ParamUtils.fixUrlWithProtocol(serviceBaseUrl, true);
        return this;
    }

    public String serviceAuthKey() {
        return serviceAuthKey;
    }

    public FileUploaderClientConfigBuilder serviceAuthKey(String serviceAuthKey) {
        this.serviceAuthKey = serviceAuthKey;
        return this;
    }

    public String uploadServiceUrl() {
        return uploadServiceUrl;
    }

    public FileUploaderClientConfigBuilder uploadServiceUrl(String uploadServiceUrl) {
        this.uploadServiceUrl = ParamUtils.fixUrl(uploadServiceUrl, false, false);
        return this;
    }

    public String matchServiceUrl() {
        return matchServiceUrl;
    }

    public FileUploaderClientConfigBuilder matchServiceUrl(String matchServiceUrl) {
        this.matchServiceUrl = ParamUtils.fixUrl(matchServiceUrl, false, false);
        return this;
    }

    public String resourcesServiceUrl() {
        return resourcesServiceUrl;
    }

    public FileUploaderClientConfigBuilder resourcesServiceUrl(String resourcesServiceUrl) {
        this.resourcesServiceUrl = ParamUtils.fixUrl(resourcesServiceUrl, false, false);
        return this;
    }

    public IFileUploaderClientConfig build() {
        return new IFileUploaderClientConfig() {
            @Override
            public String getServiceBaseUrl() {
                return serviceBaseUrl;
            }

            @Override
            public String getServiceAuthKey() {
                return serviceAuthKey;
            }

            @Override
            public String getUploadServiceUrl() {
                if (StringUtils.isNotBlank(uploadServiceUrl)) {
                    return buildServiceUrl(uploadServiceUrl);
                }
                return IFileUploaderClientConfig.super.getUploadServiceUrl();
            }

            @Override
            public String getMatchServiceUrl() {
                if (StringUtils.isNotBlank(matchServiceUrl)) {
                    return buildServiceUrl(matchServiceUrl);
                }
                return IFileUploaderClientConfig.super.getMatchServiceUrl();
            }

            @Override
            public String getResourcesServiceUrl(String sourcePath) {
                if (StringUtils.isNotBlank(resourcesServiceUrl)) {
                    return buildServiceUrl(String.format("%s/%s", resourcesServiceUrl, sourcePath));
                }
                return IFileUploaderClientConfig.super.getResourcesServiceUrl(sourcePath);
            }
        };
    }
}
