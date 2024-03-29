/*
 * Copyright 2020 the original author or authors.
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
import net.ymate.platform.core.IApplication;
import net.ymate.platform.core.beans.annotation.Ignored;
import net.ymate.platform.core.support.IDestroyable;
import net.ymate.platform.core.support.IInitialization;

/**
 * IFileUploader generated By ModuleMojo on 2020/08/06 22:21
 *
 * @author YMP (https://www.ymate.net/)
 */
@Ignored
public interface IFileUploader extends IInitialization<IApplication>, IDestroyable {

    String MODULE_NAME = "module.fileuploader";

    /**
     * 获取所属应用容器
     *
     * @return 返回所属应用容器实例
     */
    IApplication getOwner();

    /**
     * 获取配置
     *
     * @return 返回配置对象
     */
    IFileUploaderConfig getConfig();

    /**
     * 注册上传文件结果处理器
     *
     * @param targetClass 目标类型
     * @throws Exception 可能产生的任何异常
     */
    void registerUploadResultProcessor(Class<? extends IUploadResultProcessor> targetClass) throws Exception;

    /**
     * 根据上传文件结果处理器名称获取实例对象
     *
     * @param name 处理器名称
     * @return 返回上传文件结果处理器对象
     */
    IUploadResultProcessor getUploadResultProcessor(String name);

    /**
     * 文件哈希值缓存对象
     *
     * @return 返回文件哈希值缓存对象
     */
    ICache getFileHashCache();

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
     * @return 若匹配成功则返回文件的元描述对象, 否则返回空
     * @throws Exception 可能产生的任何异常
     */
    UploadFileMeta match(String hash) throws Exception;

    /**
     * 加载资源
     *
     * @param resourceType 资源类型
     * @param hash         文件哈希值
     * @return 若存在则返回文件对象包装器接口实例, 否则返回空
     * @throws Exception 可能产生的任何异常
     */
    IFileWrapper resources(ResourceType resourceType, String hash) throws Exception;

}
