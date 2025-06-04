create database if not exists intelliChat;

-- 切换数据库
use intelliChat;

-- ----------------------------
-- remove all foreign key constraints
-- ----------------------------
ALTER TABLE `user` DROP FOREIGN KEY `user_ibfk_1`;
ALTER TABLE `group_msg_content` DROP FOREIGN KEY `group_ibfk_1`;
ALTER TABLE `group_msg_content` DROP FOREIGN KEY `group_ibfk_2`;



-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
                         `id` int(11) NOT NULL AUTO_INCREMENT,
                         `username` varchar(20) NOT NULL COMMENT '登录账号',
                         `nickname` varchar(20) NOT NULL COMMENT '昵称',
                         `password` varchar(255) NOT NULL COMMENT '密码',
                         `user_profile` varchar(255) DEFAULT NULL COMMENT '管理员头像',
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES ('1', 'admin', '系统管理员', 'yanjq2024', 'https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1784117537,3335593911&fm=26&gp=0.jpg');


-- ----------------------------
-- Table structure for feedback
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback` (
                            `id` varchar(64) NOT NULL,
                            `user_id` varchar(64) DEFAULT NULL,
                            `username` varchar(64) DEFAULT NULL,
                            `nickname` varchar(64) DEFAULT NULL,
                            `content` varchar(255) DEFAULT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for user_state
-- ----------------------------
DROP TABLE IF EXISTS `user_state`;
CREATE TABLE `user_state` (
                              `id` int(11) NOT NULL,
                              `name` varchar(20) NOT NULL COMMENT '状态名',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `user_state` VALUES ('1', '在线');
INSERT INTO `user_state` VALUES ('2', '离线');
INSERT INTO `user_state` VALUES ('3', '已注销');


-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `id` int(11) NOT NULL AUTO_INCREMENT,
                        `username` varchar(20) NOT NULL COMMENT '登录账号',
                        `nickname` varchar(20) NOT NULL COMMENT '昵称',
                        `password` varchar(255) NOT NULL COMMENT '密码',
                        `user_profile` varchar(255) DEFAULT NULL COMMENT '用户头像',
                        `user_state_id` int(11) DEFAULT '2' COMMENT '用户状态id',
                        `is_enabled` tinyint(1) DEFAULT '1' COMMENT '是否可用',
                        `is_locked` tinyint(1) DEFAULT '0' COMMENT '是否被锁定',
                        PRIMARY KEY (`id`),
                        KEY `user_ibfk_1` (`user_state_id`),
                        CONSTRAINT `user_ibfk_1` FOREIGN KEY (`user_state_id`) REFERENCES `user_state` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for message_type
-- ----------------------------
DROP TABLE IF EXISTS `message_type`;
CREATE TABLE `message_type` (
                                `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '消息类型编号',
                                `name` varchar(20) DEFAULT NULL COMMENT '消息类型名称',
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of message_type
-- ----------------------------
INSERT INTO `message_type` VALUES ('1', '文本');
INSERT INTO `message_type` VALUES ('2', '图片');
INSERT INTO `message_type` VALUES ('3', '文件');


-- ----------------------------
-- Table structure for group_msg_content
-- ----------------------------
DROP TABLE IF EXISTS `group_msg_content`;
CREATE TABLE `group_msg_content` (
                                     `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '消息内容编号',
                                     `from_id` int(11) DEFAULT NULL COMMENT '发送者的编号',
                                     `from_name` varchar(20) DEFAULT NULL COMMENT '发送者的昵称',
                                     `from_profile` varchar(255) DEFAULT NULL COMMENT '发送者的头像',
                                     `create_time` datetime DEFAULT NULL COMMENT '消息发送时间',
                                     `content` text COMMENT '消息内容',
                                     `message_type_id` int(11) DEFAULT NULL COMMENT '消息类型编号',
                                     PRIMARY KEY (`id`),
                                     KEY `group_ibfk_1` (`from_id`),
                                     KEY `group_ibfk_2` (`message_type_id`),
                                     CONSTRAINT `group_ibfk_1` FOREIGN KEY (`from_id`) REFERENCES `user` (`id`),
                                     CONSTRAINT `group_ibfk_2` FOREIGN KEY (`message_type_id`) REFERENCES `message_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=utf8;



-- ----------------------------
-- Table structure for mail_send_log
-- ----------------------------
DROP TABLE IF EXISTS `mail_send_log`;
CREATE TABLE `mail_send_log` (
                                 `msg_id` varchar(255) NOT NULL,
                                 `content_type` tinyint(2) DEFAULT NULL COMMENT '0:反馈，1:验证码',
                                 `content` varchar(255) DEFAULT NULL,
                                 `mail_address` varchar(64) DEFAULT NULL,
                                 `status` tinyint(2) DEFAULT NULL COMMENT '0-投递中，1-成功，2-失败',
                                 `route_key` varchar(128) DEFAULT NULL,
                                 `exchange` varchar(128) DEFAULT NULL,
                                 `count` tinyint(2) DEFAULT NULL,
                                 `try_time` datetime DEFAULT NULL,
                                 `create_time` datetime DEFAULT NULL,
                                 `update_time` datetime DEFAULT NULL,
                                 PRIMARY KEY (`msg_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;