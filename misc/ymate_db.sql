-- ----------------------------
--  Table structure for `ym_attachment`
-- ----------------------------
DROP TABLE IF EXISTS `ym_attachment`;
CREATE TABLE `ym_attachment` (
  `id` varchar(32) NOT NULL,
  `hash` varchar(32) NOT NULL,
  `uid` varchar(32) NOT NULL,
  `static_url` varchar(255) DEFAULT NULL,
  `source_path` varchar(255) NOT NULL,
  `extension` varchar(10) DEFAULT NULL,
  `mime_type` varchar(100) NOT NULL,
  `size` bigint(20) DEFAULT '0',
  `status` smallint(2) unsigned DEFAULT '0',
  `type` smallint(2) unsigned DEFAULT '0',
  `site_id` varchar(32) NOT NULL,
  `owner` varchar(32) DEFAULT NULL,
  `serial_attrs` text,
  `create_time` bigint(13) NOT NULL,
  `last_modify_time` bigint(13) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `ym_attachment_attribute`
-- ----------------------------
DROP TABLE IF EXISTS `ym_attachment_attribute`;
CREATE TABLE `ym_attachment_attribute` (
  `id` varchar(32) NOT NULL,
  `attachment_id` varchar(32) NOT NULL,
  `attr_key` varchar(255) NOT NULL,
  `attr_value` text,
  `type` smallint(2) unsigned DEFAULT '0',
  `owner` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `ym_attachment_meta`
-- ----------------------------
DROP TABLE IF EXISTS `ym_attachment_meta`;
CREATE TABLE `ym_attachment_meta` (
  `id` varchar(32) NOT NULL,
  `usage_size` bigint(20) DEFAULT '0',
  `max_size` bigint(20) DEFAULT '0',
  `site_id` varchar(32) NOT NULL,
  `create_time` bigint(13) NOT NULL,
  `last_modify_time` bigint(13) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;