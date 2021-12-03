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

import net.ymate.platform.commons.json.IJsonObjectWrapper;

/**
 * 上传文件结果处理器接口
 *
 * @author 刘镇 (suninformation@163.com) on 2016/05/09 01:04
 * @since 1.0
 */
public interface IUploadResultProcessor {

    /**
     * 文件上传结果处理器名称
     *
     * @return 返回文件上传结果处理器名称
     */
    String getName();

    /**
     * 自定义JSON结果
     *
     * @param fileMeta 上传文件元数据对象
     * @return 返回自定义JSON结果对象
     * @throws Exception 可能产生任何异常
     */
    IJsonObjectWrapper process(UploadFileMeta fileMeta) throws Exception;
}
