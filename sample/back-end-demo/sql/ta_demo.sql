CREATE TABLE `tbl_claim` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ontid` varchar(255) DEFAULT NULL COMMENT 'ontid',
  `claim_id` varchar(255) DEFAULT NULL COMMENT 'calim id',
  `claim` longtext COMMENT '用户calim',
  `tx_hash` varchar(255) DEFAULT NULL COMMENT 'calim交易hash',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_ontid` (`ontid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tbl_invoke` (
  `id` varchar(255) NOT NULL,
  `params` text,
  `ontid_index` int(11) DEFAULT NULL,
  `tx_hash` varchar(255) DEFAULT NULL,
  `state` int(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tbl_login` (
  `id` varchar(255) NOT NULL COMMENT '主键',
  `ontid` varchar(255) DEFAULT NULL COMMENT 'ontid',
  `user_name` varchar(255) DEFAULT NULL COMMENT '用户名',
  `state` int(2) DEFAULT NULL COMMENT '是否认证成功;0-失败，1-成功，2-未注册',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_ontid` (`ontid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tbl_register` (
  `id` varchar(255) NOT NULL COMMENT '主键',
  `ontid` varchar(255) DEFAULT NULL COMMENT 'ontid',
  `user_name` varchar(255) DEFAULT NULL COMMENT '用户名',
  `state` int(2) DEFAULT NULL COMMENT '是否认证成功;0-失败，1-成功，2-已存在',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_ontid` (`ontid`),
  KEY `idx_user_name` (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;