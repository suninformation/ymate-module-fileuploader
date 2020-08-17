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
 * 针对FileUpload组件文件上传返回结果处理器
 *
 * @author 刘镇 (suninformation@163.com) on 16/5/9 上午2:48
 * @since 1.0
 */
public class FileUploadResultProcessor implements IUploadResultProcessor {

    @Override
    public String getName() {
        return "fileupload";
    }

    @Override
    public IJsonObjectWrapper process(UploadFileMeta fileMeta) throws Exception {
        // {"files":[{"thumbnailUrl":"update/a.jpg","name":"01.png","size":"300k", "hash": "0ed1267e72738426cc7f8dfd24fe5300", "type": "image"}]}
        IJsonObjectWrapper objectWrapper = JsonWrapper.createJsonObject()
                .put("thumbnailUrl", fileMeta.getUrl())
                .put("name", fileMeta.getFilename())
                .put("size", fileMeta.getSize())
                .put("hash", fileMeta.getHash())
                .put("type", fileMeta.getType().name().toLowerCase());
        return JsonWrapper.createJsonObject().put("files", JsonWrapper.createJsonArray().add(objectWrapper));
    }
}
