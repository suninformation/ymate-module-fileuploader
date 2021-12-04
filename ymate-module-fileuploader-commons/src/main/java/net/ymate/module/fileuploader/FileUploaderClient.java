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

import net.ymate.platform.commons.exception.ServiceException;
import net.ymate.platform.commons.exception.ServiceStatusException;
import net.ymate.platform.commons.http.HttpClientHelper;
import net.ymate.platform.commons.http.HttpRequestBuilder;
import net.ymate.platform.commons.http.IHttpResponse;
import net.ymate.platform.commons.json.IJsonNodeWrapper;
import net.ymate.platform.commons.json.IJsonObjectWrapper;
import net.ymate.platform.commons.json.JsonWrapper;
import net.ymate.platform.commons.util.ClassUtils;
import net.ymate.platform.commons.util.ParamUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 2021/12/3 12:35 下午
 * @since 2.0.0
 */
public class FileUploaderClient implements IFileUploaderClient {

    private static final Log LOG = LogFactory.getLog(FileUploaderClient.class);

    public static IFileUploaderClient create() {
        return create(null);
    }

    public static IFileUploaderClient create(IFileUploaderClientConfig clientConfig) {
        IFileUploaderClient client = ClassUtils.loadClass(IFileUploaderClient.class);
        if (client == null) {
            client = new FileUploaderClient();
        }
        if (clientConfig != null) {
            client.initialize(clientConfig);
        }
        return client;
    }

    private IFileUploaderClientConfig clientConfig;

    private boolean initialized;

    private FileUploaderClient() {
    }

    @Override
    public void initialize(IFileUploaderClientConfig clientConfig) {
        if (!this.initialized) {
            this.clientConfig = clientConfig;
            this.initialized = true;
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public IFileUploaderClientConfig getClientConfig() {
        return clientConfig;
    }

    @Override
    public UploadFileMeta upload(IFileWrapper fileWrapper) throws Exception {
        String hash = DigestUtils.md5Hex(fileWrapper.getInputStream());
        UploadFileMeta fileMeta = match(hash);
        if (fileMeta == null) {
            try (IHttpResponse response = HttpRequestBuilder.create(clientConfig.getUploadServiceUrl())
                    .addParams(doBuildRequestParams())
                    .addBody("file", fileWrapper.toContentBody()).build().post()) {
                if (response != null) {
                    if (response.getStatusCode() == HttpClientHelper.HTTP_STATUS_CODE_SUCCESS) {
                        JsonWrapper jsonWrapper = JsonWrapper.fromJson(response.getContent());
                        if (jsonWrapper != null && jsonWrapper.isJsonObject()) {
                            IJsonObjectWrapper objectWrapper = jsonWrapper.getAsJsonObject();
                            Integer ret = objectWrapper.getAsInteger("ret");
                            if (ret != null) {
                                if (ret == 0) {
                                    IJsonNodeWrapper data = objectWrapper.get("data");
                                    if (data != null && data.isJsonObject()) {
                                        fileMeta = JsonWrapper.deserialize(data.toString(), UploadFileMeta.class);
                                    }
                                } else {
                                    throw new ServiceException(ret, objectWrapper.getString("msg"));
                                }
                            }
                        }
                    } else {
                        throw new ServiceStatusException(response.getStatusCode(), response.getContent());
                    }
                }
            }
        }
        return fileMeta;
    }

    @Override
    public UploadFileMeta upload(File file) throws Exception {
        return upload(new IFileWrapper.Default(file));
    }

    private Map<String, String> doBuildRequestParams() {
        return doBuildRequestParams(null);
    }

    private Map<String, String> doBuildRequestParams(String hash) {
        Map<String, String> requestParams = new HashMap<>(4);
        requestParams.put("format", "json");
        if (StringUtils.isNotBlank(hash)) {
            requestParams.put("hash", hash);
        }
        if (StringUtils.isNotBlank(clientConfig.getServiceAuthKey())) {
            requestParams.put("nonce", ParamUtils.createNonceStr());
            requestParams.put("sign", ParamUtils.createSignature(requestParams, false, true, clientConfig.getServiceAuthKey()));
        }
        return requestParams;
    }

    @Override
    public UploadFileMeta match(String hash) throws Exception {
        UploadFileMeta fileMeta = null;
        try (IHttpResponse response = HttpRequestBuilder.create(clientConfig.getMatchServiceUrl())
                .addParams(doBuildRequestParams(hash)).build().post()) {
            if (response != null) {
                if (response.getStatusCode() == HttpClientHelper.HTTP_STATUS_CODE_SUCCESS) {
                    JsonWrapper jsonWrapper = JsonWrapper.fromJson(response.getContent());
                    if (jsonWrapper != null && jsonWrapper.isJsonObject()) {
                        IJsonObjectWrapper objectWrapper = jsonWrapper.getAsJsonObject();
                        Integer ret = objectWrapper.getAsInteger("ret");
                        if (ret != null) {
                            if (ret == 0) {
                                if (objectWrapper.getBoolean("matched")) {
                                    IJsonNodeWrapper data = objectWrapper.get("data");
                                    if (data != null && data.isJsonObject()) {
                                        fileMeta = JsonWrapper.deserialize(data.toString(), UploadFileMeta.class);
                                    }
                                }
                            } else {
                                throw new ServiceException(ret, objectWrapper.getString("msg"));
                            }
                        }
                    }
                } else {
                    throw new ServiceStatusException(response.getStatusCode(), response.getContent());
                }
            }
        }
        return fileMeta;
    }

    @Override
    public IFileWrapper resources(ResourceType resourceType, String hash) throws Exception {
        return resources(String.format("%s/%s", resourceType.name().toLowerCase(), hash));
    }

    @Override
    public IFileWrapper resources(String sourcePath) throws Exception {
        IFileWrapper returnValue = null;
        String url = clientConfig.getResourcesServiceUrl(sourcePath);
        try (IHttpResponse response = HttpRequestBuilder.create(url).download(true).build().get()) {
            if (response.getStatusCode() == HttpClientHelper.HTTP_STATUS_CODE_SUCCESS) {
                net.ymate.platform.commons.http.IFileWrapper fileWrapper = response.getFileWrapper();
                returnValue = new IFileWrapper.Default(fileWrapper.getFileName(), fileWrapper.getContentType(), fileWrapper.getFile());
            } else if (LOG.isWarnEnabled()) {
                LOG.warn(String.format("Request for resource '%s' failed: %s", url, StringUtils.defaultIfBlank(response.getReasonPhrase(), response.getContent())));
            }
        }
        return returnValue;
    }
}
