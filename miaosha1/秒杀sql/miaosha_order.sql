/*
Navicat MySQL Data Transfer

Source Server         : 123.57.93.65
Source Server Version : 50727
Source Host           : 123.57.93.65:3306
Source Database       : miaosha

Target Server Type    : MYSQL
Target Server Version : 50727
File Encoding         : 65001

Date: 2020-04-09 19:06:43
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `miaosha_order`
-- ----------------------------
DROP TABLE IF EXISTS `miaosha_order`;
CREATE TABLE `miaosha_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '商品ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `u_uid_gid` (`user_id`,`goods_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1560 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of miaosha_order
-- ----------------------------
INSERT INTO `miaosha_order` VALUES ('1547', '18912341234', '1561', '1');
INSERT INTO `miaosha_order` VALUES ('1548', '18912341234', '1562', '2');
INSERT INTO `miaosha_order` VALUES ('1549', '18912341234', '1563', '4');
INSERT INTO `miaosha_order` VALUES ('1550', '18912341234', '1564', '3');
