-- ----------------------------
--  Table structure for `ym_attachment`
-- ----------------------------
DROP TABLE IF EXISTS `ym_attachment`;
CREATE TABLE `ym_attachment` (
  `id` varchar(32) NOT NULL COMMENT '附件唯一标识',
  `hash` varchar(32) NOT NULL COMMENT '文件哈希签名',
  `static_url` varchar(255) DEFAULT NULL COMMENT '静态资源引用URL地址',
  `source_path` varchar(255) NOT NULL COMMENT '原始存放路径',
  `extension` varchar(10) DEFAULT NULL COMMENT '扩展名',
  `mime_type` varchar(100) NOT NULL COMMENT 'MIME类型',
  `file_size` bigint(20) DEFAULT '0' COMMENT '文件大小',
  `type` smallint(2) unsigned DEFAULT '0' COMMENT '类型',
  `status` smallint(2) unsigned DEFAULT '0' COMMENT '状态',
  `create_time` bigint(13) NOT NULL COMMENT '创建时间',
  `last_modify_time` bigint(13) DEFAULT '0' COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='附件信息';

-- ----------------------------
--  Table structure for `ym_attachment_attribute`
-- ----------------------------
DROP TABLE IF EXISTS `ym_attachment_attribute`;
CREATE TABLE `ym_attachment_attribute` (
  `id` varchar(32) NOT NULL COMMENT '附件属性唯一标识',
  `attachment_id` varchar(32) NOT NULL COMMENT '附件唯一标识',
  `attr_key` varchar(100) NOT NULL COMMENT '属性键名',
  `attr_value` text COMMENT '属性键值',
  `type` smallint(2) unsigned DEFAULT '0' COMMENT '类型',
  `owner` varchar(32) DEFAULT NULL COMMENT '拥有者标识',
  `create_time` bigint(13) NOT NULL COMMENT '创建时间',
  `last_modify_time` bigint(13) DEFAULT '0' COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `attach_attrkey_UNIQUE` (`attachment_id`,`attr_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='附件属性信息';