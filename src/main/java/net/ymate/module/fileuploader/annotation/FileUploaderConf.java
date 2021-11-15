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
package net.ymate.module.fileuploader.annotation;

import net.ymate.module.fileuploader.IFileStorageAdapter;
import net.ymate.module.fileuploader.IImageProcessor;
import net.ymate.module.fileuploader.IResourcesProcessor;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.*;

/**
 * @author 刘镇 (suninformation@163.com) on 2020/08/06 22:21
 * @since 2.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FileUploaderConf {

    /**
     * @return 模块是否已启用, 默认值: true
     */
    boolean enabled() default true;

    /**
     * @return 节点标识符, 默认值: unknown
     */
    String nodeId() default StringUtils.EMPTY;

    /**
     * @return 缓存名称前缀, 默认值: ""
     */
    String cacheNamePrefix() default StringUtils.EMPTY;

    /**
     * @return 缓存数据超时时间, 可选参数, 数值必须大于等于0, 否则将采用默认
     */
    int cacheTimeout() default 0;

    /**
     * @return 默认控制器服务请求映射前缀(不允许 ' / ' 开始和结束), 默认值: ""
     */
    String servicePrefix() default StringUtils.EMPTY;

    /**
     * @return 是否注册默认控制器, 默认值: true
     */
    boolean serviceEnabled() default true;

    /**
     * @return 上传文件存储根路径（根据存储适配器接口实现决定其值具体含义）, 默认存储适配器取值: ${root}/upload_files
     */
    String fileStoragePath() default StringUtils.EMPTY;

    /**
     * @return 缩略图文件存储根路径（根据存储适配器接口实现决定其值具体含义）, 默认存储适配器取值与上传文件存储根路径值相同
     */
    String thumbStoragePath() default StringUtils.EMPTY;

    /**
     * @return 静态资源引用基准URL路径, 必须以'http://'或'https://'开始并以'/'结束, 如: http://www.ymate.net/static/resources/, 默认值: 空(即不使用静态资源引用路径)
     */
    String resourcesBaseUrl() default StringUtils.EMPTY;

    /**
     * @return 资源处理器类, 用于资源上传、匹配及验证被访问资源是否允许(非代理模式则此项必填), 此类需实现net.ymate.module.fileuploader.IResourcesProcessor接口
     */
    Class<? extends IResourcesProcessor> resourcesProcessorClass() default IResourcesProcessor.class;

    /**
     * @return 资源文件缓存超时时间(秒), 取值范围: 0-31536000(=60 * 60 * 24 * 365), 取值小于或等于0则表示缓存一年, 默认值: 0
     */
    int resourcesCacheTimeout() default 0;

    /**
     * @return 文件存储适配器接口实现, 若未提供则使用系统默认
     */
    Class<? extends IFileStorageAdapter> fileStorageAdapterClass() default IFileStorageAdapter.class;

    /**
     * @return 图片文件处理器接口实现, 若未提供则使用系统默认
     */
    Class<? extends IImageProcessor> imageProcessorClass() default IImageProcessor.class;

    /**
     * @return 是否开启代理模式, 默认值: false
     */
    boolean proxyMode() default false;

    /**
     * @return 代理服务基准URL路径(若开启代理模式则此项必填), 必须以'http://'或'https://'开始并以'/'结束, 如: http://www.ymate.net/proxies/, 默认值: 空
     */
    String proxyServiceBaseUrl() default StringUtils.EMPTY;

    /**
     * @return 代理客户端与服务端之间通讯请求参数签名密钥, 默认值: ""
     */
    String proxyServiceAuthKey() default StringUtils.EMPTY;

    /**
     * @return 是否允许自定义缩略图尺寸, 默认值: false
     */
    boolean allowCustomThumbSize() default false;

    /**
     * @return 缩略图尺寸列表, 该尺寸列表在允许自定义缩略图尺寸时生效, 若列表不为空则自定义尺寸不能超过此范围, 如: 600_480、1024_0 (0表示等比缩放, 不支持0_0), 默认值: 空
     */
    String[] thumbSizeList() default {};

    /**
     * @return 缩略图清晰度, 默认值: 0f
     */
    float thumbQuality() default 0;

    /**
     * @return 允许上传的文件ContentType列表, 默认值: 空, 表示不限制
     */
    String[] allowContentTypes() default {};
}
