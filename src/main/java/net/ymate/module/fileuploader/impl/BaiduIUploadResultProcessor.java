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
package net.ymate.module.fileuploader.impl;

import net.ymate.module.fileuploader.IUploadResultProcessor;
import net.ymate.module.fileuploader.UploadFileMeta;
import net.ymate.platform.commons.json.IJsonObjectWrapper;
import net.ymate.platform.commons.json.JsonWrapper;

/**
 * 针对百度编辑器文件上传返回结果处理器
 *
 * @author 刘镇 (suninformation@163.com) on 16/5/9 上午2:46
 * @since 1.0
 */
public class BaiduIUploadResultProcessor implements IUploadResultProcessor {

    @Override
    public String getName() {
        return "baidu";
    }

    @Override
    public IJsonObjectWrapper process(UploadFileMeta fileMeta) throws Exception {
        return JsonWrapper.createJsonObject()
                .put("state", "SUCCESS")
                .put("title", fileMeta.getFilename())
                .put("size", fileMeta.getSize())
                .put("url", fileMeta.getUrl());
    }
}
