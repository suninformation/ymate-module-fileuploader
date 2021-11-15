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

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * 上传文件信息对象
 *
 * @author 刘镇 (suninformation@163.com) on 2016/03/30 01:09
 * @since 1.0
 */
public class UploadFileMeta implements Serializable {

    public static String buildSourcePath(ResourceType resourceType, String hash) {
        return buildSourcePath(resourceType, hash, null);
    }

    public static String buildSourcePath(ResourceType resourceType, String hash, String filename) {
        if (resourceType == null) {
            throw new NullArgumentException("resourceType");
        }
        if (StringUtils.isBlank(hash)) {
            throw new NullArgumentException("hash");
        }
        String sourcePath = String.format("%s/%s/%s", resourceType.name().toLowerCase(), StringUtils.substring(hash, 0, 2), StringUtils.substring(hash, 2, 4));
        if (StringUtils.isBlank(filename)) {
            return sourcePath;
        }
        return String.format("%s/%s", sourcePath, filename);
    }

    /**
     * 文件哈希值
     */
    private String hash;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 扩展名称
     */
    private String extension;

    /**
     * 文件静态引用URL地址
     */
    private String url;

    /**
     * 源文件路径
     */
    private String sourcePath;

    /**
     * 文件大小（字节）
     */
    private Long size;

    /**
     * 文件MIME类型
     */
    private String mimeType;

    /**
     * 文件资源类型
     */
    private ResourceType type;

    /**
     * 文件状态
     */
    private Integer status;

    /**
     * 文件创建时间（毫秒）
     */
    private Long createTime;

    /**
     * 文件最后修改时间（毫秒）
     */
    private Long lastModifyTime;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UploadFileMeta that = (UploadFileMeta) o;
        return new EqualsBuilder()
                .append(hash, that.hash)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(hash)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("hash", hash)
                .append("filename", filename)
                .append("extension", extension)
                .append("url", url)
                .append("sourcePath", sourcePath)
                .append("size", size)
                .append("mimeType", mimeType)
                .append("type", type)
                .append("status", status)
                .append("createTime", createTime)
                .append("lastModifyTime", lastModifyTime)
                .toString();
    }

    public Builder bind() {
        return new Builder(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final UploadFileMeta fileMeta;

        public Builder() {
            fileMeta = new UploadFileMeta();
        }

        public Builder(UploadFileMeta fileMeta) {
            this.fileMeta = fileMeta;
        }

        public UploadFileMeta build() {
            return fileMeta;
        }

        public Builder hash(String hash) {
            fileMeta.setHash(hash);
            return this;
        }

        public Builder filename(String filename) {
            fileMeta.setFilename(filename);
            return this;
        }

        public Builder extension(String extension) {
            fileMeta.setExtension(extension);
            return this;
        }

        public Builder url(String url) {
            fileMeta.setUrl(url);
            return this;
        }

        public Builder sourcePath(String sourcePath) {
            fileMeta.setSourcePath(sourcePath);
            return this;
        }

        public Builder size(Long size) {
            fileMeta.setSize(size);
            return this;
        }

        public Builder mimeType(String mimeType) {
            fileMeta.setMimeType(mimeType);
            return this;
        }

        public Builder type(ResourceType type) {
            fileMeta.setType(type);
            return this;
        }

        public Builder status(Integer status) {
            fileMeta.setStatus(status);
            return this;
        }

        public Builder createTime(Long createTime) {
            fileMeta.setCreateTime(createTime);
            return this;
        }

        public Builder lastModifyTime(Long lastModifyTime) {
            fileMeta.setLastModifyTime(lastModifyTime);
            return this;
        }
    }
}
