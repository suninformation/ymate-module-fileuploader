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
package net.ymate.module.fileuploader.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.ymate.module.fileuploader.IUploadResultProcessor;
import net.ymate.module.fileuploader.UploadFileMeta;
import net.ymate.module.fileuploader.annotation.UploadResultProcessor;
import net.ymate.platform.webmvc.util.WebUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 针对FileUpload组件文件上传返回结果处理器
 *
 * @author 刘镇 (suninformation@163.com) on 16/5/9 上午2:48
 * @version 1.0
 */
@UploadResultProcessor("fileupload")
public class FileUploadResultProcessor implements IUploadResultProcessor {

    @Override
    public JSONObject process(UploadFileMeta fileMeta) throws Exception {
        // {"files":[{"thumbnailUrl":"update/a.jpg","name":"01.png","size":"300k"}]}
        JSONArray _filesArr = new JSONArray();
        JSONObject _fileObj = new JSONObject();
        _fileObj.put("thumbnailUrl", fileMeta.getUrl());
        if (StringUtils.isNotBlank(fileMeta.getFilename())) {
            _fileObj.put("name", WebUtils.decodeURL(fileMeta.getFilename()));
        }
        _fileObj.put("size", fileMeta.getSize());
        _fileObj.put("hash", fileMeta.getHash());
        _fileObj.put("type", fileMeta.getType().name().toLowerCase());
        _filesArr.add(_fileObj);
        //
        JSONObject _json = new JSONObject();
        _json.put("files", _filesArr);
        return _json;
    }
}
