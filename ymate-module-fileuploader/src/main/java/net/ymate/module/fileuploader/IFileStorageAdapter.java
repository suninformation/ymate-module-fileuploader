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

import java.io.File;
import java.util.Map;

/**
 * 上传文件存储适配器接口
 *
 * @author 刘镇 (suninformation@163.com) on 2016/03/30 19:32
 * @since 1.0
 */
public interface IFileStorageAdapter extends IInitialization<IFileUploader> {

    /**
     * 检查资源文件是否存在
     *
     * @param fileMeta 文件信息对象
     * @return 布尔值true或false
     * @throws Exception 可能产生的任何异常
     */
    boolean isExists(UploadFileMeta fileMeta) throws Exception;

    /**
     * 存储文件
     *
     * @param hash 文件哈希值
     * @param file 上传的文件
     * @return 返回上传文件信息对象
     * @throws Exception 可能产生的任何异常
     */
    UploadFileMeta writeFile(String hash, IFileWrapper file) throws Exception;

    /**
     * 针对图片和视频截图文件自动修复补全等操作
     *
     * @param resourceType     资源类型
     * @param targetFile       目标文件
     * @param sourcePathDir    源文件存储路径
     * @param thumbStoragePath 缩略图存储路径
     * @param hash             文件哈希值
     */
    void doAfterWriteFile(ResourceType resourceType, File targetFile, String sourcePathDir, String thumbStoragePath, String hash);

    /**
     * 构建文件扩展属性
     *
     * @param hash         文件哈希值
     * @param resourceType 资源类型
     * @param file         上传的文件
     * @return 返回文件扩展属性映射
     */
    Map<String, Object> doBuildFileAttributes(String hash, ResourceType resourceType, IFileWrapper file);

    /**
     * 读取文件
     *
     * @param hash       文件哈希值
     * @param sourcePath 资源路径
     * @return 返回资源文件对象
     */
    File readFile(String hash, String sourcePath);

    /**
     * 读取文件缩略图
     *
     * @param resourceType 资源类型
     * @param hash         文件哈希值
     * @param sourcePath   资源路径
     * @param width        图片宽度
     * @param height       图片高度
     * @return 返回资源文件缩略图, 若不存在则返回null
     */
    File readThumb(ResourceType resourceType, String hash, String sourcePath, int width, int height);

    /**
     * 获取缩略图文件存储路径
     *
     * @return 返回缩略存储路径文件对象
     */
    File getThumbStoragePath();
}
