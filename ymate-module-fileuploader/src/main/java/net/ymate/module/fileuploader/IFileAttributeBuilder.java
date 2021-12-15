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

import java.util.Map;

/**
 * 文件扩展属性构建器
 *
 * @author 刘镇 (suninformation@163.com) on 2021/12/15 9:11 下午
 * @since 2.0.0
 */
public interface IFileAttributeBuilder {

    /**
     * 构建文件扩展属性
     *
     * @param hash         文件哈希值
     * @param resourceType 资源类型
     * @param file         上传的文件
     * @return 返回文件扩展属性映射
     */
    Map<String, Object> build(String hash, ResourceType resourceType, IFileWrapper file);
}
