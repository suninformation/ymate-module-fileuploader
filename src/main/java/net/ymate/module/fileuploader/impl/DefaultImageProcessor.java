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

import com.jhlabs.image.ScaleFilter;
import net.coobird.thumbnailator.util.ThumbnailatorUtils;
import net.ymate.module.fileuploader.IImageProcessor;
import net.ymate.platform.commons.util.RuntimeUtils;
import org.apache.commons.lang3.StringUtils;
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
 * @author 刘镇 (suninformation@163.com) on 2016/06/07 02:51
 * @since 1.0
 */
public class DefaultImageProcessor implements IImageProcessor {

    private static final Log LOG = LogFactory.getLog(DefaultImageProcessor.class);

    @Override
    public boolean resize(BufferedImage source, File dist, int width, int height, float quality, String formatName) {
        ScaleFilter scaleFilter = new ScaleFilter(width, height);
        BufferedImage out = scaleFilter.filter(source, new BufferedImage(width, height, source.getType()));
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
                if (quality > 0) {
                    writeParam.setCompressionQuality(quality);
                }
            }
            try (ImageOutputStream outputStream = ImageIO.createImageOutputStream(dist)) {
                if (outputStream != null) {
                    writer.setOutput(outputStream);
                    writer.write(null, new IIOImage(out, null, null), writeParam);
                    writer.dispose();
                    //
                    return true;
                }
            } catch (IOException e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn(StringUtils.EMPTY, RuntimeUtils.unwrapThrow(e));
                }
            }
        }
        return false;
    }
}
