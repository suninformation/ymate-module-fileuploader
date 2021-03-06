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

import java.io.File;
import java.util.List;

/**
 * @author 刘镇 (suninformation@163.com) on 16/3/27 上午6:05
 * @version 1.0
 */
public interface IFileUploaderModuleCfg {

    String PROXY_MODE = "proxy_mode";

    String FILE_STORAGE_PATH = "file_storage_path";

    String NODE_ID = "node_id";

    String CACHE_NAME_PREFIX = "cache_name_prefix";

    String CACHE_TIMEOUT = "cache_timeout";

    String RESOURCES_BASE_URL = "resources_base_url";

    String RESOURCES_PROCESSOR_CLASS = "resources_processor_class";

    String RESOURCES_CACHE_TIMEOUT = "resources_cache_timeout";

    String FILE_STORAGE_ADAPTER_CLASS = "file_storage_adapter_class";

    String IMAGE_FILE_PROCESSOR_CLASS = "image_file_processor_class";

    String PROXY_SERVICE_BASE_URL = "proxy_service_base_url";

    String PROXY_SERVICE_AUTH_KEY = "proxy_service_auth_key";

    String ALLOW_CUSTOM_THUMB_SIZE = "allow_custom_thumb_size";

    String THUMB_SIZE_LIST = "thumb_size_list";

    String THUMB_QUALITY = "thumb_quality";

    String ALLOW_CONTENT_TYPES = "allow_content_types";

    /**
     * @return 服务器节点标识符, 默认值: unknown
     */
    String getNodeId();

    /**
     * @return 缓存名称前缀, 默认值: ""
     */
    String getCacheNamePrefix();

    /**
     * @return 缓存数据超时时间, 可选参数, 数值必须大于等于0, 否则将采用默认
     */
    int getCacheTimeout();

    /**
     * @return 上传文件存储根路径, 默认值: ${root}/upload_files
     */
    File getFileStoragePath();

    /**
     * @return 静态资源引用基准URL路径, 必须以'http://'或'https://'开始并以'/'结束, 如: http://www.ymate.net/static/resources/, 默认值: 空(即不使用静态资源引用路径)
     */
    String getResourcesBaseUrl();

    /**
     * @return 资源处理器类, 用于资源上传、匹配及验证被访问资源是否允许(非代理模式则此项必填), 此类需实现net.ymate.module.fileuploader.IResourcesProcessor接口
     */
    IResourcesProcessor getResourcesProcessor();

    /**
     * @return 资源文件缓存超时时间(秒), 取值范围: 0-31536000(=60 * 60 * 24 * 365), 取值小于或等于0则表示缓存一年, 默认值: 0
     */
    int getResourcesCacheTimeout();

    /**
     * @return 文件存储适配器接口实现, 若未提供则使用系统默认
     */
    IFileStorageAdapter getFileStorageAdapter();

    /**
     * @return 图片文件处理器接口实现, 若未提供则使用系统默认
     */
    IImageFileProcessor getImageFileProcessor();

    /**
     * 若开启代理模式, 则除文件存储路径参数外其它将全部无效, 一切将以代理服务端配置为主
     *
     * @return 是否开启代理模式, 默认值: false
     */
    boolean isProxyMode();

    /**
     * @return 代理服务基准URL路径(若开启代理模式则此项必填), 必须以'http://'或'https://'开始并以'/'结束, 如: http://www.ymate.net/proxies/, 默认值: 空
     */
    String getProxyServiceBaseUrl();

    /**
     * @return # 代理客户端与服务端之间通讯请求参数签名密钥, 默认值: ""
     */
    String getProxyServiceAuthKey();

    /**
     * @return 是否允许自定义缩略图尺寸, 默认值: false
     */
    boolean isAllowCustomThumbSize();

    /**
     * @return 缩略图尺寸列表, 该尺寸列表在允许自定义缩略图尺寸时生效, 若列表不为空则自定义尺寸不能超过此范围, 如: 600_480、1024_0 (0表示等比缩放, 不支持0*0), 默认值: 空
     */
    List<String> getThumbSizeList();

    /**
     * @return 缩略图清晰度, 默认值: 0f
     */
    float getThumbQuality();

    /**
     * @return 允许上传的文件ContentType列表, 默认值: 空, 表示不限制
     */
    List<String> getAllowContentTypes();
}
