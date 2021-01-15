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
package net.ymate.module.fileuploader;

import net.ymate.platform.cache.CacheElement;
import net.ymate.platform.commons.util.FileUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 刘镇 (suninformation@163.com) on 2020/08/10 16:51
 * @since 2.0.0
 */
public abstract class AbstractResourcesProcessor implements IResourcesProcessor {

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

    public IFileUploader getOwner() {
        return owner;
    }

    protected UploadFileMeta doGetUploadFileMetaFromCache(String hash) {
        CacheElement cacheElement = (CacheElement) owner.getFileHashCache().get(FILE_META_CACHE_PREFIX + hash);
        if (cacheElement != null) {
            return (UploadFileMeta) cacheElement.getObject();
        }
        return null;
    }

    protected void doPutElementToCache(String hash, Object element) {
        owner.getFileHashCache().put(FILE_META_CACHE_PREFIX + hash, new CacheElement(element, owner.getConfig().getCacheTimeout()));
    }

    /**
     * 通过指定的哈希值和资源类型匹配对应的文件
     *
     * @param hash         文件哈希值
     * @param resourceType 资源类型
     * @return 返回匹配的文件信息对象
     * @throws Exception 可能产生的任何异常
     */
    protected abstract UploadFileMeta doMatchHash(String hash, ResourceType resourceType) throws Exception;

    @Override
    public UploadFileMeta upload(final IFileWrapper fileWrapper) throws Exception {
        String hash = DigestUtils.md5Hex(fileWrapper.getInputStream());
        UploadFileMeta fileMeta = doGetUploadFileMetaFromCache(hash);
        if (fileMeta == null) {
            ResourceType resourceType = ResourceType.valueOf(StringUtils.substringBefore(fileWrapper.getContentType(), URL_SEPARATOR).toUpperCase());
            fileMeta = doMatchHash(hash, resourceType);
            if (fileMeta == null) {
                fileMeta = getOwner().getConfig().getFileStorageAdapter().writeFile(hash, fileWrapper);
                if (fileMeta != null) {
                    doPutElementToCache(hash, fileMeta);
                    //
                    owner.getOwner().getEvents().fireEvent(new FileUploadEvent(owner, FileUploadEvent.EVENT.FILE_UPLOADED_CREATE).setEventSource(fileMeta));
                }
            }
        }
        return fileMeta;
    }

    @Override
    public UploadFileMeta matchHash(String hash) throws Exception {
        UploadFileMeta fileMeta = null;
        if (StringUtils.isNotBlank(hash)) {
            fileMeta = doGetUploadFileMetaFromCache(hash);
            if (fileMeta == null) {
                fileMeta = doMatchHash(hash, null);
                if (fileMeta != null) {
                    doPutElementToCache(hash, fileMeta);
                    //
                    owner.getOwner().getEvents().fireEvent(new FileUploadEvent(owner, FileUploadEvent.EVENT.FILE_MATCHED).setEventSource(fileMeta));
                }
            }
        }
        return fileMeta;
    }

    @Override
    public UploadFileMeta getResource(ResourceType resourceType, String hash) throws Exception {
        if (resourceType != null && StringUtils.isNotBlank(hash)) {
            UploadFileMeta fileMeta = doMatchHash(hash, resourceType);
            if (fileMeta != null) {
                fileMeta.setFilename(StringUtils.join(new Object[]{fileMeta.getHash(), fileMeta.getExtension()}, FileUtils.POINT_CHAR));
                if (isAccessNotAllowed(fileMeta)) {
                    throw new ResourcesAccessException(resourceType, hash);
                }
                owner.getOwner().getEvents().fireEvent(new FileUploadEvent(owner, FileUploadEvent.EVENT.FILE_DOWNLOADED).setEventSource(fileMeta));
                return fileMeta;
            }
        }
        return null;
    }

    @Override
    public boolean isAccessNotAllowed(UploadFileMeta fileMeta) {
        return false;
    }

    @Override
    public UploadFileMeta proxyUpload(IFileWrapper fileWrapper) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public UploadFileMeta proxyMatchHash(String hash) throws Exception {
        throw new UnsupportedOperationException();
    }
}
