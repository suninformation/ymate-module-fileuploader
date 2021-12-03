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

import java.io.File;

/**
 * @author 刘镇 (suninformation@163.com) on 2021/12/3 11:53 上午
 * @since 2.0.0
 */
public interface IFileUploaderClient {

    /**
     * 初始化
     *
     * @param clientConfig 客户端配置对象
     */
    void initialize(IFileUploaderClientConfig clientConfig);

    /**
     * 是否已初始化
     *
     * @return 返回true表示已初始化
     */
    boolean isInitialized();

    /**
     * 获取客户端配置
     *
     * @return 返回客户端配置对象
     */
    IFileUploaderClientConfig getClientConfig();

    /**
     * 执行文件上传
     *
     * @param fileWrapper 上传文件对象包装器
     * @return 返回上传文件描述对象
     * @throws Exception 可能产生的任何异常
     */
    UploadFileMeta upload(IFileWrapper fileWrapper) throws Exception;

    /**
     * 执行文件上传
     *
     * @param file 上传文件对象
     * @return 返回上传文件描述对象
     * @throws Exception 可能产生的任何异常
     */
    UploadFileMeta upload(File file) throws Exception;

    /**
     * 匹配文件哈希值
     *
     * @param hash 文件哈希值
     * @return 若匹配成功则返回文件的元描述对象, 否则返回空
     * @throws Exception 可能产生的任何异常
     */
    UploadFileMeta match(String hash) throws Exception;

    /**
     * 下载资源
     *
     * @param resourceType 资源类型
     * @param hash         文件哈希值
     * @return 若存在则返回文件对象包装器接口实例, 否则返回空
     * @throws Exception 可能产生的任何异常
     */
    IFileWrapper resources(ResourceType resourceType, String hash) throws Exception;

    /**
     * 下载资源
     *
     * @param sourcePath 源文件路径
     * @return 若存在则返回文件对象包装器接口实例, 否则返回空
     * @throws Exception 可能产生的任何异常
     */
    IFileWrapper resources(String sourcePath) throws Exception;
}
