/*
SQLyog Ultimate v9.02 
MySQL - 5.6.12 : Database - sh
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`sh` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci */;

USE `sh`;

/*Table structure for table `address` */

DROP TABLE IF EXISTS `address`;

CREATE TABLE `address` (
  `address_id` int(11) NOT NULL,
  `province` text COLLATE utf8_unicode_ci NOT NULL,
  `district` text COLLATE utf8_unicode_ci NOT NULL,
  `road` text COLLATE utf8_unicode_ci,
  `detail` text COLLATE utf8_unicode_ci NOT NULL,
  `latitude` DOUBLE NOT NULL,
  `longitude` DOUBLE NOT NULL,
  PRIMARY KEY (`address_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `address` */

/*Table structure for table `area` */

DROP TABLE IF EXISTS `area`;

CREATE TABLE `area` (
  `area_id` int(11) NOT NULL,
  `area_name` text COLLATE utf8_unicode_ci,
  PRIMARY KEY (`area_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `area` */

/*Table structure for table `asset` */

DROP TABLE IF EXISTS `asset`;

CREATE TABLE `asset` (
  `asset_id` int(11) NOT NULL,
  `asset_name` text COLLATE utf8_unicode_ci,
  `asset_type_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`asset_id`),
  KEY `Refsm_user42` (`user_id`),
  KEY `Refasset_type44` (`asset_type_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `asset` */

/*Table structure for table `asset_type` */

DROP TABLE IF EXISTS `asset_type`;

CREATE TABLE `asset_type` (
  `asset_type_id` int(11) NOT NULL,
  `name` text COLLATE utf8_unicode_ci,
  `unit_price` int(11) DEFAULT NULL,
  PRIMARY KEY (`asset_type_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `asset_type` */

/*Table structure for table `asset_warehouse` */

DROP TABLE IF EXISTS `asset_warehouse`;

CREATE TABLE `asset_warehouse` (
  `id` int(11) NOT NULL,
  `warehouse_id` int(11) NOT NULL,
  `asset_type_id` int(11) NOT NULL,
  `count` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `Refasset_type45` (`asset_type_id`),
  KEY `Refwarehouse46` (`warehouse_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `asset_warehouse` */

/*Table structure for table `fund` */

DROP TABLE IF EXISTS `fund`;

CREATE TABLE `fund` (
  `fund_id` int(11) NOT NULL,
  `fund_name` text COLLATE utf8_unicode_ci,
  `amount` int(11) DEFAULT NULL,
  PRIMARY KEY (`fund_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `fund` */

/*Table structure for table `incident` */

DROP TABLE IF EXISTS `incident`;

CREATE TABLE `incident` (
  `incident_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `content` text COLLATE utf8_unicode_ci,
  `create_date` date DEFAULT NULL,
  `type_id` char(10) COLLATE utf8_unicode_ci NOT NULL,
  `manager_id` int(11) NOT NULL,
  `resolve_content` text COLLATE utf8_unicode_ci,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`incident_id`),
  KEY `Refsm_user22` (`user_id`),
  KEY `Refsm_user23` (`manager_id`),
  KEY `Refincident_type24` (`type_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `incident` */

/*Table structure for table `incident_type` */

DROP TABLE IF EXISTS `incident_type`;

CREATE TABLE `incident_type` (
  `type_id` char(10) COLLATE utf8_unicode_ci NOT NULL,
  `type_name` char(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`type_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `incident_type` */

/*Table structure for table `message` */

DROP TABLE IF EXISTS `message`;

CREATE TABLE `message` (
  `message_id` int(11) NOT NULL,
  `content` text COLLATE utf8_unicode_ci,
  `create_user` int(11) NOT NULL,
  `send_date` date DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`message_id`),
  KEY `Refsm_user25` (`create_user`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `message` */

/*Table structure for table `message_receiver` */

DROP TABLE IF EXISTS `message_receiver`;

CREATE TABLE `message_receiver` (
  `id` int(11) NOT NULL,
  `message_id` int(11) DEFAULT NULL,
  `receiver_id` int(11) NOT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `Refsm_user26` (`receiver_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `message_receiver` */

/*Table structure for table `order_service` */

DROP TABLE IF EXISTS `order_service`;

CREATE TABLE `order_service` (
  `order_id` int(11) NOT NULL,
  `customer_id` int(11) NOT NULL,
  `price` int(11) DEFAULT NULL,
  `address_id` int(11) NOT NULL,
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  `mon_start` int(11) DEFAULT NULL,
  `mon_end` int(11) DEFAULT NULL,
  `tue_start` int(11) DEFAULT NULL,
  `tue_end` int(11) DEFAULT NULL,
  `wed_start` int(11) DEFAULT NULL,
  `wed_end` int(11) DEFAULT NULL,
  `thu_start` int(11) DEFAULT NULL,
  `thu_end` int(11) DEFAULT NULL,
  `fri_start` int(11) DEFAULT NULL,
  `fri_end` int(11) DEFAULT NULL,
  `sat_start` int(11) DEFAULT NULL,
  `sat_end` int(11) DEFAULT NULL,
  `sun_start` int(11) DEFAULT NULL,
  `sun_end` int(11) DEFAULT NULL,
  `cooking` int(11) DEFAULT NULL,
  `baby_care` int(11) DEFAULT NULL,
  `old_care` int(11) DEFAULT NULL,
  `cleaning` int(11) DEFAULT NULL,
  `is_periodical` int(11) DEFAULT NULL,
  `order_code` text COLLATE utf8_unicode_ci,
  `status` text COLLATE utf8_unicode_ci,
  `content` text COLLATE utf8_unicode_ci,
  `create_date` datetime DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  KEY `Refsm_user13` (`customer_id`),
  KEY `Refaddress18` (`address_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `order_service` */

insert  into `order_service`(`order_id`,`customer_id`,`price`,`address_id`,`start_date`,`end_date`,`mon_start`,`mon_end`,`tue_start`,`tue_end`,`wed_start`,`wed_end`,`thu_start`,`thu_end`,`fri_start`,`fri_end`,`sat_start`,`sat_end`,`sun_start`,`sun_end`,`cooking`,`baby_care`,`old_care`,`cleaning`,`is_periodical`,`order_code`,`status`,`content`,`create_date`) values (1,6,500000,1,'2017-06-03 00:00:00','2017-06-04 00:00:00',1315,1715,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,1,0,'DH17060300001','1','Dọn dẹp buổi chiều','2017-06-02 14:48:34'),(2,9,500000,1,'2017-06-03 00:00:00','2017-07-03 00:00:00',1315,1715,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,1,1,'DH17060300002','1','Dọn dẹp buổi chiều','2017-06-02 14:48:34'),(3,9,500000,2,'2017-06-03 00:00:00','2017-07-03 00:00:00',1315,1715,1400,1730,0,0,0,0,0,0,0,0,0,0,1,1,0,1,1,'DH17060300003','1','Dọn dẹp buổi chiều','2017-06-02 14:48:34'),(4,6,500000,3,'2017-06-10 14:00:00','2017-06-10 17:00:00',0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,1,0,'DH17061000004','1','Dọn dẹp buổi chiều','2017-06-08 14:48:34'),(5,3,500000,4,'2017-06-11 15:00:00','2017-06-11 18:00:00',0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,1,0,'DH17061100005','1',NULL,'2017-06-09 14:48:34'),(6,4,500000,5,'2017-06-12 08:00:00','2017-06-12 12:00:00',0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,1,0,'DH17061200006','1',NULL,'2017-06-10 14:48:34'),(7,5,1000000,6,'2017-06-13 09:00:00','2017-06-13 13:00:00',0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,1,0,'DH17061300007','1',NULL,'2017-06-11 14:48:34');

/*Table structure for table `plan` */

DROP TABLE IF EXISTS `plan`;

CREATE TABLE `plan` (
  `plan_id` int(11) NOT NULL,
  `customer_id` int(11) NOT NULL,
  `maid_id` int(11) NOT NULL,
  `start_date` DATETIME NOT NULL,
  `end_date` DATETIME NOT NULL,
  `order_id` int(11) NOT NULL,
  `status` int(11) NOT NULL,
  `real_start` DATETIME DEFAULT NULL,
  `real_end` DATETIME DEFAULT NULL,
  `time_comment` int(11) DEFAULT NULL,
  `customer_comment` text COLLATE utf8_unicode_ci,
  `customer_rating` int(11) DEFAULT NULL,
  `salary` int(11) DEFAULT NULL,
  PRIMARY KEY (`plan_id`),
  KEY `Refsm_user15` (`customer_id`),
  KEY `Refsm_user16` (`maid_id`),
  KEY `Reforder_service17` (`order_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `plan` */

/*Table structure for table `post` */

DROP TABLE IF EXISTS `post`;

CREATE TABLE `post` (
  `post_id` int(11) NOT NULL,
  `subject` text COLLATE utf8_unicode_ci,
  `content` text COLLATE utf8_unicode_ci,
  `create_user` int(11) NOT NULL,
  `publish_date` date DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`post_id`),
  KEY `Refsm_user27` (`create_user`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `post` */

/*Table structure for table `post_tag` */

DROP TABLE IF EXISTS `post_tag`;

CREATE TABLE `post_tag` (
  `id` int(11) NOT NULL,
  `tag_id` int(11) NOT NULL,
  `post_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `Reftag28` (`tag_id`),
  KEY `Refpost29` (`post_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `post_tag` */

/*Table structure for table `price_policy` */

DROP TABLE IF EXISTS `price_policy`;

CREATE TABLE `price_policy` (
  `policy_id` int(11) NOT NULL,
  `policy_code` text COLLATE utf8_unicode_ci,
  `content` text COLLATE utf8_unicode_ci,
  `group_id` int(11) NOT NULL,
  `service_id` int(11) NOT NULL,
  `area_id` int(11) NOT NULL,
  `add_rate` int(11) DEFAULT NULL,
  `add_vnd` int(11) DEFAULT NULL,
  `from_date` date DEFAULT NULL,
  `to_date` date DEFAULT NULL,
  `create_user` int(11) NOT NULL,
  `create_date` date DEFAULT NULL,
  PRIMARY KEY (`policy_id`),
  KEY `Refsm_user30` (`create_user`),
  KEY `Refsm_group31` (`group_id`),
  KEY `Refservice35` (`service_id`),
  KEY `Refarea37` (`area_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `price_policy` */

/*Table structure for table `promotion` */

DROP TABLE IF EXISTS `promotion`;

CREATE TABLE `promotion` (
  `poromotion_id` int(11) NOT NULL,
  `promotion_code` text COLLATE utf8_unicode_ci,
  `content` text COLLATE utf8_unicode_ci,
  `group_id` int(11) NOT NULL,
  `service_id` int(11) NOT NULL,
  `area_id` int(11) NOT NULL,
  `from_date` date DEFAULT NULL,
  `to_date` date DEFAULT NULL,
  `create_user` int(11) NOT NULL,
  `create_date` date DEFAULT NULL,
  PRIMARY KEY (`poromotion_id`),
  KEY `Refsm_user32` (`create_user`),
  KEY `Refsm_group33` (`group_id`),
  KEY `Refservice34` (`service_id`),
  KEY `Refarea36` (`area_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `promotion` */

/*Table structure for table `receipt` */

DROP TABLE IF EXISTS `receipt`;

CREATE TABLE `receipt` (
  `receipt_id` int(11) NOT NULL,
  `receiver_id` int(11) DEFAULT NULL,
  `payer_id` int(11) DEFAULT NULL,
  `receiver_name` text COLLATE utf8_unicode_ci,
  `receiver_mobile` text COLLATE utf8_unicode_ci,
  `payer_name` text COLLATE utf8_unicode_ci,
  `payer_mobile` text COLLATE utf8_unicode_ci,
  `receipt_no` text COLLATE utf8_unicode_ci,
  `pay_date` date DEFAULT NULL,
  `amount` int(11) DEFAULT NULL,
  `receive_reason` text COLLATE utf8_unicode_ci,
  `reason_id` int(11) NOT NULL,
  `fund_id` int(11) NOT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`receipt_id`),
  KEY `Refreceipt_reason38` (`reason_id`),
  KEY `Reffund40` (`fund_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `receipt` */

/*Table structure for table `receipt_reason` */

DROP TABLE IF EXISTS `receipt_reason`;

CREATE TABLE `receipt_reason` (
  `reason_id` int(11) NOT NULL,
  `reason_name` text COLLATE utf8_unicode_ci,
  PRIMARY KEY (`reason_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `receipt_reason` */

/*Table structure for table `salary_month` */

DROP TABLE IF EXISTS `salary_month`;

CREATE TABLE `salary_month` (
  `salary_id` int(11) NOT NULL,
  `maid_id` int(11) NOT NULL,
  `work_hour` int(11) DEFAULT NULL,
  `unit_price` int(11) DEFAULT NULL,
  `reward` int(11) DEFAULT NULL,
  `penalty` int(11) DEFAULT NULL,
  `total` int(11) DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `month` int(11) DEFAULT NULL,
  `year` int(11) DEFAULT NULL,
  PRIMARY KEY (`salary_id`),
  KEY `Refsm_user19` (`maid_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `salary_month` */

/*Table structure for table `service` */

DROP TABLE IF EXISTS `service`;

CREATE TABLE `service` (
  `service_id` int(11) NOT NULL,
  `service_name` text COLLATE utf8_unicode_ci,
  PRIMARY KEY (`service_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `service` */

/*Table structure for table `sm_group` */

DROP TABLE IF EXISTS `sm_group`;

CREATE TABLE `sm_group` (
  `group_id` int(11) NOT NULL,
  `group_name` text COLLATE utf8_unicode_ci,
  `parent_id` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  PRIMARY KEY (`group_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `sm_group` */

/*Table structure for table `sm_user` */

DROP TABLE IF EXISTS `sm_user`;

CREATE TABLE `sm_user` (
  `user_id` int(11) DEFAULT NULL,
  `address_id` int(11) DEFAULT NULL,
  `name` text COLLATE utf8_unicode_ci,
  `mobile` text COLLATE utf8_unicode_ci,
  `birthday` date DEFAULT NULL,
  `user_type` int(11) DEFAULT NULL,
  `is_enable` int(11) DEFAULT NULL,
  `password` text COLLATE utf8_unicode_ci,
  `id_number` text COLLATE utf8_unicode_ci,
  `family_mobile` text COLLATE utf8_unicode_ci,
  `home_province` text COLLATE utf8_unicode_ci,
  `amount` int(11) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `picture` text COLLATE utf8_unicode_ci
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `sm_user` */

insert  into `sm_user`(`user_id`,`address_id`,`name`,`mobile`,`birthday`,`user_type`,`is_enable`,`password`,`id_number`,`family_mobile`,`home_province`,`amount`,`create_date`,`picture`) values (2,2,'Hiền','0986488885','1986-05-08',2,1,'b2d3936c6e83e6b3d0576d6de7f4805624ff30b12605957f7ecfc85aa9504922','112030988','0986488886','Hà Nội',1000000,'2017-06-19 11:32:14',NULL),(1,1,'Admin','0986488886','1986-05-08',1,1,'b2d3936c6e83e6b3d0576d6de7f4805624ff30b12605957f7ecfc85aa9504922','112030988','0986488885','Hà Nội',1000000,'2017-06-19 11:32:23',NULL),(3,NULL,'Đỗ Minh Hiền','0986488882','1986-01-06',4,1,'b2d3936c6e83e6b3d0576d6de7f4805624ff30b12605957f7ecfc85aa9504922','112030988','0987658772','Hà Nội',NULL,'2017-06-21 13:38:06','1498554937182_41a2df73-b5fa-46a7-b554-8cecb524b30d.jpg'),(4,NULL,'Phạm Thanh Tú','098769876','1986-01-21',4,1,'b2d3936c6e83e6b3d0576d6de7f4805624ff30b12605957f7ecfc85aa9504922','112030988','098986987','Hà nội',NULL,'2017-06-21 13:42:37','1498554955922_17980407-0bac-44a9-a522-460b0e35d199.jpg'),(5,NULL,'Nguyễn Mạnh Thắng','0987986898','1985-01-20',4,1,'b2d3936c6e83e6b3d0576d6de7f4805624ff30b12605957f7ecfc85aa9504922','113098076','0989876587','Hà Nội',NULL,'2017-06-21 13:44:24','1498554882643_f7a5c3b7-ec42-4efc-970f-92016f82eb85.jpg'),(6,NULL,'Đoàn Phương','098787676','1987-01-13',4,1,'b2d3936c6e83e6b3d0576d6de7f4805624ff30b12605957f7ecfc85aa9504922','115484746648','0987678654','Hà Nội',NULL,'2017-06-21 14:05:35','1498554979281_9754b059-0a3e-4fa7-b917-19aecc7094d8.jpg'),(9,NULL,'Đào Đức Cường','0986488876','1986-01-14',4,1,'b2d3936c6e83e6b3d0576d6de7f4805624ff30b12605957f7ecfc85aa9504922','123456113','0987678653','Hải Phòng',NULL,'2017-06-21 15:45:26','1498555002747_fb9cddbe-3730-4736-82fa-9a8c78e157e0.jpg'),(10,NULL,'Nguyễn A','0986488801','2017-06-15',3,1,'b2d3936c6e83e6b3d0576d6de7f4805624ff30b12605957f7ecfc85aa9504922','112030901','0986488801','Hà Nội',NULL,'2017-06-27 09:36:57',NULL),(11,NULL,'Nguyễn B','098648802','2017-01-11',3,1,'b2d3936c6e83e6b3d0576d6de7f4805624ff30b12605957f7ecfc85aa9504922','112030902','098648802','Hà Nội',NULL,'2017-06-27 09:37:34','1498554384144_c2215b3b-1353-4858-b85c-7dbceaf4f441.jpg'),(12,NULL,'Nguyễn C','098648803','2017-01-08',3,1,'b2d3936c6e83e6b3d0576d6de7f4805624ff30b12605957f7ecfc85aa9504922','112030903','098648803','Hà Nội',NULL,'2017-06-27 09:38:15','1498554548504_8c86271f-3f75-4940-8e2c-8d0f1290b88b.jpg'),(13,NULL,'Nguyễn D','098648804','2017-01-01',3,1,'b2d3936c6e83e6b3d0576d6de7f4805624ff30b12605957f7ecfc85aa9504922','112030904','098648804','Hà Nội',NULL,'2017-06-27 09:39:28','1498554650735_c46667db-0144-4222-95a2-e9517623164d.jpg'),(14,NULL,'Nguyễn E','098648805','2017-01-02',3,1,'b2d3936c6e83e6b3d0576d6de7f4805624ff30b12605957f7ecfc85aa9504922','112030905','098648805','Hà Nội',NULL,'2017-06-27 09:39:55','1498554827158_fde5292c-3c74-464d-8183-55ebf144ae34.jpg');

/*Table structure for table `spend` */

DROP TABLE IF EXISTS `spend`;

CREATE TABLE `spend` (
  `spend_id` int(11) NOT NULL,
  `receiver_id` int(11) DEFAULT NULL,
  `payer_id` int(11) DEFAULT NULL,
  `receiver_name` text COLLATE utf8_unicode_ci,
  `receiver_mobile` text COLLATE utf8_unicode_ci,
  `payer_name` text COLLATE utf8_unicode_ci,
  `payer_mobile` text COLLATE utf8_unicode_ci,
  `spend_no` text COLLATE utf8_unicode_ci,
  `pay_date` date DEFAULT NULL,
  `amount` int(11) DEFAULT NULL,
  `spend_reason` text COLLATE utf8_unicode_ci,
  `reason_id` int(11) NOT NULL,
  `fund_id` int(11) NOT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`spend_id`),
  KEY `Reffund41` (`fund_id`),
  KEY `Refspend_reason39` (`reason_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `spend` */

/*Table structure for table `spend_reason` */

DROP TABLE IF EXISTS `spend_reason`;

CREATE TABLE `spend_reason` (
  `reason_id` int(11) NOT NULL,
  `reason_name` text COLLATE utf8_unicode_ci,
  PRIMARY KEY (`reason_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `spend_reason` */

/*Table structure for table `tag` */

DROP TABLE IF EXISTS `tag`;

CREATE TABLE `tag` (
  `tag_id` int(11) NOT NULL,
  `tag_name` text COLLATE utf8_unicode_ci,
  PRIMARY KEY (`tag_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `tag` */

/*Table structure for table `user_group` */

DROP TABLE IF EXISTS `user_group`;

CREATE TABLE `user_group` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `Refsm_group20` (`group_id`),
  KEY `Refsm_user21` (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `user_group` */

/*Table structure for table `warehouse` */

DROP TABLE IF EXISTS `warehouse`;

CREATE TABLE `warehouse` (
  `warehouse_id` int(11) NOT NULL,
  `name` text COLLATE utf8_unicode_ci,
  PRIMARY KEY (`warehouse_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `warehouse` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
