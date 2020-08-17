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

import net.ymate.module.fileuploader.*;
import net.ymate.platform.commons.lang.BlurObject;
import net.ymate.platform.commons.util.FileUtils;
import net.ymate.platform.commons.util.RuntimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author 刘镇 (suninformation@163.com) on 2016/03/31 01:50
 * @since 1.0
 */
public class DefaultFileStorageAdapter implements IFileStorageAdapter {

    private static final Log LOG = LogFactory.getLog(DefaultFileStorageAdapter.class);

    private IFileUploader owner;

    private boolean initialized;

    @Override
    public void initialize(IFileUploader owner) throws Exception {
        this.owner = owner;
        this.initialized = true;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean isExists(UploadFileMeta fileMeta) {
        File target = new File(owner.getConfig().getFileStoragePath(), fileMeta.getSourcePath());
        return target.exists() && target.isFile();
    }

    @Override
    public UploadFileMeta writeFile(String hash, IFileWrapper file) throws Exception {
        ResourceType resourceType = ResourceType.valueOf(StringUtils.substringBefore(file.getContentType(), "/").toUpperCase());
        // 转存文件，路径格式：{TYPE_NAME}/{FILE_HASH_1-2BIT}/{FILE_HASH_3-4BIT}/{FILE_HASH_32BIT}.{EXT}
        String extension = StringUtils.trimToNull(file.getSuffix());
        String filename = StringUtils.join(new Object[]{hash, extension}, FileUtils.POINT_CHAR);
        String sourcePath = String.format("%s/%s/%s/%s", resourceType.name().toLowerCase(), StringUtils.substring(hash, 0, 2), StringUtils.substring(hash, 2, 4), filename);
        File targetFile = new File(owner.getConfig().getFileStoragePath(), sourcePath);
        if (targetFile.getParentFile().mkdirs() && LOG.isInfoEnabled()) {
            LOG.info(String.format("Successfully created directory: %s", targetFile.getParentFile().getPath()));
        }
        file.writeTo(targetFile);
        //
        createThumbFiles(targetFile);
        //
        return UploadFileMeta.builder()
                .hash(hash)
                .filename(filename)
                .extension(extension)
                .size(file.getContentLength())
                .mimeType(file.getContentType())
                .type(resourceType)
                .status(0)
                .sourcePath(sourcePath)
                .createTime(file.getLastModifyTime())
                .lastModifyTime(file.getLastModifyTime())
                .build();
    }

    @Override
    public void createThumbFiles(File sourceFile) {
        // 判断是否允许自定义缩略图尺寸
        if (owner.getConfig().isAllowCustomThumbSize() && !owner.getConfig().getThumbSizeList().isEmpty()) {
            try {
                BufferedImage bufferedImage = ImageIO.read(sourceFile);
                if (bufferedImage != null) {
                    for (String thumbSize : owner.getConfig().getThumbSizeList()) {
                        String[] sizeArr = StringUtils.split(thumbSize, "_");
                        // 调整宽高参数, 超出原图将不处理
                        int width = BlurObject.bind(sizeArr[0]).toIntValue();
                        int height = BlurObject.bind(sizeArr[1]).toIntValue();
                        //
                        createThumbFileIfNeed(sourceFile, bufferedImage, width, height);
                    }
                }
            } catch (IOException e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn(StringUtils.EMPTY, RuntimeUtils.unwrapThrow(e));
                }
            }
        }
    }

    private File createThumbFileIfNeed(File sourceFile, BufferedImage bufferedImg, int width, int height) {
        try {
            if (bufferedImg == null) {
                bufferedImg = ImageIO.read(sourceFile);
            }
            if (bufferedImg != null) {
                int originWidth = bufferedImg.getWidth();
                int originHeight = bufferedImg.getHeight();
                // 调整宽高参数, 超出原图将不处理
                int resizeWidth = Math.min(width, originWidth);
                int resizeHeight = Math.min(height, originHeight);
                if (resizeWidth == 0) {
                    resizeWidth = Long.valueOf(Math.round(originWidth * height * 1.0 / originHeight)).intValue();
                } else if (resizeHeight == 0) {
                    resizeHeight = Long.valueOf(Math.round(originHeight * width * 1.0 / originWidth)).intValue();
                }
                //
                float quality = owner.getConfig().getThumbQuality();
                if (quality <= 0) {
                    quality = 0.72f;
                }
                String extension = StringUtils.trimToNull(FileUtils.getExtName(sourceFile.getName()));
                if (StringUtils.isNotBlank(extension)) {
                    String thumbSize = String.format("%d_%d", width, height);
                    String thumbFileName = String.format("%s_%s_%d.%s", StringUtils.substringBeforeLast(sourceFile.getName(), FileUtils.POINT_CHAR), thumbSize, BlurObject.bind(quality * 100).toIntValue(), extension);
                    File thumbFile = new File(sourceFile.getParent(), thumbFileName);
                    if (!thumbFile.exists() && owner.getConfig().isAllowCustomThumbSize() && owner.getConfig().getThumbSizeList().contains(thumbSize)) {
                        if (!owner.getConfig().getImageProcessor().resize(bufferedImg, thumbFile, resizeWidth, resizeHeight, quality, extension)) {
                            return null;
                        }
                    }
                    return thumbFile;
                }
            }
        } catch (Exception e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn(StringUtils.EMPTY, RuntimeUtils.unwrapThrow(e));
            }
        }
        return null;
    }

    @Override
    public File readFile(String hash, String sourcePath) {
        return new File(owner.getConfig().getFileStoragePath(), sourcePath);
    }

    @Override
    public File readThumb(ResourceType resourceType, String hash, String sourcePath, int width, int height) {
        File targetFile = new File(owner.getConfig().getFileStoragePath(), sourcePath);
        if (targetFile.exists()) {
            if (width > 0 || height > 0) {
                File thumbFile = createThumbFileIfNeed(targetFile, null, width, height);
                if (thumbFile != null) {
                    return thumbFile;
                }
            }
        }
        return targetFile;
    }
}
