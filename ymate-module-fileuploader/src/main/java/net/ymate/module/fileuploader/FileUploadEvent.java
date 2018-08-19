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
package net.ymate.module.fileuploader;

import net.ymate.platform.core.event.EventContext;
import net.ymate.platform.core.event.IEvent;

/**
 * @author 刘镇 (suninformation@163.com) on 17/3/23 下午11:08
 * @version 1.0
 */
public class FileUploadEvent extends EventContext<IFileUploader, FileUploadEvent.EVENT> implements IEvent {

    /**
     * Fileuploader事件枚举
     */
    public enum EVENT {

        /**
         * 文件上传(全新上传)
         */
        FILE_UPLOADED_CREATE,

        /**
         * 文件上传(重复上传)
         */
        FILE_UPLOADED_UPDATE,

        /**
         * 文件下载
         */
        FILE_DOWNLOADED,

        /**
         * 文件Hash匹配成功
         */
        FILE_MATCHED
    }

    public FileUploadEvent(IFileUploader owner, EVENT eventName) {
        super(owner, FileUploadEvent.class, eventName);
    }
}
