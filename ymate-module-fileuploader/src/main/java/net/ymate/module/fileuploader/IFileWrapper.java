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

import net.ymate.platform.core.util.FileUtils;
import net.ymate.platform.core.util.MimeTypeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author 刘镇 (suninformation@163.com) on 2018/11/5 下午3:19
 * @version 1.0
 */
public interface IFileWrapper extends net.ymate.framework.commons.IFileWrapper {

    class NEW extends net.ymate.framework.commons.IFileWrapper.NEW implements IFileWrapper {

        private File __sourceFile;

        public NEW(String fileName, String contentType, File sourceFile) {
            super(fileName, contentType, sourceFile.length(), null);
            __sourceFile = sourceFile;
        }

        public NEW(String contentType, File sourceFile) {
            super(sourceFile.getName(), contentType, sourceFile.length(), null);
            __sourceFile = sourceFile;
        }

        public NEW(File sourceFile) {
            super(sourceFile.getName(), MimeTypeUtils.getFileMimeType(FileUtils.getExtName(sourceFile.getName())), sourceFile.length(), null);
            __sourceFile = sourceFile;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new FileInputStream(__sourceFile);
        }

        @Override
        public long getLastModifyTime() {
            return __sourceFile.lastModified();
        }
    }

    /**
     * @return 返回文件最后修改时间(毫秒)
     */
    long getLastModifyTime();
}
