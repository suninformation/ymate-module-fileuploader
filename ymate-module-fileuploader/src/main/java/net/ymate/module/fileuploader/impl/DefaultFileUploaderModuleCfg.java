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
package net.ymate.module.fileuploader.impl;

import net.ymate.module.fileuploader.*;
import net.ymate.platform.core.YMP;
import net.ymate.platform.core.support.IConfigReader;
import net.ymate.platform.core.support.impl.MapSafeConfigReader;
import net.ymate.platform.core.util.RuntimeUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 刘镇 (suninformation@163.com) on 16/3/30 下午5:46
 * @version 1.0
 */
public class DefaultFileUploaderModuleCfg implements IFileUploaderModuleCfg {

    private String __nodeId;

    private String __cacheNamePrefix;

    private int __cacheTimeout;

    private File __fileStoragePath;

    private String __resourcesBaseUrl;

    private IResourcesProcessor __resourcesProcessor;

    private int __resourcesCacheTimeout;

    private IFileStorageAdapter __fileStorageAdapter;

    private IImageFileProcessor __imageFileProcessor;

    private boolean __proxyMode;

    private String __proxyServiceBaseUrl;

    private String __proxyServiceAuthKey;

    private boolean __allowCustomThumbSize;

    private List<String> __thumbSizeList;

    private float __thumbQuality;

    private List<String> __allowContentTypes;

    public DefaultFileUploaderModuleCfg(YMP owner) {
        IConfigReader _moduleCfg = MapSafeConfigReader.bind(owner.getConfig().getModuleConfigs(IFileUploader.MODULE_NAME));
        //
        __proxyMode = _moduleCfg.getBoolean(PROXY_MODE);
        //
        __fileStoragePath = new File(RuntimeUtils.replaceEnvVariable(_moduleCfg.getString(FILE_STORAGE_PATH, "${root}/upload_files")));
        if (!__proxyMode) {
            if (!__fileStoragePath.isAbsolute() ||
                    !__fileStoragePath.exists() ||
                    !__fileStoragePath.isDirectory() ||
                    !__fileStoragePath.canRead() || !__fileStoragePath.canWrite()) {
                throw new IllegalArgumentException("The parameter file_storage_path is invalid or is not a directory");
            }
        }
        //
        __nodeId = _moduleCfg.getString(NODE_ID, "unknown");
        //
        __cacheNamePrefix = _moduleCfg.getString(CACHE_NAME_PREFIX);
        //
        __cacheTimeout = _moduleCfg.getInt(CACHE_TIMEOUT);
        //
        __resourcesBaseUrl = StringUtils.trimToNull(_moduleCfg.getString(RESOURCES_BASE_URL));
        if (__resourcesBaseUrl != null) {
            if (!StringUtils.startsWithIgnoreCase(__resourcesBaseUrl, "http://") &&
                    !StringUtils.startsWithIgnoreCase(__resourcesBaseUrl, "https://")) {
                throw new IllegalArgumentException("The parameter resources_base_url is invalid");
            } else if (!StringUtils.endsWith(__resourcesBaseUrl, "/")) {
                __resourcesBaseUrl = __resourcesBaseUrl + "/";
            }
        }
        //
        __resourcesProcessor = _moduleCfg.getClassImpl(RESOURCES_PROCESSOR_CLASS, IResourcesProcessor.class);
        if (!__proxyMode && __resourcesProcessor == null) {
            throw new NullArgumentException(RESOURCES_PROCESSOR_CLASS);
        }
        //
        __resourcesCacheTimeout = _moduleCfg.getInt(RESOURCES_CACHE_TIMEOUT);
        int _oneYear = 60 * 60 * 24 * 365;
        if (__resourcesCacheTimeout <= 0 || __resourcesCacheTimeout > _oneYear) {
            __resourcesCacheTimeout = _oneYear;
        }
        //
        __fileStorageAdapter = _moduleCfg.getClassImpl(FILE_STORAGE_ADAPTER_CLASS, IFileStorageAdapter.class);
        if (!__proxyMode && __fileStorageAdapter == null) {
            __fileStorageAdapter = new DefaultFileStorageAdapter();
        }
        //
        __imageFileProcessor = _moduleCfg.getClassImpl(IMAGE_FILE_PROCESSOR_CLASS, IImageFileProcessor.class);
        if (__imageFileProcessor == null) {
            __imageFileProcessor = new DefaultImageFileProcessor();
        }
        //
        __proxyServiceBaseUrl = StringUtils.trimToNull(_moduleCfg.getString(PROXY_SERVICE_BASE_URL));
        if (__proxyMode) {
            if (__proxyServiceBaseUrl != null) {
                if (!StringUtils.startsWithIgnoreCase(__proxyServiceBaseUrl, "http://") &&
                        !StringUtils.startsWithIgnoreCase(__proxyServiceBaseUrl, "https://")) {
                    throw new IllegalArgumentException("The parameter " + PROXY_SERVICE_BASE_URL + " is invalid");
                } else if (!StringUtils.endsWith(__proxyServiceBaseUrl, "/")) {
                    __proxyServiceBaseUrl = __proxyServiceBaseUrl + "/";
                }
            } else {
                throw new NullArgumentException(PROXY_SERVICE_BASE_URL);
            }
        }
        __proxyServiceAuthKey = StringUtils.trimToEmpty(_moduleCfg.getString(PROXY_SERVICE_AUTH_KEY));
        //
        __allowCustomThumbSize = _moduleCfg.getBoolean(ALLOW_CUSTOM_THUMB_SIZE);
        //
        __thumbSizeList = new ArrayList<String>();
        String[] _tmpArr = _moduleCfg.getArray(THUMB_SIZE_LIST);
        if (_tmpArr != null && _tmpArr.length > 0) {
            for (String _thumbSize : _tmpArr) {
                String[] _tmpThumb = StringUtils.split(_thumbSize, '_');
                if (_tmpThumb != null && _tmpThumb.length == 2) {
                    if (StringUtils.isNumeric(_tmpThumb[0]) && StringUtils.isNumeric(_tmpThumb[1])) {
                        int _width = Integer.valueOf(_tmpThumb[0]);
                        int _height = Integer.valueOf(_tmpThumb[1]);
                        if (_width >= 0 && _height >= 0) {
                            if (_width == _height && _width == 0) {
                                break;
                            }
                            __thumbSizeList.add(_thumbSize);
                        }
                    }
                }
            }
        }
        //
        __thumbQuality = _moduleCfg.getFloat(THUMB_QUALITY);
        //
        __allowContentTypes = new ArrayList<String>();
        _tmpArr = _moduleCfg.getArray(ALLOW_CONTENT_TYPES);
        if (_tmpArr != null && _tmpArr.length > 0) {
            __allowContentTypes.addAll(Arrays.asList(_tmpArr));
        }
    }

    @Override
    public String getNodeId() {
        return __nodeId;
    }

    @Override
    public String getCacheNamePrefix() {
        return __cacheNamePrefix;
    }

    @Override
    public int getCacheTimeout() {
        return __cacheTimeout;
    }

    @Override
    public File getFileStoragePath() {
        return __fileStoragePath;
    }

    @Override
    public String getResourcesBaseUrl() {
        return __resourcesBaseUrl;
    }

    @Override
    public IResourcesProcessor getResourcesProcessor() {
        return __resourcesProcessor;
    }

    @Override
    public int getResourcesCacheTimeout() {
        return __resourcesCacheTimeout;
    }

    @Override
    public IFileStorageAdapter getFileStorageAdapter() {
        return __fileStorageAdapter;
    }

    @Override
    public IImageFileProcessor getImageFileProcessor() {
        return __imageFileProcessor;
    }

    @Override
    public boolean isProxyMode() {
        return __proxyMode;
    }

    @Override
    public String getProxyServiceBaseUrl() {
        return __proxyServiceBaseUrl;
    }

    @Override
    public String getProxyServiceAuthKey() {
        return __proxyServiceAuthKey;
    }

    @Override
    public boolean isAllowCustomThumbSize() {
        return __allowCustomThumbSize;
    }

    @Override
    public List<String> getThumbSizeList() {
        return __thumbSizeList;
    }

    @Override
    public float getThumbQuality() {
        return __thumbQuality;
    }

    @Override
    public List<String> getAllowContentTypes() {
        return __allowContentTypes;
    }
}
