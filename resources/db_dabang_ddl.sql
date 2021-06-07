-- jb_dabang.전통차 definition

CREATE TABLE `전통차` (
  `tea_id` char(5) NOT NULL,
  `tea_name` varchar(20) NOT NULL COMMENT '차 이름(예, 질경이차)',
  `tea_description` varchar(200) DEFAULT NULL COMMENT '차 특성 등을 말로 설명',
  PRIMARY KEY (`tea_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

GRANT Alter ON jb_dabang.* TO 'myself'@'%';
GRANT Create ON jb_dabang.* TO 'myself'@'%';
GRANT Create view ON jb_dabang.* TO 'myself'@'%';
GRANT Delete ON jb_dabang.* TO 'myself'@'%';
GRANT Delete history ON jb_dabang.* TO 'myself'@'%';
GRANT Drop ON jb_dabang.* TO 'myself'@'%';
GRANT Grant option ON jb_dabang.* TO 'myself'@'%';
GRANT Index ON jb_dabang.* TO 'myself'@'%';
GRANT Insert ON jb_dabang.* TO 'myself'@'%';
GRANT References ON jb_dabang.* TO 'myself'@'%';
GRANT Select ON jb_dabang.* TO 'myself'@'%';
GRANT Show view ON jb_dabang.* TO 'myself'@'%';
GRANT Trigger ON jb_dabang.* TO 'myself'@'%';
GRANT Update ON jb_dabang.* TO 'myself'@'%';

/* 일단 데이터베이스를 root 로써 생성 */
CREATE DATABASE `jb_dabang` /*!40100 DEFAULT CHARACTER SET utf8 */;
