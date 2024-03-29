/*Table structure for table `cart_tbl` */

DROP TABLE IF EXISTS `cart_tbl`;

CREATE TABLE `cart_tbl` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '购物车id',
  `commodity_code` varchar(255) DEFAULT NULL COMMENT '商品编码',
  `price` int DEFAULT '0' COMMENT '商品单价',
  `count` int DEFAULT '0' COMMENT '购买数量',
  `user_id` varchar(255) DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `commodity_code` (`commodity_code`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;

/*Data for the table `cart_tbl` */

insert  into `cart_tbl`(`id`,`commodity_code`,`price`,`count`,`user_id`) values
(1,'PU201',500,10,'UU100');

/*Table structure for table `order_tbl` */

DROP TABLE IF EXISTS `order_tbl`;

CREATE TABLE `order_tbl` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '订单id',
  `user_id` varchar(255) DEFAULT NULL COMMENT '用户id',
  `commodity_code` varchar(255) DEFAULT NULL COMMENT '商品编码,也可以是商品id',
  `count` int DEFAULT '0' COMMENT '购买这个商品的数量',
  `money` int DEFAULT '0' COMMENT '订单金额',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb3;

/*Data for the table `order_tbl` */

insert  into `order_tbl`(`id`,`user_id`,`commodity_code`,`count`,`money`) values
(22,'UU100','PU201',10,200),
(23,'UU100','PU201',10,200),
(24,'UU100','PU201',10,200),
(25,'UU100','PU201',10,200);

/*Table structure for table `stock_tbl` */

DROP TABLE IF EXISTS `stock_tbl`;

CREATE TABLE `stock_tbl` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '商品id',
  `commodity_code` varchar(255) DEFAULT NULL COMMENT '商品编码',
  `count` int DEFAULT '0' COMMENT '商品库存',
  PRIMARY KEY (`id`),
  UNIQUE KEY `commodity_code` (`commodity_code`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;

/*Data for the table `stock_tbl` */

insert  into `stock_tbl`(`id`,`commodity_code`,`count`) values
(1,'PU201',990);

/*Table structure for table `undo_log` */

DROP TABLE IF EXISTS `undo_log`;

CREATE TABLE `undo_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `branch_id` bigint NOT NULL,
  `xid` varchar(100) NOT NULL,
  `context` varchar(128) NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb3;

