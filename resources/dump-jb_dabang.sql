-- MariaDB dump 10.19  Distrib 10.5.9-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: jb_dabang
-- ------------------------------------------------------
-- Server version	10.5.9-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `고객주소`
--

DROP TABLE IF EXISTS `고객주소`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `고객주소` (
  `주소번호` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `고객SN` int(10) unsigned NOT NULL,
  `단지번호` int(10) unsigned NOT NULL,
  `상세주소` varchar(40) DEFAULT NULL COMMENT '101동 1503호',
  PRIMARY KEY (`고객SN`,`주소번호`),
  KEY `고객주소_단지번호_FK` (`단지번호`),
  KEY `고객주소_주소번호_IDX` (`주소번호`) USING BTREE,
  CONSTRAINT `고객주소_FK` FOREIGN KEY (`고객SN`) REFERENCES `전통고객` (`고객SN`),
  CONSTRAINT `고객주소_단지번호_FK` FOREIGN KEY (`단지번호`) REFERENCES `단지주소` (`단지번호`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `단지주소`
--

DROP TABLE IF EXISTS `단지주소`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `단지주소` (
  `단지번호` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `관리번호` decimal(25,0) NOT NULL,
  `우편번호` mediumint(9) DEFAULT NULL COMMENT '백만저장 용량',
  `도로명주소` varchar(100) DEFAULT NULL COMMENT '경기도 수원시 팔달구 권광로364번길 7-2(우만동,현대아파트)',
  PRIMARY KEY (`단지번호`),
  UNIQUE KEY `고객단지_un` (`관리번호`),
  KEY `고객단지_관리번호_IDX` (`관리번호`) USING BTREE,
  CONSTRAINT `고객단지_FK` FOREIGN KEY (`관리번호`) REFERENCES `도로명주소` (`관리번호`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `도로명주소`
--

DROP TABLE IF EXISTS `도로명주소`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `도로명주소` (
  `관리번호` decimal(25,0) NOT NULL,
  `도로명코드` decimal(12,0) NOT NULL COMMENT '413904397016',
  `읍면동일련번호` tinyint(2) NOT NULL COMMENT '00',
  `지하여부` tinyint(1) DEFAULT NULL COMMENT '0:지상, 1:지하',
  `건물본번` int(5) unsigned DEFAULT NULL,
  `건물부번` int(5) unsigned DEFAULT NULL,
  `기초구역번호` int(5) unsigned DEFAULT NULL COMMENT '우편번호',
  PRIMARY KEY (`관리번호`),
  KEY `도로명주소_FK3` (`도로명코드`,`읍면동일련번호`),
  CONSTRAINT `도로명주소_FK` FOREIGN KEY (`도로명코드`, `읍면동일련번호`) REFERENCES `도로명코드` (`도로명코드`, `읍면동일련번호`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `도로명코드`
--

DROP TABLE IF EXISTS `도로명코드`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `도로명코드` (
  `도로명코드` decimal(12,0) NOT NULL COMMENT '413904397016',
  `읍면동일련번호` tinyint(2) NOT NULL COMMENT '00',
  `시도명` varchar(20) DEFAULT NULL,
  `시군구` varchar(20) DEFAULT NULL,
  `읍면동구분` tinyint(1) DEFAULT NULL COMMENT '0: 읍면, 1:동, 2:미부여',
  `도로명` varchar(80) DEFAULT NULL,
  `읍면동` varchar(20) DEFAULT NULL,
  `읍면동코드` smallint(6) DEFAULT NULL COMMENT '132',
  PRIMARY KEY (`도로명코드`,`읍면동일련번호`),
  KEY `도로명코드_도로명_IDX` (`도로명`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `바구니`
--

DROP TABLE IF EXISTS `바구니`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `바구니` (
  `바구니ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `고객SN` int(10) unsigned NOT NULL,
  `주문일` datetime NOT NULL DEFAULT current_timestamp() COMMENT '시분초까지 기록',
  `단지번호` int(10) unsigned DEFAULT NULL COMMENT '배송지 단지',
  `상세주소` varchar(40) DEFAULT NULL COMMENT '배송지 주소',
  PRIMARY KEY (`바구니ID`),
  KEY `상품주문_단지_FK` (`단지번호`),
  KEY `상품주문_FK` (`고객SN`),
  CONSTRAINT `상품주문_FK` FOREIGN KEY (`고객SN`) REFERENCES `전통고객` (`고객SN`),
  CONSTRAINT `상품주문_단지_FK` FOREIGN KEY (`단지번호`) REFERENCES `단지주소` (`단지번호`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `바구니행`
--

DROP TABLE IF EXISTS `바구니행`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `바구니행` (
  `행ID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '차 구매 바구니 항목 행',
  `바구니ID` int(10) unsigned NOT NULL,
  `상품ID` smallint(5) unsigned NOT NULL,
  `주문수량` smallint(5) unsigned NOT NULL DEFAULT 1 COMMENT '상품 주문 수량(100 티백) 5박스',
  `금액` decimal(7,0) NOT NULL COMMENT '수량으로 단가 계산',
  PRIMARY KEY (`행ID`),
  KEY `바구니행_FK` (`바구니ID`),
  KEY `상품ID_FK` (`상품ID`),
  CONSTRAINT `바구니행_FK` FOREIGN KEY (`바구니ID`) REFERENCES `바구니` (`바구니ID`),
  CONSTRAINT `상품ID_FK` FOREIGN KEY (`상품ID`) REFERENCES `전통차` (`상품ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `부가정보`
--

DROP TABLE IF EXISTS `부가정보`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `부가정보` (
  `관리번호` decimal(25,0) NOT NULL,
  `시군구건물명` varchar(40) DEFAULT NULL,
  `공동주택여부` tinyint(4) DEFAULT NULL COMMENT '0:비공동주택, 1:공동주택',
  PRIMARY KEY (`관리번호`),
  CONSTRAINT `부가정보_FK` FOREIGN KEY (`관리번호`) REFERENCES `도로명주소` (`관리번호`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `전통고객`
--

DROP TABLE IF EXISTS `전통고객`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `전통고객` (
  `고객SN` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '고객 일련번호(Serial Number)',
  `고객ID` varchar(40) NOT NULL,
  `고객이름` varchar(40) NOT NULL,
  `password` binary(16) DEFAULT NULL COMMENT '해슁된 비밀번호 16바이트',
  `salt` binary(16) DEFAULT NULL COMMENT '비밀번호 생성 salt 16바이트',
  PRIMARY KEY (`고객SN`),
  UNIQUE KEY `전통고객_ID_un` (`고객ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `전통차`
--

DROP TABLE IF EXISTS `전통차`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `전통차` (
  `상품ID` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `차이름` varchar(20) NOT NULL COMMENT '차 이름(예, 질경이차)',
  `제고수량` int(10) unsigned DEFAULT NULL COMMENT '2g 티백 100개들이 박스 단위',
  `제조일` date DEFAULT NULL,
  `용량` varchar(20) DEFAULT NULL COMMENT '예, 2g 티백 100개 상자',
  `가격` decimal(7,0) DEFAULT NULL COMMENT '단위: 원',
  `설명` varchar(100) DEFAULT NULL COMMENT '생산자가 제공하는 제품설명',
  `회사SN` int(10) unsigned DEFAULT NULL COMMENT '제조회사 일련번호',
  PRIMARY KEY (`상품ID`),
  KEY `전통차_FK` (`회사SN`),
  FULLTEXT KEY `차설명_Text_IDX` (`설명`),
  CONSTRAINT `전통차_FK` FOREIGN KEY (`회사SN`) REFERENCES `제조회사` (`회사SN`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `제조회사`
--

DROP TABLE IF EXISTS `제조회사`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `제조회사` (
  `회사SN` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `회사명` varchar(40) NOT NULL,
  `단지번호` int(10) unsigned DEFAULT NULL COMMENT '단지주소.단지번호',
  PRIMARY KEY (`회사SN`),
  UNIQUE KEY `제조회사_un` (`회사명`),
  KEY `제조회사_FK` (`단지번호`),
  CONSTRAINT `제조회사_FK` FOREIGN KEY (`단지번호`) REFERENCES `단지주소` (`단지번호`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='농장, 국가 약칭';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'jb_dabang'
--
/*!50003 DROP PROCEDURE IF EXISTS `getAddressInfo` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'IGNORE_SPACE,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`myself`@`localhost` PROCEDURE `getAddressInfo`(p주소번호 integer)
    MODIFIES SQL DATA
select * from 고객주소 where 주소번호 = p주소번호 ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getCustomer성씨` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'IGNORE_SPACE,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`myself`@`localhost` PROCEDURE `getCustomer성씨`(in 성씨 varchar(40),
						out customers int)
begin 
	select count(*) into customers from 전통고객
	where 고객이름 like concat(성씨, '%');
end ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-08-21 12:31:25
