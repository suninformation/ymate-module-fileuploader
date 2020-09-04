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
package net.ymate.module.fileuploader;

import net.ymate.platform.core.support.IInitialization;

/**
 * 资源处理器 -- 用于资源上传、匹配及验证被访问资源是否允许
 *
 * @author 刘镇 (suninformation@163.com) on 2018/07/16 18:55
 * @since 1.0
 */
public interface IResourcesProcessor extends IInitialization<IFileUploader> {

    /**
     * 分析并存储上传的文件
     *
     * @param fileWrapper 上传文件包装对象
     * @return 返回上传文件的元描述对象
     * @throws Exception 可能产生的任何异常
     */
    UploadFileMeta upload(IFileWrapper fileWrapper) throws Exception;

    /**
     * 通过比较文件哈希值来判断文件是否已存在
     *
     * @param hash 文件哈希值
     * @return 若存在则返回该文件的元描述对象, 否则返回空
     * @throws Exception 可能产生的任何异常
     */
    UploadFileMeta matchHash(String hash) throws Exception;

    /**
     * 根据资源类型和文件哈希尝试加载符合的资源(验证资源是否允许访问)
     *
     * @param resourceType 资源类型
     * @param hash         文件哈希值
     * @return 返回找到的指定类型的资源记录对象, 若未找到则反回空
     * @throws Exception 可能生产的任何异常
     */
    UploadFileMeta getResource(ResourceType resourceType, String hash) throws Exception;

    /**
     * 判断当前资源文件是否被禁止访问
     *
     * @param fileMeta 上传文件的元描述对象
     * @return 返回true表示禁止访问
     */
    boolean isAccessNotAllowed(UploadFileMeta fileMeta);

    /**
     * 用于自定义代理文件上传处理逻辑(若采用模块默认处理请在接口方法内抛出UnsupportedOperationException异常)
     *
     * @param fileWrapper 上传文件对象包装器
     * @return 返回代理上传文件的元描述对象
     * @throws Exception 可能产生的任何异常
     */
    UploadFileMeta proxyUpload(IFileWrapper fileWrapper) throws Exception;

    /**
     * 用于自定义代理文件哈希值比对逻辑(若采用模块默认处理请在接口方法内抛出UnsupportedOperationException异常)
     *
     * @param hash 文件哈希值
     * @return 返回代理文件哈希值是否已存在, 若存在则返回该文件的元描述对象, 否则返回空
     * @throws Exception 可能产生的任何异常
     */
    UploadFileMeta proxyMatchHash(String hash) throws Exception;
}
