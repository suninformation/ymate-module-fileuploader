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

import net.ymate.module.fileuploader.AbstractResourcesProcessor;
import net.ymate.module.fileuploader.IFileWrapper;
import net.ymate.module.fileuploader.ResourceType;
import net.ymate.module.fileuploader.UploadFileMeta;
import net.ymate.platform.commons.json.JsonWrapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author 刘镇 (suninformation@163.com) on 2019-01-03 19:08
 * @since 1.0
 */
public class DefaultResourcesProcessor extends AbstractResourcesProcessor {

    private static final Log LOG = LogFactory.getLog(DefaultResourcesProcessor.class);

    private UploadFileMeta doReadFileMeta(String hash, String sourcePath, ResourceType resourceType) throws Exception {
        UploadFileMeta fileMeta = null;
        File metaFile = new File(getOwner().getConfig().getFileStoragePath(), String.format("%s/%s/.metadata/%s", resourceType.name().toLowerCase(), sourcePath, hash));
        if (metaFile.exists()) {
            fileMeta = JsonWrapper.deserialize(IOUtils.toString(new FileInputStream(metaFile), StandardCharsets.UTF_8), UploadFileMeta.class);
        }
        return fileMeta;
    }

    @Override
    protected UploadFileMeta doMatchHash(String hash, ResourceType resourceType) throws Exception {
        UploadFileMeta fileMeta = null;
        if (StringUtils.isNotBlank(hash)) {
            String sourcePath = String.format("%s/%s", StringUtils.substring(hash, 0, 2), StringUtils.substring(hash, 2, 4));
            if (resourceType == null) {
                for (ResourceType type : ResourceType.values()) {
                    fileMeta = doReadFileMeta(hash, sourcePath, type);
                    if (fileMeta != null) {
                        break;
                    }
                }
            } else {
                fileMeta = doReadFileMeta(hash, sourcePath, resourceType);
            }
        }
        return fileMeta;
    }

    @Override
    public UploadFileMeta upload(IFileWrapper fileWrapper) throws Exception {
        UploadFileMeta fileMeta = super.upload(fileWrapper);
        if (fileMeta != null) {
            String sourcePath = StringUtils.substringBeforeLast(fileMeta.getSourcePath(), URL_SEPARATOR);
            File metaFile = new File(getOwner().getConfig().getFileStoragePath(), String.format("%s/.metadata/%s", sourcePath, fileMeta.getHash()));
            if (metaFile.getParentFile().mkdirs() && LOG.isInfoEnabled()) {
                LOG.info(String.format("Successfully created directory: %s", metaFile.getParentFile().getPath()));
            }
            try (OutputStream outputStream = new FileOutputStream(metaFile)) {
                String fileMetaJson = JsonWrapper.toJsonString(fileMeta, false, false);
                IOUtils.write(fileMetaJson, outputStream, StandardCharsets.UTF_8);
            }
        }
        return fileMeta;
    }
}
