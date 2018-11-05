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

/**
 * @author 刘镇 (suninformation@163.com) on 2018/11/5 上午11:46
 * @version 1.0
 */
public class ContentTypeNotAllowException extends Exception {

    public ContentTypeNotAllowException() {
    }

    public ContentTypeNotAllowException(String message) {
        super(message);
    }

    public ContentTypeNotAllowException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContentTypeNotAllowException(Throwable cause) {
        super(cause);
    }
}
