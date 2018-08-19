package net.ymate.module.fileuploader.model;

import net.ymate.platform.core.beans.annotation.PropertyState;
import net.ymate.platform.persistence.IShardingable;
import net.ymate.platform.persistence.annotation.*;
import net.ymate.platform.persistence.jdbc.IConnectionHolder;
import net.ymate.platform.persistence.jdbc.support.BaseEntity;

/**
 * Attachment generated By EntityGenerator on 2016/11/03 上午 04:21:08
 *
 * @author YMP
 * @version 1.0
 */
@Entity(Attachment.TABLE_NAME)
public class Attachment extends BaseEntity<Attachment, java.lang.String> {

    private static final long serialVersionUID = 1L;

    @Id
    @Property(name = FIELDS.ID, nullable = false, length = 32)
    @PropertyState(propertyName = FIELDS.ID)
    private java.lang.String id;

    @Property(name = FIELDS.HASH, nullable = false, length = 32)
    @PropertyState(propertyName = FIELDS.HASH)
    private java.lang.String hash;

    @Property(name = FIELDS.STATIC_URL, length = 255)
    @PropertyState(propertyName = FIELDS.STATIC_URL)
    private java.lang.String staticUrl;

    @Property(name = FIELDS.SOURCE_PATH, nullable = false, length = 255)
    @PropertyState(propertyName = FIELDS.SOURCE_PATH)
    private java.lang.String sourcePath;

    @Property(name = FIELDS.EXTENSION, length = 10)
    @PropertyState(propertyName = FIELDS.EXTENSION)
    private java.lang.String extension;

    @Property(name = FIELDS.MIME_TYPE, nullable = false, length = 100)
    @PropertyState(propertyName = FIELDS.MIME_TYPE)
    private java.lang.String mimeType;

    @Property(name = FIELDS.FILE_SIZE, length = 20)
    @Default("0")
    @PropertyState(propertyName = FIELDS.FILE_SIZE)
    private java.lang.Long fileSize;

    @Property(name = FIELDS.TYPE, unsigned = true, length = 2)
    @Default("0")
    @PropertyState(propertyName = FIELDS.TYPE)
    private java.lang.Integer type;

    @Property(name = FIELDS.STATUS, unsigned = true, length = 2)
    @Default("0")
    @PropertyState(propertyName = FIELDS.STATUS)
    private java.lang.Integer status;

    @Property(name = FIELDS.CREATE_TIME, nullable = false, length = 13)
    @PropertyState(propertyName = FIELDS.CREATE_TIME)
    @Readonly
    private java.lang.Long createTime;

    @Property(name = FIELDS.LAST_MODIFY_TIME, length = 13)
    @Default("0")
    @PropertyState(propertyName = FIELDS.LAST_MODIFY_TIME)
    private java.lang.Long lastModifyTime;

    /**
     * 构造器
     */
    public Attachment() {
    }

    /**
     * 构造器
     *
     * @param id
     * @param hash
     * @param sourcePath
     * @param mimeType
     * @param createTime
     */
    public Attachment(java.lang.String id, java.lang.String hash, java.lang.String sourcePath, java.lang.String mimeType, java.lang.Long createTime) {
        this.id = id;
        this.hash = hash;
        this.sourcePath = sourcePath;
        this.mimeType = mimeType;
        this.createTime = createTime;
    }

    /**
     * 构造器
     *
     * @param id
     * @param hash
     * @param staticUrl
     * @param sourcePath
     * @param extension
     * @param mimeType
     * @param fileSize
     * @param type
     * @param status
     * @param createTime
     * @param lastModifyTime
     */
    public Attachment(java.lang.String id, java.lang.String hash, java.lang.String staticUrl, java.lang.String sourcePath, java.lang.String extension, java.lang.String mimeType, java.lang.Long fileSize, java.lang.Integer type, java.lang.Integer status, java.lang.Long createTime, java.lang.Long lastModifyTime) {
        this.id = id;
        this.hash = hash;
        this.staticUrl = staticUrl;
        this.sourcePath = sourcePath;
        this.extension = extension;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.type = type;
        this.status = status;
        this.createTime = createTime;
        this.lastModifyTime = lastModifyTime;
    }

    @Override
    public java.lang.String getId() {
        return id;
    }

    @Override
    public void setId(java.lang.String id) {
        this.id = id;
    }


    /**
     * @return the hash
     */
    public java.lang.String getHash() {
        return hash;
    }

    /**
     * @param hash the hash to set
     */
    public void setHash(java.lang.String hash) {
        this.hash = hash;
    }

    /**
     * @return the staticUrl
     */
    public java.lang.String getStaticUrl() {
        return staticUrl;
    }

    /**
     * @param staticUrl the staticUrl to set
     */
    public void setStaticUrl(java.lang.String staticUrl) {
        this.staticUrl = staticUrl;
    }

    /**
     * @return the sourcePath
     */
    public java.lang.String getSourcePath() {
        return sourcePath;
    }

    /**
     * @param sourcePath the sourcePath to set
     */
    public void setSourcePath(java.lang.String sourcePath) {
        this.sourcePath = sourcePath;
    }

    /**
     * @return the extension
     */
    public java.lang.String getExtension() {
        return extension;
    }

    /**
     * @param extension the extension to set
     */
    public void setExtension(java.lang.String extension) {
        this.extension = extension;
    }

    /**
     * @return the mimeType
     */
    public java.lang.String getMimeType() {
        return mimeType;
    }

    /**
     * @param mimeType the mimeType to set
     */
    public void setMimeType(java.lang.String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * @return the fileSize
     */
    public java.lang.Long getFileSize() {
        return fileSize;
    }

    /**
     * @param fileSize the fileSize to set
     */
    public void setFileSize(java.lang.Long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * @return the type
     */
    public java.lang.Integer getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(java.lang.Integer type) {
        this.type = type;
    }

    /**
     * @return the status
     */
    public java.lang.Integer getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(java.lang.Integer status) {
        this.status = status;
    }

    /**
     * @return the createTime
     */
    public java.lang.Long getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime the createTime to set
     */
    public void setCreateTime(java.lang.Long createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the lastModifyTime
     */
    public java.lang.Long getLastModifyTime() {
        return lastModifyTime;
    }

    /**
     * @param lastModifyTime the lastModifyTime to set
     */
    public void setLastModifyTime(java.lang.Long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }


    //
    // Chain
    //

    public static AttachmentBuilder builder() {
        return new AttachmentBuilder();
    }

    public AttachmentBuilder bind() {
        return new AttachmentBuilder(this);
    }

    public static class AttachmentBuilder {

        private Attachment _model;

        public AttachmentBuilder() {
            _model = new Attachment();
        }

        public AttachmentBuilder(Attachment model) {
            _model = model;
        }

        public Attachment build() {
            return _model;
        }


        public IConnectionHolder connectionHolder() {
            return _model.getConnectionHolder();
        }

        public AttachmentBuilder connectionHolder(IConnectionHolder connectionHolder) {
            _model.setConnectionHolder(connectionHolder);
            return this;
        }

        public String dataSourceName() {
            return _model.getDataSourceName();
        }

        public AttachmentBuilder dataSourceName(String dsName) {
            _model.setDataSourceName(dsName);
            return this;
        }

        public IShardingable shardingable() {
            return _model.getShardingable();
        }

        public AttachmentBuilder shardingable(IShardingable shardingable) {
            _model.setShardingable(shardingable);
            return this;
        }

        public java.lang.String id() {
            return _model.getId();
        }

        public AttachmentBuilder id(java.lang.String id) {
            _model.setId(id);
            return this;
        }

        public java.lang.String hash() {
            return _model.getHash();
        }

        public AttachmentBuilder hash(java.lang.String hash) {
            _model.setHash(hash);
            return this;
        }

        public java.lang.String staticUrl() {
            return _model.getStaticUrl();
        }

        public AttachmentBuilder staticUrl(java.lang.String staticUrl) {
            _model.setStaticUrl(staticUrl);
            return this;
        }

        public java.lang.String sourcePath() {
            return _model.getSourcePath();
        }

        public AttachmentBuilder sourcePath(java.lang.String sourcePath) {
            _model.setSourcePath(sourcePath);
            return this;
        }

        public java.lang.String extension() {
            return _model.getExtension();
        }

        public AttachmentBuilder extension(java.lang.String extension) {
            _model.setExtension(extension);
            return this;
        }

        public java.lang.String mimeType() {
            return _model.getMimeType();
        }

        public AttachmentBuilder mimeType(java.lang.String mimeType) {
            _model.setMimeType(mimeType);
            return this;
        }

        public java.lang.Long fileSize() {
            return _model.getFileSize();
        }

        public AttachmentBuilder fileSize(java.lang.Long fileSize) {
            _model.setFileSize(fileSize);
            return this;
        }

        public java.lang.Integer type() {
            return _model.getType();
        }

        public AttachmentBuilder type(java.lang.Integer type) {
            _model.setType(type);
            return this;
        }

        public java.lang.Integer status() {
            return _model.getStatus();
        }

        public AttachmentBuilder status(java.lang.Integer status) {
            _model.setStatus(status);
            return this;
        }

        public java.lang.Long createTime() {
            return _model.getCreateTime();
        }

        public AttachmentBuilder createTime(java.lang.Long createTime) {
            _model.setCreateTime(createTime);
            return this;
        }

        public java.lang.Long lastModifyTime() {
            return _model.getLastModifyTime();
        }

        public AttachmentBuilder lastModifyTime(java.lang.Long lastModifyTime) {
            _model.setLastModifyTime(lastModifyTime);
            return this;
        }

    }

    /**
     * Attachment 字段常量表
     */
    public class FIELDS {
        public static final String ID = "id";
        public static final String HASH = "hash";
        public static final String STATIC_URL = "static_url";
        public static final String SOURCE_PATH = "source_path";
        public static final String EXTENSION = "extension";
        public static final String MIME_TYPE = "mime_type";
        public static final String FILE_SIZE = "file_size";
        public static final String TYPE = "type";
        public static final String STATUS = "status";
        public static final String CREATE_TIME = "create_time";
        public static final String LAST_MODIFY_TIME = "last_modify_time";
    }

    public static final String TABLE_NAME = "attachment";

}
