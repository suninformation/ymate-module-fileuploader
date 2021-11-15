/*
 * Copyright 2007-2021 the original author or authors.
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

import net.ymate.platform.commons.lang.BlurObject;
import net.ymate.platform.commons.util.FileUtils;
import net.ymate.platform.commons.util.RuntimeUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author 刘镇 (suninformation@163.com) on 2021/11/14 11:41 上午
 * @since 1.0.0
 */
public abstract class AbstractFileStorageAdapter implements IFileStorageAdapter {

    private static final Log LOG = LogFactory.getLog(AbstractFileStorageAdapter.class);

    private IFileUploader owner;

    private boolean initialized;

    @Override
    public void initialize(IFileUploader owner) throws Exception {
        if (!initialized) {
            this.owner = owner;
            doInitialize();
            this.initialized = true;
        }
    }

    /**
     * 执行初始化
     *
     * @throws Exception 初始过程中产生的任何异常
     */
    protected abstract void doInitialize() throws Exception;

    protected IFileUploader getOwner() {
        return this.owner;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    protected File doCheckAndFixStorageDir(String storageName, File storageDir, boolean needWriteLog) {
        if (!storageDir.exists()) {
            if (storageDir.mkdirs()) {
                if (needWriteLog && LOG.isInfoEnabled()) {
                    LOG.info(String.format("Successfully created %s directory: %s", storageName, storageDir.getPath()));
                }
            } else {
                throw new IllegalArgumentException(String.format("Failed to create %s directory: %s", storageName, storageDir.getPath()));
            }
        } else if (!storageDir.isAbsolute() || !storageDir.isDirectory() || !storageDir.canRead() || !storageDir.canWrite()) {
            throw new IllegalArgumentException(String.format("Parameter %s value [%s] is invalid or is not a directory", storageName, storageDir.getPath()));
        }
        return storageDir;
    }

    protected void doCreateThumbFilesIfNeed(File targetFile, File distDir) {
        // 判断若已允许则尝试生成自定义尺寸的全部缩略图
        if (owner.getConfig().isAllowCustomThumbSize() && !owner.getConfig().getThumbSizeList().isEmpty()) {
            try {
                doCreateThumbFiles(ImageIO.read(targetFile), targetFile.getName(), distDir, owner.getConfig().getThumbSizeList());
            } catch (IOException e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn(StringUtils.EMPTY, RuntimeUtils.unwrapThrow(e));
                }
            }
        }
    }

    protected void doCreateThumbFiles(BufferedImage bufferedImage, String fileName, String distDir, List<String> thumbSizeList) {
        if (StringUtils.isBlank(distDir)) {
            throw new NullArgumentException("distDir");
        }
        doCreateThumbFiles(bufferedImage, fileName, new File(distDir), thumbSizeList);
    }

    protected void doCreateThumbFiles(BufferedImage bufferedImage, String fileName, File distDir, List<String> thumbSizeList) {
        if (StringUtils.isBlank(fileName)) {
            throw new NullArgumentException("fileName");
        }
        if (distDir == null) {
            throw new NullArgumentException("distDir");
        }
        if (bufferedImage != null && !thumbSizeList.isEmpty()) {
            for (String thumbSize : thumbSizeList) {
                String[] sizeArr = StringUtils.split(thumbSize, "_");
                // 调整宽高参数, 超出原图将不处理
                int width = BlurObject.bind(sizeArr[0]).toIntValue();
                int height = BlurObject.bind(sizeArr[1]).toIntValue();
                //
                doCreateThumbFileIfNeed(bufferedImage, fileName, distDir, width, height);
            }
        }
    }

    protected File doCreateThumbFileIfNeed(BufferedImage bufferedImg, String fileName, String distDir, int width, int height) {
        if (StringUtils.isBlank(distDir)) {
            throw new NullArgumentException("distDir");
        }
        return doCreateThumbFileIfNeed(bufferedImg, fileName, new File(distDir), width, height);
    }

    protected File doCreateThumbFileIfNeed(BufferedImage bufferedImg, String fileName, File distDir, int width, int height) {
        if (StringUtils.isBlank(fileName)) {
            throw new NullArgumentException("fileName");
        }
        if (distDir == null) {
            throw new NullArgumentException("distDir");
        }
        if (bufferedImg != null) {
            int originWidth = bufferedImg.getWidth();
            int originHeight = bufferedImg.getHeight();
            // 调整宽高参数, 超出原图将不处理
            int resizeWidth = Math.min(width, originWidth);
            int resizeHeight = Math.min(height, originHeight);
            if (resizeWidth == 0 && resizeHeight == 0) {
                resizeWidth = originWidth;
                resizeHeight = originHeight;
            } else if (resizeWidth == 0) {
                resizeWidth = Long.valueOf(Math.round(originWidth * height * 1.0 / originHeight)).intValue();
            } else if (resizeHeight == 0) {
                resizeHeight = Long.valueOf(Math.round(originHeight * width * 1.0 / originWidth)).intValue();
            }
            //
            String extension = StringUtils.trimToNull(FileUtils.getExtName(fileName));
            if (StringUtils.isNotBlank(extension)) {
                distDir = doCheckAndFixStorageDir("distDir", distDir, false);
                File thumbFile = new File(distDir, doBuildThumbFileName(fileName, extension, width, height));
                if (!thumbFile.exists()) {
                    if (!owner.getConfig().getImageProcessor().resize(bufferedImg, thumbFile, resizeWidth, resizeHeight, doGetQuality(), extension)) {
                        return null;
                    } else if (LOG.isInfoEnabled()) {
                        LOG.info(String.format("Successfully created thumbnail file: %s", thumbFile.getPath()));
                    }
                }
                return thumbFile;
            }
        }
        return null;
    }

    protected String doBuildThumbFileName(String fileName, String extension, int width, int height) {
        String thumbSize = String.format("%d_%d", width, height);
        return String.format("%s_%s_%d.%s", StringUtils.substringBeforeLast(fileName, FileUtils.POINT_CHAR), thumbSize, BlurObject.bind(doGetQuality() * 100).toIntValue(), extension);
    }

    protected float doGetQuality() {
        float quality = owner.getConfig().getThumbQuality();
        if (quality <= 0) {
            quality = 0.72f;
        }
        return quality;
    }

    protected File doGetThumbFileIfExists(File distDir, String fileName, int width, int height) {
        String extension = StringUtils.trimToNull(FileUtils.getExtName(fileName));
        if (StringUtils.isNotBlank(extension)) {
            File thumbFile = new File(distDir, doBuildThumbFileName(fileName, extension, width, height));
            if (thumbFile.exists()) {
                return thumbFile;
            }
        }
        return null;
    }
}
