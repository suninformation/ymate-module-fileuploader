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
package net.ymate.module.fileuploader.mongodb;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.gridfs.GridFS;
import net.ymate.module.fileuploader.*;
import net.ymate.platform.commons.DateTimeHelper;
import net.ymate.platform.commons.lang.PairObject;
import net.ymate.platform.commons.util.FileUtils;
import net.ymate.platform.commons.util.RuntimeUtils;
import net.ymate.platform.persistence.mongodb.IMongo;
import net.ymate.platform.persistence.mongodb.MongoDB;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 2021/11/14 1:09 上午
 * @since 2.0.0
 */
public class MongoGridFileStorageAdapter extends AbstractFileStorageAdapter {

    private static final Log LOG = LogFactory.getLog(MongoGridFileStorageAdapter.class);

    public static PairObject<String, String> parseMongoDataSourceCfg(IFileUploader owner, IMongo mongo) {
        String[] values = StringUtils.split(StringUtils.defaultIfBlank(owner.getConfig().getFileStoragePath(), GridFS.DEFAULT_BUCKET), ":");
        if (values.length > 1) {
            return PairObject.bind(values[0], values[1]);
        }
        return PairObject.bind(mongo.getConfig().getDefaultDataSourceName(), values[0]);
    }

    public static UploadFileMeta buildUploadFileMeta(String hash, GridFSFile fsFile) {
        if (StringUtils.isNotBlank(hash) && fsFile != null) {
            Document metadata = fsFile.getMetadata() != null ? fsFile.getMetadata() : new Document();
            long uploadDate = DateTimeHelper.bind(fsFile.getUploadDate()).timeMillis();
            return UploadFileMeta.builder()
                    .hash(hash)
                    .filename(fsFile.getFilename())
                    .extension(metadata.getString("extension"))
                    .size(fsFile.getLength())
                    .mimeType(metadata.getString("contentType"))
                    .type(ResourceType.valueOf(metadata.getString("resourceType")))
                    .status(metadata.getInteger("status", 0))
                    .sourcePath(metadata.getString("sourcePath"))
                    .createTime(uploadDate)
                    .lastModifyTime(uploadDate)
                    .attributes((Document) metadata.get("attributes"))
                    .build();
        }
        return null;
    }

    private IMongo mongo;

    private PairObject<String, String> dataSourceCfg;

    private File thumbStoragePath;

    @Override
    protected void doInitialize() throws Exception {
        this.mongo = getOwner().getOwner().getModuleManager().getModule(MongoDB.class);
        this.dataSourceCfg = parseMongoDataSourceCfg(getOwner(), this.mongo);
        this.thumbStoragePath = doCheckAndFixStorageDir(IFileUploaderConfig.THUMB_STORAGE_PATH, new File(RuntimeUtils.replaceEnvVariable(StringUtils.defaultIfBlank(getOwner().getConfig().getThumbStoragePath(), IFileUploaderConfig.DEFAULT_STORAGE_PATH))), true);
    }

    @Override
    public boolean isExists(UploadFileMeta fileMeta) throws Exception {
        return mongo.openGridFsSession(dataSourceCfg.getKey(), dataSourceCfg.getValue(), session -> session.match(fileMeta.getHash()) != null);
    }

    @Override
    public UploadFileMeta writeFile(String hash, IFileWrapper file) throws Exception {
        return mongo.openGridFsSession(dataSourceCfg.getKey(), dataSourceCfg.getValue(), session -> {
            GridFSFile fsFile = session.match(hash);
            if (fsFile == null) {
                ResourceType resourceType = ResourceType.valueOf(StringUtils.substringBefore(file.getContentType(), IResourcesProcessor.URL_SEPARATOR).toUpperCase());
                String sourcePathDir = UploadFileMeta.buildSourcePath(resourceType, hash);
                String extension = StringUtils.trimToNull(file.getSuffix());
                String filename = StringUtils.join(new Object[]{hash, extension}, FileUtils.POINT_CHAR);
                String sourcePath = String.format("%s/%s", sourcePathDir, filename);
                Document metadata = new Document()
                        .append("resourceType", resourceType.name())
                        .append("extension", extension)
                        .append("contentType", file.getContentType())
                        .append("status", 0)
                        .append("sourcePath", sourcePath);
                Map<String, Object> attributes = doBuildFileAttributes(hash, resourceType, file);
                if (attributes != null && !attributes.isEmpty()) {
                    metadata.append("attributes", new Document(attributes));
                }
                try (InputStream inputStream = file.getInputStream()) {
                    fsFile = session.find(session.upload(filename, inputStream, new GridFSUploadOptions().metadata(metadata)));
                }
                //
                doAfterWriteFile(resourceType, file.getFile(), sourcePathDir, thumbStoragePath.getPath(), hash);
            }
            return buildUploadFileMeta(hash, fsFile);
        });
    }

    @Override
    public File readFile(String hash, String sourcePath) {
        File tmpFile = new File(thumbStoragePath, sourcePath);
        if (!tmpFile.exists()) {
            try {
                return mongo.openGridFsSession(dataSourceCfg.getKey(), dataSourceCfg.getValue(), session -> {
                    GridFSFile fsFile = session.match(hash);
                    if (fsFile != null) {
                        UploadFileMeta fileMeta = buildUploadFileMeta(hash, fsFile);
                        if (fileMeta != null && StringUtils.equals(sourcePath, fileMeta.getSourcePath())) {
                            doCheckAndFixStorageDir("tmpFileSourcePath", tmpFile.getParentFile(), false);
                            session.download(fsFile.getObjectId().toString(), tmpFile);
                            return tmpFile;
                        }
                    }
                    return null;
                });
            } catch (Exception e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn(StringUtils.EMPTY, RuntimeUtils.unwrapThrow(e));
                }
            }
        }
        return tmpFile;
    }

    @Override
    public File getThumbStoragePath() {
        return thumbStoragePath;
    }
}
