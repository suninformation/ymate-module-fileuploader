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
package net.ymate.module.fileuploader.handle;

import net.ymate.module.fileuploader.IFileUploader;
import net.ymate.module.fileuploader.IUploadResultProcessor;
import net.ymate.module.fileuploader.annotation.UploadResultProcessor;
import net.ymate.platform.core.beans.IBeanHandler;
import net.ymate.platform.core.util.ClassUtils;

/**
 * @author 刘镇 (suninformation@163.com) on 16/5/9 上午2:19
 * @version 1.0
 */
public class UploadResultProcessorHandler implements IBeanHandler {

    private IFileUploader __owner;

    public UploadResultProcessorHandler(IFileUploader owner) {
        __owner = owner;
    }

    @SuppressWarnings("unchecked")
    public Object handle(Class<?> targetClass) throws Exception {
        if (ClassUtils.isInterfaceOf(targetClass, IUploadResultProcessor.class)) {
            __owner.registerUploadResultProcessor(targetClass.getAnnotation(UploadResultProcessor.class).value(),
                    (Class<? extends IUploadResultProcessor>) targetClass);
        }
        return null;
    }
}

