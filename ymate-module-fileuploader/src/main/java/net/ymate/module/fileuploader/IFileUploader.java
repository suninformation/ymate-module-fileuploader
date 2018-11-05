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

import net.ymate.platform.cache.ICache;
import net.ymate.platform.core.YMP;

/**
 * @author 刘镇 (suninformation@163.com) on 16/3/27 上午6:04
 * @version 1.0
 */
public interface IFileUploader {

    String MODULE_NAME = "module.fileuploader";

    /**
     * @return 返回所属YMP框架管理器实例
     */
    YMP getOwner();

    /**
     * @return 返回模块配置对象
     */
    IFileUploaderModuleCfg getModuleCfg();

    /**
     * @return 返回模块是否已初始化
     */
    boolean isInited();

    /**
     * 注册上传文件结果处理器
     *
     * @param name        处理器名称
     * @param targetClass 目标类型
     * @throws Exception 可能产生的任何异常
     */
    void registerUploadResultProcessor(String name, Class<? extends IUploadResultProcessor> targetClass) throws Exception;

    /**
     * @param name 处理器名称
     * @return 根据处理器名称获取实例对象
     */
    IUploadResultProcessor getUploadResultProcessor(String name);

    /**
     * @return 返回文件哈希值缓存对象
     */
    ICache getMatchFileHashCache();

    /**
     * 执行文件上传
     *
     * @param fileWrapper 上传文件对象包装器
     * @return 返回上传文件描述对象
     * @throws Exception 可能产生的任何异常
     */
    UploadFileMeta upload(IFileWrapper fileWrapper) throws Exception;

    /**
     * 匹配文件哈希值
     *
     * @param hash 文件哈希值
     * @return 若匹配成功则返回文件访问URL地址, 否则返回空
     * @throws Exception 可能产生的任何异常
     */
    String match(String hash) throws Exception;

    /**
     * 加载资源
     *
     * @param resourceType 资源类型
     * @param hash         文件哈希值
     * @return 若存在则返回文件对象包装器接口实例, 否则返回空
     * @throws Exception 可能产生的任何异常
     */
    IFileWrapper resources(IFileUploader.ResourceType resourceType, String hash) throws Exception;

    /**
     * 文件资源类型枚举
     */
    enum ResourceType {

        IMAGE(1),
        VIDEO(2),
        AUDIO(3),
        TEXT(4),
        APPLICATION(5),
        THUMB(6);

        private int type;

        ResourceType(int type) {
            this.type = type;
        }

        public static ResourceType valueOf(Integer type) {
            if (type != null) {
                switch (type) {
                    case 1:
                    case 6:
                        return ResourceType.IMAGE;
                    case 2:
                        return ResourceType.VIDEO;
                    case 3:
                        return ResourceType.AUDIO;
                    case 4:
                        return ResourceType.TEXT;
                    default:
                        return ResourceType.APPLICATION;
                }
            }
            return null;
        }

        public int type() {
            return type;
        }
    }
}
