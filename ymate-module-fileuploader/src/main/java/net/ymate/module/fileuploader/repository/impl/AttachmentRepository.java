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
package net.ymate.module.fileuploader.repository.impl;

import net.ymate.framework.core.util.WebUtils;
import net.ymate.module.fileuploader.*;
import net.ymate.module.fileuploader.repository.IAttachmentRepository;
import net.ymate.module.fileuploader.model.Attachment;
import net.ymate.platform.cache.CacheElement;
import net.ymate.platform.core.YMP;
import net.ymate.platform.core.beans.annotation.Bean;
import net.ymate.platform.core.lang.PairObject;
import net.ymate.platform.core.util.FileUtils;
import net.ymate.platform.core.util.UUIDUtils;
import net.ymate.platform.persistence.Fields;
import net.ymate.platform.persistence.jdbc.annotation.Transaction;
import net.ymate.platform.persistence.jdbc.query.IDBLocker;
import net.ymate.platform.webmvc.IUploadFileWrapper;
import net.ymate.platform.webmvc.context.WebContext;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;

/**
 * @author 刘镇 (suninformation@163.com) on 16/11/3 上午4:11
 * @version 1.0
 */
@Bean
@Transaction
public class AttachmentRepository implements IAttachmentRepository {

    private String __doGetResourceURLFromCache(String hash) {
        CacheElement _element = (CacheElement) FileUploader.get().getMatchHashCache().get("res_hash_" + hash);
        if (_element != null) {
            return (String) _element.getObject();
        }
        return null;
    }

    private UploadFileMeta __doGetUploadFileMetaFromCache(String hash) {
        CacheElement _element = (CacheElement) FileUploader.get().getMatchHashCache().get("meta_hash_" + hash);
        if (_element != null) {
            return (UploadFileMeta) _element.getObject();
        }
        return null;
    }

    private void __doPutElementToCache(String prefix, String hash, Object element) {
        FileUploader.get().getMatchHashCache().put(prefix + hash, new CacheElement(element, FileUploader.get().getModuleCfg().getCacheTimeout()));
    }

    @Transaction
    public UploadFileMeta uploadFile(IUploadFileWrapper fileWrapper) throws Exception {
        // 通过对文件签名的方式获取唯一ID
        String _hash = DigestUtils.md5Hex(fileWrapper.getInputStream());
        // 先尝试从缓存中加载
        UploadFileMeta _fileMeta = __doGetUploadFileMetaFromCache(_hash);
        if (_fileMeta == null) {
            _fileMeta = new UploadFileMeta();
            _fileMeta.setHash(_hash);
            _fileMeta.setTitle(StringUtils.substringBeforeLast(fileWrapper.getName(), "."));
            _fileMeta.setFilename(fileWrapper.getName());
            _fileMeta.setSize(fileWrapper.getSize());
            //
            IFileStorageAdapter _storageAdapter = FileUploader.get().getModuleCfg().getFileStorageAdapter();
            Attachment _attach = __doMatchHash(_hash, null, Fields.create(Attachment.FIELDS.ID, Attachment.FIELDS.TYPE, Attachment.FIELDS.SOURCE_PATH));
            if (_attach != null) {
                // 若记录存在但文件并不存在时
                if (!_storageAdapter.isFileExists(_hash, _attach.getSourcePath())) {
                    // 加锁
                    Attachment.builder().id(_attach.getId()).build().load(Fields.create(Attachment.FIELDS.ID), IDBLocker.MYSQL);
                    // 保存文件
                    PairObject<Integer, String> _result = _storageAdapter.saveFile(_hash, fileWrapper);
                    // 更新记录
                    _attach = new Attachment.AttachmentBuilder(_attach).staticUrl(FileUploader.get().getModuleCfg().getResourcesBaseUrl())
                            .sourcePath(_result.getValue())
                            .lastModifyTime(System.currentTimeMillis())
                            .size(fileWrapper.getSize())
                            .build().update(Fields.create(Attachment.FIELDS.STATIC_URL, Attachment.FIELDS.SOURCE_PATH, Attachment.FIELDS.LAST_MODIFY_TIME, Attachment.FIELDS.SIZE));
                    //
                    YMP.get().getEvents().fireEvent(new FileUploadEvent(FileUploader.get(), FileUploadEvent.EVENT.FILE_UPLOADED_UPDATE).setEventSource(_attach));
                }
                _fileMeta.setType(IFileUploader.ResourceType.valueOf(_attach.getType()));
                _fileMeta.setUrl(__doBuildResourceUrl(_hash, _fileMeta.getType(), _attach.getSourcePath()));
            } else {
                // 若记录不存在时, 尝试保存文件
                PairObject<Integer, String> _result = _storageAdapter.saveFile(_hash, fileWrapper);
                if (_result != null) {
                    _fileMeta.setType(IFileUploader.ResourceType.valueOf(_result.getKey()));
                    _fileMeta.setUrl(__doBuildResourceUrl(_hash, _fileMeta.getType(), _result.getValue()));
                    // 插入文件记录
                    _attach = Attachment.builder().id(UUIDUtils.UUID())
                            .hash(_hash)
                            .type(_result.getKey())
                            .uid(IFileUploader.MODULE_NAME)
                            .siteId("default")
                            .staticUrl(FileUploader.get().getModuleCfg().getResourcesBaseUrl())
                            .sourcePath(_result.getValue())
                            .mimeType(fileWrapper.getContentType())
                            .createTime(System.currentTimeMillis())
                            .extension(FileUtils.getExtName(fileWrapper.getName()))
                            .size(fileWrapper.getSize())
                            .build().save();
                    //
                    YMP.get().getEvents().fireEvent(new FileUploadEvent(FileUploader.get(), FileUploadEvent.EVENT.FILE_UPLOADED_CREATE).setEventSource(_attach));
                }
            }
            // 缓存
            __doPutElementToCache("meta_hash_", _hash, _fileMeta);
        }
        return _fileMeta;
    }

    private Attachment __doMatchHash(String hash, IFileUploader.ResourceType resourceType, Fields fields) throws Exception {
        if (StringUtils.isNotBlank(hash)) {
            Attachment.AttachmentBuilder _builder = Attachment.builder()
                    .hash(hash)
                    .siteId("default");
            if (resourceType != null) {
                _builder.type(resourceType.type());
            }
            if (fields != null) {
                return _builder.build().findFirst(fields);
            } else {
                return _builder.build().findFirst();
            }
        }
        return null;
    }

    private String __doBuildResourceUrl(String hash, IFileUploader.ResourceType type, String sourcePath) {
        String _resourcesBaseUrl = FileUploader.get().getModuleCfg().getResourcesBaseUrl();
        if (_resourcesBaseUrl != null) {
            sourcePath = StringUtils.replaceChars(sourcePath, File.separatorChar, '/');
            if (StringUtils.startsWith(sourcePath, "/")) {
                sourcePath = StringUtils.substringAfter(sourcePath, "/");
            }
            return _resourcesBaseUrl + sourcePath;
        }
        _resourcesBaseUrl = type.name().toLowerCase() + "/" + hash;
        return WebUtils.buildURL(WebContext.getRequest(), "/uploads/resources/" + _resourcesBaseUrl, true);
    }

    public String matchHash(String hash) throws Exception {
        String _returnValue = null;
        if (StringUtils.isNotBlank(hash)) {
            // 先尝试从缓存中加载
            _returnValue = __doGetResourceURLFromCache(hash);
            if (StringUtils.isBlank(_returnValue)) {
                Attachment _attach = __doMatchHash(hash, null, Fields.create(Attachment.FIELDS.TYPE, Attachment.FIELDS.SOURCE_PATH));
                if (_attach != null) {
                    _attach.setHash(hash);
                    _attach.setSiteId("default");
                    _returnValue = __doBuildResourceUrl(hash, IFileUploader.ResourceType.valueOf(_attach.getType()), _attach.getSourcePath());
                    // 缓存
                    __doPutElementToCache("res_hash_", hash, _returnValue);
                    //
                    YMP.get().getEvents().fireEvent(new FileUploadEvent(FileUploader.get(), FileUploadEvent.EVENT.FILE_MATCHED).setEventSource(_attach));
                }
            }
        }
        return _returnValue;
    }

    public Attachment getResource(IFileUploader.ResourceType resourceType, String hash) throws Exception {
        if (resourceType == null || StringUtils.isBlank(hash)) {
            return null;
        }
        Attachment _attach = __doMatchHash(hash, resourceType, Fields.create(Attachment.FIELDS.ID)
                .add(Attachment.FIELDS.SIZE)
                .add(Attachment.FIELDS.MIME_TYPE)
                .add(Attachment.FIELDS.SOURCE_PATH)
                .add(Attachment.FIELDS.EXTENSION)
                .add(Attachment.FIELDS.STATUS));
        if (_attach != null) {
            _attach.setHash(hash);
            _attach.setSiteId("default");
            YMP.get().getEvents().fireEvent(new FileUploadEvent(FileUploader.get(), FileUploadEvent.EVENT.FILE_DOWNLOADED).setEventSource(_attach));
        }
        return _attach;
    }
}
