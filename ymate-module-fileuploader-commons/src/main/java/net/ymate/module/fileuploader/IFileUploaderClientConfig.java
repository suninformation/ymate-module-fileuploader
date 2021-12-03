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

/**
 * @author 刘镇 (suninformation@163.com) on 2021/12/3 12:48 下午
 * @since 2.0.0
 */
public interface IFileUploaderClientConfig {

    String DEFAULT_UPLOAD_SERVICE_URL = "uploads/push";

    String DEFAULT_MATCH_SERVICE_URL = "uploads/match";

    String DEFAULT_RESOURCES_SERVICE_URL = "uploads/resources";

    /**
     * 获取服务基准URL路径
     *
     * @return 返回服务基准URL路径
     */
    String getServiceBaseUrl();

    /**
     * 获取客户端与服务端之间通讯请求参数签名密钥
     *
     * @return 返回客户端与服务端之间通讯请求参数签名密钥
     */
    String getServiceAuthKey();

    /**
     * 基于服务基准URL路径构建请求地址
     *
     * @param serviceUrl 相对请求地址
     * @return 返回完整的请求URL地址
     */
    default String buildServiceUrl(String serviceUrl) {
        return String.format("%s%s", getServiceBaseUrl(), serviceUrl);
    }

    /**
     * 获取文件上传服务URL地址
     *
     * @return 返回完整的文件上传服务URL地址
     */
    default String getUploadServiceUrl() {
        return buildServiceUrl(DEFAULT_UPLOAD_SERVICE_URL);
    }

    /**
     * 获取文件匹配服务URL地址
     *
     * @return 返回完整的文件匹配服务URL地址
     */
    default String getMatchServiceUrl() {
        return buildServiceUrl(DEFAULT_MATCH_SERVICE_URL);
    }

    /**
     * 获取文件下载服务URL地址
     *
     * @param sourcePath 源文件路径
     * @return 返回完整的文件下载服务URL地址
     */
    default String getResourcesServiceUrl(String sourcePath) {
        return buildServiceUrl(String.format("%s/%s", DEFAULT_RESOURCES_SERVICE_URL, ParamUtils.fixUrl(sourcePath, false, false)));
    }
}
