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

import net.ymate.platform.commons.FFmpegHelper;
import net.ymate.platform.commons.lang.BlurObject;
import net.ymate.platform.commons.util.ClassUtils;
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
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 2021/11/14 11:41 上午
 * @since 2.0.0
 */
public abstract class AbstractFileStorageAdapter implements IFileStorageAdapter {

    private static final Log LOG = LogFactory.getLog(AbstractFileStorageAdapter.class);

    private IFileUploader owner;

    private IFileAttributeBuilder fileAttributeBuilder;

    private boolean initialized;

    @Override
    public void initialize(IFileUploader owner) throws Exception {
        if (!initialized) {
            this.owner = owner;
            this.fileAttributeBuilder = ClassUtils.loadClass(IFileAttributeBuilder.class);
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

    @Override
    public void doAfterWriteFile(ResourceType resourceType, File targetFile, String sourcePathDir, String thumbStoragePath, String hash) {
        if (targetFile != null && targetFile.exists()) {
            // 若为视频文件类型则创建截图
            if (resourceType.equals(ResourceType.VIDEO)) {
                targetFile = doCreateVideoScreenshot(resourceType, targetFile, thumbStoragePath, hash);
                resourceType = ResourceType.THUMB;
            }
            // 尝试生成限定的缩略图文件
            if (targetFile != null && targetFile.exists() && getOwner().getConfig().isThumbCreateOnUploaded()) {
                if (resourceType.equals(ResourceType.IMAGE)) {
                    doCreateThumbFilesIfNeed(targetFile, new File(thumbStoragePath, sourcePathDir));
                } else if (resourceType.equals(ResourceType.THUMB)) {
                    doCreateThumbFilesIfNeed(targetFile, targetFile.getParentFile());
                }
            }
        }
    }

    @Override
    public Map<String, Object> doBuildFileAttributes(String hash, ResourceType resourceType, IFileWrapper file) {
        return fileAttributeBuilder != null ? fileAttributeBuilder.build(hash, resourceType, file) : null;
    }

    @Override
    public File readThumb(ResourceType resourceType, String hash, String sourcePath, int width, int height) {
        File targetFile = null;
        File thumbStoragePath = getThumbStoragePath();
        if (resourceType.equals(ResourceType.IMAGE)) {
            File thumbFile = new File(thumbStoragePath, sourcePath);
            thumbFile = doGetThumbFileIfExists(thumbFile.getParentFile(), thumbFile.getName(), width, height);
            if (thumbFile != null) {
                return thumbFile;
            }
            targetFile = readFile(hash, sourcePath);
        } else if (resourceType.equals(ResourceType.THUMB)) {
            String screenshotSourcePath = doBuildVideoScreenshotFileName(hash);
            targetFile = new File(thumbStoragePath, screenshotSourcePath);
            if (!targetFile.exists()) {
                // 重新生成视频截图
                targetFile = doCreateVideoScreenshot(ResourceType.VIDEO, readFile(hash, sourcePath), thumbStoragePath.getPath(), hash);
            }
            sourcePath = screenshotSourcePath;
        }
        return doReadThumb(resourceType, targetFile, thumbStoragePath.getPath(), sourcePath, width, height);
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

    protected File doReadThumb(ResourceType resourceType, File targetFile, String thumbStoragePath, String sourcePath, int width, int height) {
        if (targetFile != null && targetFile.exists() && resourceType.isImage()) {
            if (width > 0 || height > 0) {
                try {
                    File thumbFile = doGetThumbFileIfExists(new File(thumbStoragePath, sourcePath).getParentFile(), targetFile.getName(), width, height);
                    if (thumbFile == null) {
                        thumbFile = doCreateThumbFileIfNeed(ImageIO.read(targetFile), targetFile.getName(), new File(thumbStoragePath, sourcePath).getParentFile(), width, height);
                    }
                    if (thumbFile != null) {
                        return thumbFile;
                    }
                } catch (IOException e) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn(StringUtils.EMPTY, RuntimeUtils.unwrapThrow(e));
                    }
                }
            }
        }
        return targetFile;
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

    protected String doBuildVideoScreenshotFileName(String hash) {
        return String.format("%s/%s.jpeg", UploadFileMeta.buildSourcePath(ResourceType.THUMB, hash), hash);
    }

    protected File doCreateVideoScreenshot(ResourceType resourceType, File targetFile, String thumbStoragePath, String hash) {
        if (targetFile != null && resourceType.equals(ResourceType.VIDEO)) {
            File screenshotFile = new File(thumbStoragePath, doBuildVideoScreenshotFileName(hash));
            if (!screenshotFile.exists()) {
                doCheckAndFixStorageDir("screenshotFileDir", screenshotFile.getParentFile(), false);
                // 通过FFmpeg工具提取视频文件指定帧截图
                FFmpegHelper ffmpeghelper = FFmpegHelper.create().bind(targetFile);
                FFmpegHelper.MediaInfo mediaInfo = ffmpeghelper.getMediaInfo();
                if (mediaInfo != null) {
                    // 截取视频中间时间的一张图片
                    int time = Math.min(1, mediaInfo.getTime() / 2);
                    screenshotFile = ffmpeghelper.screenshotVideo(time, 0, 0, 0, screenshotFile);
                    if (screenshotFile != null && LOG.isInfoEnabled()) {
                        LOG.info(String.format("Successfully created screenshot: %s", screenshotFile.getPath()));
                    }
                    return screenshotFile;
                }
            } else {
                return screenshotFile;
            }
        }
        return null;
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
            width = Math.max(width, 0);
            height = Math.max(height, 0);
            if (width > 0 || height > 0) {
                int originWidth = bufferedImg.getWidth();
                int originHeight = bufferedImg.getHeight();
                // 调整宽高参数, 超出原图将不处理
                int resizeWidth = Math.min(width, originWidth);
                int resizeHeight = Math.min(height, originHeight);
                if (resizeWidth <= 0 && resizeHeight <= 0) {
                    resizeWidth = originWidth;
                    resizeHeight = originHeight;
                } else if (resizeWidth <= 0) {
                    resizeWidth = -1;
                } else if (resizeHeight <= 0) {
                    resizeHeight = -1;
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
}
