-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `parent_id` bigint(20) DEFAULT 0 COMMENT 'Parent ID',
  `simplified_name` varchar(255) NOT NULL COMMENT '简体中文名称',
  `english_name` varchar(255) DEFAULT NULL COMMENT '英文名称',
  `traditional_name` varchar(255) DEFAULT NULL COMMENT '繁体中文名称',
  `code` varchar(50) DEFAULT NULL COMMENT 'Permission Code (e.g., user:add)',
  `type` tinyint(1) NOT NULL COMMENT 'Type (1: Menu, 2: Button)',
  `path` varchar(255) DEFAULT NULL COMMENT 'Frontend Route Path',
  `component` varchar(255) DEFAULT NULL COMMENT 'Frontend Component Path',
  `icon` varchar(50) DEFAULT NULL COMMENT 'Icon',
  `sort` int(11) DEFAULT 0 COMMENT 'Sort Order',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  `deleted` tinyint(1) DEFAULT 0 COMMENT 'Logical Delete (0: Normal, 1: Deleted)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Permission/Menu Table';

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `role_name` varchar(50) NOT NULL COMMENT 'Role Name',
  `role_code` varchar(50) NOT NULL COMMENT 'Role Code (e.g., ADMIN)',
  `description` varchar(255) DEFAULT NULL COMMENT 'Description',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  `deleted` tinyint(1) DEFAULT 0 COMMENT 'Logical Delete (0: Normal, 1: Deleted)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Role Table';

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `role_id` bigint(20) NOT NULL COMMENT 'Role ID',
  `permission_id` bigint(20) NOT NULL COMMENT 'Permission ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Role-Permission Relation Table';

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID（主键）',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '昵称',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `salt` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Salt (Optional if using BCrypt)',
  `avatar` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'Avatar (Base64)',
  `status` tinyint(1) DEFAULT 1 COMMENT 'Status (1: Enabled, 0: Disabled)',
  `deleted` tinyint(1) DEFAULT 0 COMMENT 'Logical Delete (0: Normal, 1: Deleted)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统用户表';

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `user_id` bigint(20) NOT NULL COMMENT 'User ID',
  `role_id` bigint(20) NOT NULL COMMENT 'Role ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User-Role Relation Table';
