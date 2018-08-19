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
package net.ymate.module.fileuploader.repository;

import net.ymate.module.fileuploader.IFileUploader;
import net.ymate.module.fileuploader.UploadFileMeta;
import net.ymate.module.fileuploader.model.Attachment;
import net.ymate.platform.webmvc.IUploadFileWrapper;

/**
 * @author 刘镇 (suninformation@163.com) on 16/11/3 上午3:53
 * @version 1.0
 */
public interface IAttachmentRepository {

    /**
     * 分析并存储上传的文件
     *
     * @param fileWrapper 上传文件包装对象
     * @return 返回上传文件的元描述对象
     * @throws Exception 可能产生的任何异常
     */
    UploadFileMeta uploadFile(IUploadFileWrapper fileWrapper) throws Exception;

    /**
     * 通过比较文件哈希值来判断文件是否已存在
     *
     * @param hash 文件哈希值
     * @return 若存在则返回该文件资源引用路径, 否则返回空
     * @throws Exception 可能产生的任何异常
     */
    String matchHash(String hash) throws Exception;

    /**
     * 根据资源类型和文件哈希尝试加载符合的资源
     *
     * @param resourceType 资源类型
     * @param hash         文件哈希值
     * @return 返回找到的指定类型的资源记录对象, 若未找到则反回空
     * @throws Exception 可能生产的任何异常
     */
    Attachment getResource(IFileUploader.ResourceType resourceType, String hash) throws Exception;
}
