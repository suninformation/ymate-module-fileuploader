/*
 * Copyright 2007-2017 the original author or authors.
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

import com.alibaba.fastjson.JSONObject;
import net.ymate.framework.core.util.WebUtils;
import net.ymate.module.fileuploader.IUploadResultProcessor;
import net.ymate.module.fileuploader.UploadFileMeta;
import net.ymate.module.fileuploader.annotation.UploadResultProcessor;
import net.ymate.platform.webmvc.view.View;
import net.ymate.platform.webmvc.view.impl.JsonView;
import org.apache.commons.lang.StringUtils;

/**
 * 针对百度编辑器文件上传返回结果处理器
 *
 * @author 刘镇 (suninformation@163.com) on 16/5/9 上午2:46
 * @version 1.0
 */
@UploadResultProcessor("baidu")
public class BaiduIUploadResultProcessor implements IUploadResultProcessor {

    public JsonView process(UploadFileMeta fileMeta) throws Exception {
        JSONObject _json = new JSONObject();
        _json.put("state", "SUCCESS");
        if (StringUtils.isNotBlank(fileMeta.getTitle())) {
            _json.put("title", WebUtils.decodeURL(fileMeta.getTitle()));
        }
        _json.put("size", fileMeta.getSize());
        _json.put("url", fileMeta.getUrl());
        //
        return View.jsonView(_json);
    }
}
