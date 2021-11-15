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

import net.ymate.platform.commons.http.impl.DefaultFileWrapper;

import java.io.File;

/**
 * @author 刘镇 (suninformation@163.com) on 2018/11/05 15:19
 * @since 1.0
 */
public interface IFileWrapper extends net.ymate.platform.commons.http.IFileWrapper {

    class Default extends DefaultFileWrapper implements IFileWrapper {

        private final long lastModifyTime;

        public Default(String fileName, String contentType, File sourceFile) {
            this(fileName, contentType, sourceFile, sourceFile.lastModified());
        }

        public Default(String fileName, String contentType, File sourceFile, long lastModifyTime) {
            super(fileName, contentType, sourceFile);
            this.lastModifyTime = lastModifyTime;
        }

        public Default(String contentType, File sourceFile) {
            this(contentType, sourceFile, sourceFile.lastModified());
        }

        public Default(String contentType, File sourceFile, long lastModifyTime) {
            super(sourceFile.getName(), contentType, sourceFile);
            this.lastModifyTime = lastModifyTime;
        }

        public Default(File sourceFile) {
            super(sourceFile);
            this.lastModifyTime = sourceFile.lastModified();
        }

        @Override
        public long getLastModifyTime() {
            return lastModifyTime;
        }
    }

    /**
     * 文件最后修改时间(毫秒)
     *
     * @return 返回文件最后修改时间(毫秒)
     */
    long getLastModifyTime();
}
