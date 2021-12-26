/*
Navicat MySQL Data Transfer

Source Server         : 123.57.93.65
Source Server Version : 50727
Source Host           : 123.57.93.65:3306
Source Database       : miaosha

Target Server Type    : MYSQL
Target Server Version : 50727
File Encoding         : 65001

Date: 2020-04-09 19:06:39
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `miaosha_goods`
-- ----------------------------
DROP TABLE IF EXISTS `miaosha_goods`;
CREATE TABLE `miaosha_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀的商品表',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '商品Id',
  `miaosha_price` decimal(10,2) DEFAULT '0.00' COMMENT '秒杀价',
  `stock_count` int(11) DEFAULT NULL COMMENT '库存数量',
  `start_date` datetime DEFAULT NULL COMMENT '秒杀开始时间',
  `end_date` datetime DEFAULT NULL COMMENT '秒杀结束时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of miaosha_goods
-- ----------------------------
INSERT INTO `miaosha_goods` VALUES ('1', '1', '0.01', '6', '2017-12-04 21:51:23', '2020-12-01 21:51:27');
INSERT INTO `miaosha_goods` VALUES ('2', '2', '0.01', '0', '2017-12-04 21:40:14', '2020-12-01 21:51:27');
INSERT INTO `miaosha_goods` VALUES ('3', '3', '0.01', '9', '2017-12-04 21:40:14', '2017-12-31 14:00:24');
INSERT INTO `miaosha_goods` VALUES ('4', '4', '0.01', '9', '2017-12-04 21:40:14', '2017-12-31 14:00:24');
