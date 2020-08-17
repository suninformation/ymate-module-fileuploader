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

import net.coobird.thumbnailator.Thumbnails;
import net.ymate.module.fileuploader.IImageProcessor;
import net.ymate.platform.commons.util.RuntimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author 刘镇 (suninformation@163.com) on 2016/06/07 03:14
 * @since 1.0
 */
public class ThumbnailatorImageProcessor implements IImageProcessor {

    private static final Log LOG = LogFactory.getLog(ThumbnailatorImageProcessor.class);

    @Override
    public boolean resize(BufferedImage source, File dist, int width, int height, float quality, String formatName) {
        try {
            Thumbnails.Builder<BufferedImage> thumbBuilder = Thumbnails.of(source);
            if (width == -1 || height == -1) {
                if (width != -1) {
                    thumbBuilder.width(width);
                } else if (height != -1) {
                    thumbBuilder.height(height);
                }
            } else {
                thumbBuilder.size(width, height).keepAspectRatio(false);
            }
            if (quality > 0) {
                thumbBuilder.outputQuality(quality);
            }
            thumbBuilder.outputFormat(formatName).toFile(dist);
            return true;
        } catch (IOException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn(StringUtils.EMPTY, RuntimeUtils.unwrapThrow(e));
            }
        }
        return false;
    }
}
