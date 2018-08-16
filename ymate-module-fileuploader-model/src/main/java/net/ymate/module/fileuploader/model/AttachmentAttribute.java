package net.ymate.module.fileuploader.model;

import net.ymate.platform.core.beans.annotation.PropertyState;
import net.ymate.platform.persistence.annotation.Default;
import net.ymate.platform.persistence.annotation.Entity;
import net.ymate.platform.persistence.annotation.Id;
import net.ymate.platform.persistence.annotation.Property;
import net.ymate.platform.persistence.jdbc.support.BaseEntity;

/**
 * AttachmentAttribute generated By EntityGenerator on 2016/11/03 上午 04:21:08
 *
 * @author YMP
 * @version 1.0
 */
@Entity(AttachmentAttribute.TABLE_NAME)
public class AttachmentAttribute extends BaseEntity<AttachmentAttribute, java.lang.String> {

    private static final long serialVersionUID = 1L;

    @Id
    @Property(name = FIELDS.ID, nullable = false, length = 32)
    @PropertyState(propertyName = FIELDS.ID)
    private java.lang.String id;

    @Property(name = FIELDS.ATTACHMENT_ID, nullable = false, length = 32)
    @PropertyState(propertyName = FIELDS.ATTACHMENT_ID)
    private java.lang.String attachmentId;

    @Property(name = FIELDS.ATTR_KEY, nullable = false, length = 255)
    @PropertyState(propertyName = FIELDS.ATTR_KEY)
    private java.lang.String attrKey;

    @Property(name = FIELDS.ATTR_VALUE, length = 16383)
    @PropertyState(propertyName = FIELDS.ATTR_VALUE)
    private java.lang.String attrValue;

    @Property(name = FIELDS.TYPE, unsigned = true, length = 2)
    @Default("0")
    @PropertyState(propertyName = FIELDS.TYPE)
    private java.lang.Integer type;

    @Property(name = FIELDS.OWNER, length = 32)
    @PropertyState(propertyName = FIELDS.OWNER)
    private java.lang.String owner;

    /**
     * 构造器
     */
    public AttachmentAttribute() {
    }

    /**
     * 构造器
     *
     * @param id
     * @param attachmentId
     * @param attrKey
     */
    public AttachmentAttribute(java.lang.String id, java.lang.String attachmentId, java.lang.String attrKey) {
        this.id = id;
        this.attachmentId = attachmentId;
        this.attrKey = attrKey;
    }

    /**
     * 构造器
     *
     * @param id
     * @param attachmentId
     * @param attrKey
     * @param attrValue
     * @param type
     * @param owner
     */
    public AttachmentAttribute(java.lang.String id, java.lang.String attachmentId, java.lang.String attrKey, java.lang.String attrValue, java.lang.Integer type, java.lang.String owner) {
        this.id = id;
        this.attachmentId = attachmentId;
        this.attrKey = attrKey;
        this.attrValue = attrValue;
        this.type = type;
        this.owner = owner;
    }

    public java.lang.String getId() {
        return id;
    }

    public void setId(java.lang.String id) {
        this.id = id;
    }


    /**
     * @return the attachmentId
     */
    public java.lang.String getAttachmentId() {
        return attachmentId;
    }

    /**
     * @param attachmentId the attachmentId to set
     */
    public void setAttachmentId(java.lang.String attachmentId) {
        this.attachmentId = attachmentId;
    }

    /**
     * @return the attrKey
     */
    public java.lang.String getAttrKey() {
        return attrKey;
    }

    /**
     * @param attrKey the attrKey to set
     */
    public void setAttrKey(java.lang.String attrKey) {
        this.attrKey = attrKey;
    }

    /**
     * @return the attrValue
     */
    public java.lang.String getAttrValue() {
        return attrValue;
    }

    /**
     * @param attrValue the attrValue to set
     */
    public void setAttrValue(java.lang.String attrValue) {
        this.attrValue = attrValue;
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
     * @return the owner
     */
    public java.lang.String getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(java.lang.String owner) {
        this.owner = owner;
    }


    //
    // Chain
    //

    public static AttachmentAttributeBuilder builder() {
        return new AttachmentAttributeBuilder();
    }

    public AttachmentAttributeBuilder bind() {
        return new AttachmentAttributeBuilder(this);
    }

    public static class AttachmentAttributeBuilder {

        private AttachmentAttribute _model;

        public AttachmentAttributeBuilder() {
            _model = new AttachmentAttribute();
        }

        public AttachmentAttributeBuilder(AttachmentAttribute model) {
            _model = model;
        }

        public AttachmentAttribute build() {
            return _model;
        }

        public java.lang.String id() {
            return _model.getId();
        }

        public AttachmentAttributeBuilder id(java.lang.String id) {
            _model.setId(id);
            return this;
        }

        public java.lang.String attachmentId() {
            return _model.getAttachmentId();
        }

        public AttachmentAttributeBuilder attachmentId(java.lang.String attachmentId) {
            _model.setAttachmentId(attachmentId);
            return this;
        }

        public java.lang.String attrKey() {
            return _model.getAttrKey();
        }

        public AttachmentAttributeBuilder attrKey(java.lang.String attrKey) {
            _model.setAttrKey(attrKey);
            return this;
        }

        public java.lang.String attrValue() {
            return _model.getAttrValue();
        }

        public AttachmentAttributeBuilder attrValue(java.lang.String attrValue) {
            _model.setAttrValue(attrValue);
            return this;
        }

        public java.lang.Integer type() {
            return _model.getType();
        }

        public AttachmentAttributeBuilder type(java.lang.Integer type) {
            _model.setType(type);
            return this;
        }

        public java.lang.String owner() {
            return _model.getOwner();
        }

        public AttachmentAttributeBuilder owner(java.lang.String owner) {
            _model.setOwner(owner);
            return this;
        }

    }

    /**
     * AttachmentAttribute 字段常量表
     */
    public class FIELDS {
        public static final String ID = "id";
        public static final String ATTACHMENT_ID = "attachment_id";
        public static final String ATTR_KEY = "attr_key";
        public static final String ATTR_VALUE = "attr_value";
        public static final String TYPE = "type";
        public static final String OWNER = "owner";
    }

    public static final String TABLE_NAME = "attachment_attribute";

}
