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
package net.ymate.module.fileuploader.impl;

import com.jhlabs.image.ScaleFilter;
import net.coobird.thumbnailator.util.ThumbnailatorUtils;
import net.ymate.module.fileuploader.IImageFileProcessor;
import net.ymate.platform.core.util.RuntimeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * @author 刘镇 (suninformation@163.com) on 16/6/7 上午2:51
 * @version 1.0
 */
public class DefaultImageFileProcessor implements IImageFileProcessor {

    private static final Log _LOG = LogFactory.getLog(DefaultImageFileProcessor.class);

    @Override
    public boolean resize(BufferedImage source, File dist, int width, int height, float quality, String formatName) {
        try {
            ScaleFilter _filter = new ScaleFilter(width, height);
            BufferedImage _out = _filter.filter(source, new BufferedImage(width, height, source.getType()));
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(formatName);
            if (writers.hasNext()) {
                ImageWriter writer = writers.next();
                ImageWriteParam writeParam = writer.getDefaultWriteParam();
                if (writeParam.canWriteCompressed()) {
                    writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    List<String> supportedFormats = ThumbnailatorUtils.getSupportedOutputFormatTypes(formatName);
                    if (!supportedFormats.isEmpty()) {
                        writeParam.setCompressionType(supportedFormats.get(0));
                    }
                    if (quality > 0.0f) {
                        writeParam.setCompressionQuality(quality);
                    }
                }
                ImageOutputStream outputStream = ImageIO.createImageOutputStream(dist);
                if (outputStream != null) {
                    try {
                        writer.setOutput(outputStream);
                        writer.write(null, new IIOImage(_out, null, null), writeParam);
                        writer.dispose();
                        return true;
                    } finally {
                        outputStream.close();
                    }
                }
            }
        } catch (IOException e) {
            _LOG.warn("", RuntimeUtils.unwrapThrow(e));
        }
        return false;
    }
}
