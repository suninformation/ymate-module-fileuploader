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

import net.ymate.framework.commons.DateTimeHelper;
import net.ymate.module.fileuploader.*;
import net.ymate.platform.core.lang.BlurObject;
import net.ymate.platform.core.lang.PairObject;
import net.ymate.platform.core.util.DateTimeUtils;
import net.ymate.platform.core.util.FileUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author 刘镇 (suninformation@163.com) on 16/3/31 上午1:50
 * @version 1.0
 */
public class DefaultFileStorageAdapter implements IFileStorageAdapter {

    private static final Log _LOG = LogFactory.getLog(DefaultFileStorageAdapter.class);

    private IFileUploader __owner;

    @Override
    public void init(IFileUploader owner) throws Exception {
        __owner = owner;
    }

    @Override
    public boolean isFileExists(String hash, String sourcePath) {
        File _target = new File(__owner.getModuleCfg().getFileStoragePath(), sourcePath);
        return _target.exists() && _target.isFile();
    }

    @Override
    public PairObject<Integer, String> saveFile(String hash, IFileWrapper file) throws Exception {
        IFileUploader.ResourceType _fileType = IFileUploader.ResourceType.valueOf(StringUtils.substringBefore(file.getContentType(), "/").toUpperCase());
        // 转存文件，路径格式：{TYPE_NAME}/{octal_yyyy}/{MMdd}/{HHmmss}_{FILE_HASH_8BIT}_{NODE_ID}
        StringBuilder _sourcePath = new StringBuilder(_fileType.name().toLowerCase())
                .append(File.separator)
                .append(Integer.toOctalString(DateTimeHelper.now().year()))
                .append(DateTimeUtils.formatTime(System.currentTimeMillis(), File.separator + "MMdd" + File.separator + "HHmmss_"))
                .append(StringUtils.substring(hash, 0, 8))
                .append("_").append(StringUtils.substring(DigestUtils.md5Hex(__owner.getModuleCfg().getNodeId()), 0, 8));
        // 检查并创建目标目录
        String _extension = StringUtils.trimToNull(file.getSuffix());
        String _sourcePathStr = _sourcePath.append(_extension == null ? "" : "." + _extension).toString();
        File _targetFile = new File(__owner.getModuleCfg().getFileStoragePath(), _sourcePathStr);
        file.writeTo(_targetFile);
        //
        createThumbFiles(_targetFile);
        //
        return new PairObject<Integer, String>(_fileType.type(), _sourcePathStr);
    }

    @Override
    public void createThumbFiles(File sourceFile) {
        // 判断是否允许自定义缩略图尺寸
        if (__owner.getModuleCfg().isAllowCustomThumbSize() && !__owner.getModuleCfg().getThumbSizeList().isEmpty()) {
            try {
                BufferedImage _bufferedImg = ImageIO.read(sourceFile);
                if (_bufferedImg != null) {
                    for (String _thumbSize : __owner.getModuleCfg().getThumbSizeList()) {
                        String[] _sizeArr = StringUtils.split(_thumbSize, "_");
                        // 调整宽高参数, 超出原图将不处理
                        int _width = BlurObject.bind(_sizeArr[0]).toIntValue();
                        int _height = BlurObject.bind(_sizeArr[1]).toIntValue();
                        //
                        createThumbFileIfNeed(sourceFile, _bufferedImg, _width, _height);
                    }
                }
            } catch (IOException e) {
                _LOG.warn("", e);
            }
        }
    }

    private File createThumbFileIfNeed(File sourceFile, BufferedImage bufferedImg, int width, int height) {
        try {
            if (bufferedImg == null) {
                bufferedImg = ImageIO.read(sourceFile);
            }
            if (bufferedImg != null) {
                int _oWidth = bufferedImg.getWidth();
                int _oHeight = bufferedImg.getHeight();
                // 调整宽高参数, 超出原图将不处理
                _oWidth = width >= _oWidth ? _oWidth : width;
                _oHeight = height >= _oHeight ? _oHeight : height;
                //
                float quality = __owner.getModuleCfg().getThumbQuality();
                if (quality <= 0f) {
                    quality = 0.72f;
                }
                //
                String _thumbSize = _oWidth + "_" + _oHeight;
                String _thumbFileName = sourceFile.getName().concat("_" + _thumbSize + "_" + BlurObject.bind(quality * 100).toIntValue());
                String _extension = StringUtils.trimToNull(FileUtils.getExtName(sourceFile.getName()));
                if (StringUtils.isNotBlank(_extension)) {
                    File _thumbFile = new File(sourceFile.getParent(), _thumbFileName);
                    if (!_thumbFile.exists() && __owner.getModuleCfg().isAllowCustomThumbSize() && __owner.getModuleCfg().getThumbSizeList().contains(_thumbSize)) {
                        if (!__owner.getModuleCfg().getImageFileProcessor().resize(bufferedImg, _thumbFile, _oWidth, _oHeight, quality, _extension)) {
                            return null;
                        }
                    }
                    return _thumbFile;
                }
            }
        } catch (Exception e) {
            _LOG.warn("", e);
        }
        return null;
    }

    @Override
    public File readFile(String hash, String sourcePath) {
        IFileUploaderModuleCfg _cfg = FileUploader.get().getModuleCfg();
        return new File(_cfg.getFileStoragePath(), sourcePath);
    }

    @Override
    public File readThumb(IFileUploader.ResourceType resourceType, String hash, String sourcePath, int width, int height) {
        File _targetFile = new File(__owner.getModuleCfg().getFileStoragePath(), sourcePath);
        if (_targetFile.exists()) {
            if (width != 0 || height != 0) {
                File _thumbFile = createThumbFileIfNeed(_targetFile, null, width, height);
                if (_thumbFile != null) {
                    return _thumbFile;
                }
            }
        }
        return _targetFile;
    }
}
