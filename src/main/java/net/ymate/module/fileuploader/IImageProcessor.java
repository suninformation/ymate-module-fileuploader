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

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 图片文件处理器
 *
 * @author 刘镇 (suninformation@163.com) on 2016/06/07 02:47
 * @since 1.0
 */
public interface IImageProcessor {

    /**
     * 重置图片大小
     *
     * @param source 源文件对象
     * @param dist 重置后文件对象
     * @param width 重置宽度
     * @param height 重置高度
     * @param quality 图片质量
     * @param format 文件格式
     * @return 返回true表示处理成功
     */
    boolean resize(BufferedImage source, File dist, int width, int height, float quality, String format);
}
