/* 일단 데이터베이스를 root 로써 생성 */
CREATE DATABASE `jb_dabang` /*!40100 DEFAULT CHARACTER SET utf8 */;

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

-- jb_dabang.전통차 definition

CREATE TABLE `전통차` (
  `상품ID` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `차이름` varchar(20) NOT NULL COMMENT '차 이름(예, 질경이차)',
  `제고수량` int(10) unsigned DEFAULT NULL COMMENT '2g 티백 100개들이 박스 단위',
  `제조일` date DEFAULT NULL,
  `용량` varchar(20) DEFAULT NULL COMMENT '예, 2g 티백 100개 상자',
  `가격` decimal(7,0) DEFAULT NULL COMMENT '단위: 원',
  `설명` varchar(100) DEFAULT NULL COMMENT '생산자가 제공하는 제품설명',
  PRIMARY KEY (`상품ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

-- jb_dabang.전통고객 definition

CREATE TABLE `전통고객` (
  `고객ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `고객이름` varchar(40) NOT NULL,
  PRIMARY KEY (`고객ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- jb_dabang.상품주문 definition

CREATE TABLE `상품주문` (
  `주문ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `상품ID` smallint(5) unsigned DEFAULT NULL,
  `고객ID` int(10) unsigned DEFAULT NULL,
  `주문수량` smallint(5) unsigned DEFAULT 1 COMMENT '상품 주문 수량(100 티백) 5박스',
  `주문일` datetime DEFAULT current_timestamp() COMMENT '시분초까지 기록',
  PRIMARY KEY (`주문ID`),
  KEY `주문_상품ID_FK` (`상품ID`),
  KEY `주문_고객ID_FK` (`고객ID`),
  CONSTRAINT `주문_고객ID_FK` FOREIGN KEY (`고객ID`) REFERENCES `전통고객` (`고객ID`),
  CONSTRAINT `주문_상품ID_FK` FOREIGN KEY (`상품ID`) REFERENCES `전통차` (`상품ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- jb_dabang.고객단지 definition

CREATE TABLE `고객단지` (
  `단지번호` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `관리번호` decimal(25,0) NOT NULL,
  `도로명주소` varchar(100) DEFAULT NULL COMMENT '경기도 수원시 팔달구 권광로364번길 7-2(우만동,현대아파트)',
  PRIMARY KEY (`단지번호`),
  UNIQUE KEY `고객단지_un` (`관리번호`),
  KEY `고객단지_관리번호_IDX` (`관리번호`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- jb_dabang.고객주소 definition

CREATE TABLE `고객주소` (
  `주소번호` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `고객ID` int(10) unsigned NOT NULL,
  `단지번호` int(10) unsigned NOT NULL,
  `상세주소` varchar(40) DEFAULT NULL COMMENT '101동 1503호',
  PRIMARY KEY (`고객ID`,`주소번호`),
  KEY `고객주소_단지번호_FK` (`단지번호`),
  KEY `고객주소_주소번호_IDX` (`주소번호`) USING BTREE,
  CONSTRAINT `고객주소_고객ID_FK` FOREIGN KEY (`고객ID`) REFERENCES `전통고객` (`고객ID`),
  CONSTRAINT `고객주소_단지번호_FK` FOREIGN KEY (`단지번호`) REFERENCES `고객단지` (`단지번호`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;