/*
Navicat MySQL Data Transfer

Source Server         : 123.57.93.65
Source Server Version : 50727
Source Host           : 123.57.93.65:3306
Source Database       : miaosha

Target Server Type    : MYSQL
Target Server Version : 50727
File Encoding         : 65001

Date: 2020-04-09 19:06:46
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `miaosha_user`
-- ----------------------------
DROP TABLE IF EXISTS `miaosha_user`;
CREATE TABLE `miaosha_user` (
  `id` bigint NOT NULL COMMENT '用户ID，手机号码',
  `nickname` varchar(255) NOT NULL,
  `password` varchar(32) DEFAULT NULL COMMENT 'MD5(MD5(pass明文+固定salt) + salt)',
  `salt` varchar(10) DEFAULT NULL,
  `head` varchar(128) DEFAULT NULL COMMENT '头像，云存储的ID',
  `register_date` datetime DEFAULT NULL COMMENT '注册时间',
  `last_login_date` datetime DEFAULT NULL COMMENT '上蔟登录时间',
  `login_count` int DEFAULT '0' COMMENT '登录次数',
  PRIMARY KEY (`id`),
  KEY `idx_nickname` (`nickname`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of miaosha_user
-- ----------------------------
DROP TABLE IF EXISTS `miaosha_account`;
CREATE TABLE `miaosha_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `balance_amount` decimal(18,4) DEFAULT NULL COMMENT '账户余额',
  `transfer_amount` decimal(18,4) DEFAULT 0 COMMENT '准备操作金额',
  `user_id` bigint not NULL COMMENT '用户id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `user_id_index` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `miaosha_payment`;
CREATE TABLE `miaosha_payment` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '创建时间',
  `user_id` bigint not NULL COMMENT '用户id',
  `miaosha_order_id` bigint(20) NOT NULL COMMENT '订单ID，只能支付一笔',
  `amount` decimal(18,4) DEFAULT NULL COMMENT '支付金额',
  `status` varchar(45) DEFAULT NULL COMMENT '支付状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `version` int(11) DEFAULT NULL COMMENT '版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_miaosha_order_id` (`miaosha_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
