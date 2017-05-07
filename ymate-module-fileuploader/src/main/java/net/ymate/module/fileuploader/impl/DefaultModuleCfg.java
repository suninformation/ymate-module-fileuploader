/*
 * Copyright 2007-2017 the original author or authors.
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

import net.ymate.module.fileuploader.IFileStorageAdapter;
import net.ymate.module.fileuploader.IFileUploader;
import net.ymate.module.fileuploader.IFileUploaderModuleCfg;
import net.ymate.module.fileuploader.IImageFileProcessor;
import net.ymate.platform.core.YMP;
import net.ymate.platform.core.lang.BlurObject;
import net.ymate.platform.core.util.ClassUtils;
import net.ymate.platform.core.util.RuntimeUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 16/3/30 下午5:46
 * @version 1.0
 */
public class DefaultModuleCfg implements IFileUploaderModuleCfg {

    private String __nodeId;

    private String __cacheNamePrefix;

    private int __cacheTimeout;

    private File __fileStoragePath;

    private String __resourcesBaseUrl;

    private int __resourcesCacheTimeout;

    private IFileStorageAdapter __fileStorageAdapter;

    private IImageFileProcessor __imageFileProcessor;

    private boolean __proxyMode;

    private String __proxyServiceBaseUrl;

    private boolean __allowCustomThumbSize;

    private List<String> __thumbSizeList;

    private float __thumbQuality;

    private List<String> __allowContentTypes;

    public DefaultModuleCfg(YMP owner) {
        Map<String, String> _moduleCfgs = owner.getConfig().getModuleConfigs(IFileUploader.MODULE_NAME);
        //
        __proxyMode = BlurObject.bind(_moduleCfgs.get("proxy_mode")).toBooleanValue();
        //
        __fileStoragePath = new File(RuntimeUtils.replaceEnvVariable(StringUtils.defaultIfBlank(_moduleCfgs.get("file_storage_path"), "${root}/upload_files")));
        if (!__proxyMode) {
            if (!__fileStoragePath.isAbsolute() ||
                    !__fileStoragePath.exists() ||
                    !__fileStoragePath.isDirectory() ||
                    !__fileStoragePath.canRead() || !__fileStoragePath.canWrite()) {
                throw new IllegalArgumentException("The parameter file_storage_path is invalid or is not a directory");
            }
        }
        //
        __nodeId = StringUtils.defaultIfBlank(_moduleCfgs.get("node_id"), "unknown");
        //
        __cacheNamePrefix = StringUtils.trimToEmpty(_moduleCfgs.get("cache_name_prefix"));
        //
        __cacheTimeout = BlurObject.bind(_moduleCfgs.get("cache_timeout")).toIntValue();
        //
        __resourcesBaseUrl = StringUtils.trimToNull(_moduleCfgs.get("resources_base_url"));
        if (__resourcesBaseUrl != null) {
            if (!StringUtils.startsWithIgnoreCase(__resourcesBaseUrl, "http://") &&
                    !StringUtils.startsWithIgnoreCase(__resourcesBaseUrl, "https://")) {
                throw new IllegalArgumentException("The parameter resources_base_url is invalid");
            } else if (!StringUtils.endsWith(__resourcesBaseUrl, "/")) {
                __resourcesBaseUrl = __resourcesBaseUrl + "/";
            }
        }
        //
        __resourcesCacheTimeout = BlurObject.bind(_moduleCfgs.get("resources_cache_timeout")).toIntValue();
        int _oneYear = 60 * 60 * 24 * 365;
        if (__resourcesCacheTimeout <= 0 || __resourcesCacheTimeout > _oneYear) {
            __resourcesCacheTimeout = _oneYear;
        }
        //
        __fileStorageAdapter = ClassUtils.impl(_moduleCfgs.get("file_storage_adapter_class"), IFileStorageAdapter.class, this.getClass());
        if (__fileStorageAdapter == null) {
            __fileStorageAdapter = new DefaultFileStorageAdapter();
        }
        //
        __imageFileProcessor = ClassUtils.impl(_moduleCfgs.get("image_file_processor_class"), IImageFileProcessor.class, this.getClass());
        if (__imageFileProcessor == null) {
            __imageFileProcessor = new DefaultImageFileProcessor();
        }
        //
        __proxyServiceBaseUrl = StringUtils.trimToNull(_moduleCfgs.get("proxy_service_base_url"));
        if (__proxyMode) {
            if (__proxyServiceBaseUrl != null) {
                if (!StringUtils.startsWithIgnoreCase(__proxyServiceBaseUrl, "http://") &&
                        !StringUtils.startsWithIgnoreCase(__proxyServiceBaseUrl, "https://")) {
                    throw new IllegalArgumentException("The parameter proxy_service_base_url is invalid");
                } else if (!StringUtils.endsWith(__proxyServiceBaseUrl, "/")) {
                    __proxyServiceBaseUrl = __proxyServiceBaseUrl + "/";
                }
            } else {
                throw new NullArgumentException("proxy_service_base_url");
            }
        }
        //
        __allowCustomThumbSize = BlurObject.bind(_moduleCfgs.get("allow_custom_thumb_size")).toBooleanValue();
        //
        __thumbSizeList = new ArrayList<String>();
        String[] _tmpArr = StringUtils.split(_moduleCfgs.get("thumb_size_list"), '|');
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
        __thumbQuality = BlurObject.bind(_moduleCfgs.get("thumb_quality")).toFloatValue();
        //
        __allowContentTypes = new ArrayList<String>();
        _tmpArr = StringUtils.split(StringUtils.trimToEmpty(_moduleCfgs.get("allow_content_types")).toLowerCase(), '|');
        if (_tmpArr != null && _tmpArr.length > 0) {
            __allowContentTypes.addAll(Arrays.asList(_tmpArr));
        }
    }

    public String getNodeId() {
        return __nodeId;
    }

    public String getCacheNamePrefix() {
        return __cacheNamePrefix;
    }

    public int getCacheTimeout() {
        return __cacheTimeout;
    }

    public File getFileStoragePath() {
        return __fileStoragePath;
    }

    public String getResourcesBaseUrl() {
        return __resourcesBaseUrl;
    }

    public int getResourcesCacheTimeout() {
        return __resourcesCacheTimeout;
    }

    public IFileStorageAdapter getFileStorageAdapter() {
        return __fileStorageAdapter;
    }

    public IImageFileProcessor getImageFileProcessor() {
        return __imageFileProcessor;
    }

    public boolean isProxyMode() {
        return __proxyMode;
    }

    public String getProxyServiceBaseUrl() {
        return __proxyServiceBaseUrl;
    }

    public boolean isAllowCustomThumbSize() {
        return __allowCustomThumbSize;
    }

    public List<String> getThumbSizeList() {
        return __thumbSizeList;
    }

    public float getThumbQuality() {
        return __thumbQuality;
    }

    public List<String> getAllowContentTypes() {
        return __allowContentTypes;
    }
}
