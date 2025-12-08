-- ----------------------------
-- RBAC Framework SQL Script
-- ----------------------------

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `parent_id` bigint(20) DEFAULT 0 COMMENT 'Parent ID',
  `name` varchar(50) NOT NULL COMMENT 'Permission Name',
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
-- Updates for sys_user
-- ----------------------------
-- Please run these manually if columns do not exist.
-- If you are starting fresh, ensure sys_user has these columns.

ALTER TABLE `sys_user` ADD COLUMN `password` varchar(100) DEFAULT NULL COMMENT 'Password (Encrypted)';
ALTER TABLE `sys_user` ADD COLUMN `salt` varchar(50) DEFAULT NULL COMMENT 'Salt (Optional if using BCrypt)';
ALTER TABLE `sys_user` ADD COLUMN `avatar` varchar(255) DEFAULT NULL COMMENT 'Avatar URL';
ALTER TABLE `sys_user` ADD COLUMN `status` tinyint(1) DEFAULT 1 COMMENT 'Status (1: Enabled, 0: Disabled)';
ALTER TABLE `sys_user` ADD COLUMN `deleted` tinyint(1) DEFAULT 0 COMMENT 'Logical Delete (0: Normal, 1: Deleted)';
ALTER TABLE `sys_user` ADD COLUMN `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time';
ALTER TABLE `sys_user` ADD COLUMN `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time';

-- ----------------------------
-- Initial Data
-- ----------------------------

-- Role
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `description`) VALUES (1, 'Administrator', 'ADMIN', 'System Administrator');
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `description`) VALUES (2, 'User', 'USER', 'Normal User');

-- Permission (Example Menus)
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `path`, `component`, `sort`) VALUES (1, 0, 'System Management', 'sys:manage', 1, '/system', 'Layout', 1);
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `path`, `component`, `sort`) VALUES (2, 1, 'User Management', 'sys:user:list', 1, '/system/user', 'system/user/index', 1);
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `path`, `component`, `sort`) VALUES (3, 1, 'Role Management', 'sys:role:list', 1, '/system/role', 'system/role/index', 2);
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `path`, `component`, `sort`) VALUES (4, 1, 'Permission Management', 'sys:permission:list', 1, '/system/permission', 'system/permission/index', 3);

-- User (Insert Admin User if not exists)
-- Password: password (BCrypt: $2a$10$7JB720yubVSZv5W8vNGkarOu8wOla30tVnh/zAfBeGUp.UafyA.e)
-- Uses INSERT IGNORE to avoid error if ID 1 exists.
INSERT IGNORE INTO `sys_user` (`id`, `username`, `password`, `status`) VALUES (1, 'admin', '$2a$10$7JB720yubVSZv5W8vNGkarOu8wOla30tVnh/zAfBeGUp.UafyA.e', 1);

-- Assign Admin Role to User 1
INSERT IGNORE INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- Assign Permissions to Admin Role
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 1);
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 2);
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 3);
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 4);

SET FOREIGN_KEY_CHECKS = 1;
