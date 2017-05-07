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

import net.coobird.thumbnailator.Thumbnails;
import net.ymate.module.fileuploader.IImageFileProcessor;
import net.ymate.platform.core.util.RuntimeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author 刘镇 (suninformation@163.com) on 16/6/7 上午3:14
 * @version 1.0
 */
public class ThumbnailatorImageFileProcessor implements IImageFileProcessor {

    private static final Log _LOG = LogFactory.getLog(ThumbnailatorImageFileProcessor.class);

    public boolean resize(BufferedImage source, File dist, int width, int height, float quality, String formatName) {
        try {
            Thumbnails.Builder<BufferedImage> _thumbBuilder = Thumbnails.of(source);
            if (width == -1 || height == -1) {
                if (width != -1) {
                    _thumbBuilder.width(width);
                } else if (height != -1) {
                    _thumbBuilder.height(height);
                }
            } else {
                _thumbBuilder.size(width, height).keepAspectRatio(false);
            }
            if (quality > 0.0f) {
                _thumbBuilder.outputQuality(quality);
            }
            _thumbBuilder.outputFormat(formatName).toFile(dist);
            return true;
        } catch (IOException e) {
            _LOG.warn("", RuntimeUtils.unwrapThrow(e));
        }
        return false;
    }
}
