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

import net.ymate.module.fileuploader.model.Attachment;

/**
 * 用于处理被访问资源是否允许
 *
 * @author 刘镇 (suninformation@163.com) on 2018/7/16 下午6:55
 * @version 1.0
 */
public interface IResourcesAccessProcessor {

    /**
     * 验证资源是否允许访问
     *
     * @param attachment 资源记录对象
     * @return 允许访问则返回true
     * @throws Exception 可能产生的任何异常
     */
    boolean process(Attachment attachment) throws Exception;
}
