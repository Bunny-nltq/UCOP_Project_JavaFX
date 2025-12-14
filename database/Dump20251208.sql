-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: ucop_project_javafx
-- ------------------------------------------------------
-- Server version	9.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `appointments`
--

DROP TABLE IF EXISTS `appointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `appointment_number` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `scheduled_time` datetime NOT NULL,
  `actual_time` datetime DEFAULT NULL,
  `location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('SCHEDULED','CONFIRMED','IN_PROGRESS','COMPLETED','CANCELED','NO_SHOW','RESCHEDULED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `service_provider` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `notes` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `appointment_number` (`appointment_number`),
  KEY `idx_appointment_order` (`order_id`),
  KEY `idx_appointment_status` (`status`),
  KEY `idx_appointment_scheduled` (`scheduled_time`),
  CONSTRAINT `appointments_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointments`
--

LOCK TABLES `appointments` WRITE;
/*!40000 ALTER TABLE `appointments` DISABLE KEYS */;
/*!40000 ALTER TABLE `appointments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cart_id` bigint NOT NULL,
  `item_id` bigint NOT NULL,
  `quantity` bigint NOT NULL,
  `unit_price` decimal(19,4) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_cart_items` (`cart_id`),
  CONSTRAINT `cart_items_ibfk_1` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_items`
--

LOCK TABLES `cart_items` WRITE;
/*!40000 ALTER TABLE `cart_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `carts`
--

DROP TABLE IF EXISTS `carts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_id` bigint NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_cart_account` (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carts`
--

LOCK TABLES `carts` WRITE;
/*!40000 ALTER TABLE `carts` DISABLE KEYS */;
INSERT INTO `carts` VALUES (1,1,'2025-12-07 11:23:18','2025-12-07 13:14:43');
/*!40000 ALTER TABLE `carts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_id` bigint NOT NULL,
  `full_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `city` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `postal_code` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `account_id` (`account_id`),
  KEY `idx_customer_account` (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (1,1,'Nguyễn Văn A','nguyenvana@email.com','0901234567','123 Đường ABC','Hà Nội',NULL,'2025-12-07 18:02:12','2025-12-07 18:02:12'),(2,2,'Trần Thị B','tranthib@email.com','0912345678','456 Đường XYZ','TP.HCM',NULL,'2025-12-07 18:02:12','2025-12-07 18:02:12'),(3,3,'Lê Văn C','levanc@email.com','0923456789','789 Đường MNO','Đà Nẵng',NULL,'2025-12-07 18:02:12','2025-12-07 18:02:12'),(4,4,'Phạm Thị D','phamthid@email.com','0934567890','321 Đường PQR','Hải Phòng',NULL,'2025-12-07 18:02:12','2025-12-07 18:02:12'),(5,5,'Hoàng Văn E','hoangvane@email.com','0945678901','654 Đường STU','Huế',NULL,'2025-12-07 18:02:12','2025-12-07 18:02:12');
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `item_id` bigint NOT NULL,
  `quantity` bigint NOT NULL,
  `unit_price` decimal(19,4) NOT NULL,
  `item_discount` decimal(19,4) DEFAULT '0.0000',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_items` (`order_id`),
  CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,1,1,2,200000.0000,0.0000,'2025-12-03 23:55:54','2025-12-03 23:55:54'),(2,1,2,1,100000.0000,0.0000,'2025-12-03 23:55:54','2025-12-03 23:55:54'),(3,2,3,4,150000.0000,0.0000,'2025-12-03 23:55:54','2025-12-03 23:55:54'),(4,2,1,1,200000.0000,0.0000,'2025-12-03 23:55:54','2025-12-03 23:55:54'),(5,3,4,3,300000.0000,0.0000,'2025-12-03 23:55:54','2025-12-03 23:55:54'),(6,3,2,3,100000.0000,0.0000,'2025-12-03 23:55:54','2025-12-03 23:55:54'),(7,4,1,1,200000.0000,0.0000,'2025-12-03 23:55:54','2025-12-03 23:55:54'),(8,4,3,3,150000.0000,0.0000,'2025-12-03 23:55:54','2025-12-03 23:55:54'),(9,5,2,2,100000.0000,0.0000,'2025-12-03 23:55:54','2025-12-03 23:55:54'),(10,5,5,1,150000.0000,0.0000,'2025-12-03 23:55:54','2025-12-03 23:55:54'),(11,6,1,1,200000.0000,0.0000,'2025-12-03 23:55:54','2025-12-03 23:55:54'),(12,6,4,1,250000.0000,0.0000,'2025-12-03 23:55:54','2025-12-03 23:55:54'),(13,7,3,5,150000.0000,0.0000,'2025-12-03 23:55:54','2025-12-03 23:55:54');
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_id` bigint NOT NULL,
  `status` enum('CART','PLACED','PENDING_PAYMENT','PAID','PACKED','SHIPPED','DELIVERED','CLOSED','CANCELED','RMA_REQUESTED','REFUNDED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `order_number` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `shipping_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `shipping_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `shipping_address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `shipping_city` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `shipping_postal_code` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `subtotal` decimal(19,4) NOT NULL DEFAULT '0.0000',
  `item_discount` decimal(19,4) NOT NULL DEFAULT '0.0000',
  `cart_discount` decimal(19,4) NOT NULL DEFAULT '0.0000',
  `tax_amount` decimal(19,4) NOT NULL DEFAULT '0.0000',
  `shipping_fee` decimal(19,4) NOT NULL DEFAULT '0.0000',
  `cod_fee` decimal(19,4) NOT NULL DEFAULT '0.0000',
  `gateway_fee` decimal(19,4) NOT NULL DEFAULT '0.0000',
  `grand_total` decimal(19,4) NOT NULL DEFAULT '0.0000',
  `amount_due` decimal(19,4) NOT NULL DEFAULT '0.0000',
  `promotion_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `notes` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `placed_at` datetime DEFAULT NULL,
  `paid_at` datetime DEFAULT NULL,
  `shipped_at` datetime DEFAULT NULL,
  `delivered_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_number` (`order_number`),
  KEY `idx_order_account` (`account_id`),
  KEY `idx_order_status` (`status`),
  KEY `idx_order_number` (`order_number`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,1,'CART','ORD001','Nguyễn Văn A','0901234567','123 Đường ABC, Hà Nội',NULL,NULL,500000.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,530000.0000,0.0000,NULL,NULL,'2025-12-03 23:55:54','2025-12-03 23:55:54',NULL,NULL,'2025-12-02 23:55:54','2025-12-02 23:55:54',NULL,'2025-12-02 23:55:54'),(2,2,'PLACED','ORD002','Trần Thị B','0912345678','456 Đường XYZ, TP.HCM',NULL,NULL,800000.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,850000.0000,0.0000,NULL,NULL,'2025-12-03 23:55:54','2025-12-03 23:55:54',NULL,NULL,'2025-12-01 23:55:54','2025-12-01 23:55:54',NULL,'2025-12-01 23:55:54'),(3,1,'PACKED','ORD003','Nguyễn Văn A','0901234567','123 Đường ABC, Hà Nội',NULL,NULL,1200000.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,1250000.0000,0.0000,NULL,NULL,'2025-12-03 23:55:54','2025-12-03 23:55:54',NULL,NULL,'2025-11-30 23:55:54','2025-11-30 23:55:54',NULL,'2025-11-30 23:55:54'),(4,3,'DELIVERED','ORD004','Lê Văn C','0923456789','789 Đường MNO, Đà Nẵng',NULL,NULL,650000.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,680000.0000,0.0000,NULL,NULL,'2025-12-03 23:55:54','2025-12-03 23:55:54',NULL,NULL,'2025-11-29 23:55:54','2025-11-29 23:55:54',NULL,'2025-11-29 23:55:54'),(5,2,'DELIVERED','ORD005','Trần Thị B','0912345678','456 Đường XYZ, TP.HCM',NULL,NULL,350000.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,380000.0000,0.0000,NULL,NULL,'2025-12-03 23:55:54','2025-12-03 23:55:54',NULL,NULL,'2025-11-28 23:55:54','2025-11-28 23:55:54',NULL,'2025-11-28 23:55:54'),(6,4,'CLOSED','ORD006','Phạm Thị D','0934567890','321 Đường PQR, Hải Phòng',NULL,NULL,450000.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,480000.0000,480000.0000,NULL,NULL,'2025-12-03 23:55:54','2025-12-03 23:55:54',NULL,NULL,'2025-12-02 23:55:54',NULL,NULL,NULL),(7,5,'CANCELED','ORD007','Hoàng Văn E','0945678901','654 Đường STU, Huế',NULL,NULL,750000.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,790000.0000,790000.0000,NULL,NULL,'2025-12-03 23:55:54','2025-12-03 23:55:54',NULL,NULL,'2025-11-27 23:55:54',NULL,NULL,NULL);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `payment_method` enum('COD','BANK_TRANSFER','GATEWAY','WALLET') COLLATE utf8mb4_unicode_ci NOT NULL,
  `amount` decimal(19,4) NOT NULL,
  `status` enum('PENDING','PROCESSING','SUCCESS','FAILED','CANCELED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `transaction_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reference_number` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `notes` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `paid_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_payment_order` (`order_id`),
  KEY `idx_payment_status` (`status`),
  KEY `idx_payment_method` (`payment_method`),
  CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (16,1,'BANK_TRANSFER',530000.0000,'PENDING',NULL,NULL,NULL,'2025-12-04 00:05:56','2025-12-04 00:05:56',NULL,NULL,'2025-12-03 00:05:56'),(17,2,'WALLET',850000.0000,'SUCCESS',NULL,NULL,NULL,'2025-12-04 00:05:56','2025-12-04 00:05:56',NULL,NULL,'2025-12-02 00:05:56'),(18,3,'BANK_TRANSFER',1250000.0000,'PROCESSING',NULL,NULL,NULL,'2025-12-04 00:05:56','2025-12-04 00:05:56',NULL,NULL,'2025-12-01 00:05:56'),(19,4,'COD',680000.0000,'CANCELED',NULL,NULL,NULL,'2025-12-04 00:05:56','2025-12-04 00:05:56',NULL,NULL,'2025-11-30 00:05:56'),(20,5,'GATEWAY',380000.0000,'FAILED',NULL,NULL,NULL,'2025-12-04 00:05:56','2025-12-04 00:05:56',NULL,NULL,'2025-11-29 00:05:56');
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `category` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` decimal(19,4) NOT NULL,
  `image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `stock_quantity` bigint NOT NULL DEFAULT '0',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_product_category` (`category`),
  KEY `idx_product_active` (`is_active`),
  KEY `idx_product_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'Bút Bi Thiên Long TL-079','Bút bi cao cấp, mực xanh, viết mượt','Bút viết',3000.0000,'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQW3HeWCbo0JsPJdJ3vjJH1WJjB9tC2PbZoXQ&s',500,1,'2025-12-07 22:03:14','2025-12-07 22:08:20'),(2,'Bút Gel Thiên Long GEL-08','Bút gel nước, nhiều màu, viết êm','Bút viết',5000.0000,'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSNwAN-9-YaAG_G8HdHmwAOycQboj-zebCnow&s',450,1,'2025-12-07 22:03:14','2025-12-07 22:09:03'),(3,'Bút Lông Dầu Thiên Long PM-08','Bút lông dầu không lem, màu đen','Bút viết',15000.0000,'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRNCD6R6MyxAMe_9LSnu01Hva1VrRkSXM2RIQ&s',300,1,'2025-12-07 22:03:14','2025-12-07 22:09:31'),(4,'Bút Chì 2B Thiên Long PC-052','Bút chì gỗ, ruột chì 2B, viết đậm','Bút viết',4000.0000,'https://product.hstatic.net/1000230347/product/but_chi_bam_thien_long_pc-022_259ef67ff26b442fb163d06997f45f5f_1024x1024.jpg',400,1,'2025-12-07 22:03:14','2025-12-07 22:19:58'),(5,'Bút Dạ Quang 6 Màu','Bộ bút đánh dấu dạ quang 6 màu rực rỡ','Bút viết',45000.0000,'https://hachihachi.com.vn/Uploads/_6/productimage/4941829006568-(1).jpg',200,1,'2025-12-07 22:03:14','2025-12-07 22:22:32'),(6,'Vở Ô Ly Campus 200 Trang','Vở ô ly khổ A4, giấy trắng, bìa cứng','Vở và Sổ',25000.0000,'https://img.lazcdn.com/g/p/678d7fdd129a39605ec43eafe4401abc.jpg_720x720q80.jpg',350,1,'2025-12-07 22:03:14','2025-12-07 22:22:49'),(7,'Sổ Tay Bìa Da A5','Sổ tay cao cấp bìa da, 100 trang','Vở và Sổ',85000.0000,'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQV0xj8wCt9kiqah1icNyEJ7O4s7GKAMPQ07A&s',150,1,'2025-12-07 22:03:14','2025-12-07 22:23:57'),(8,'Vở Kẻ Ngang 96 Trang','Vở kẻ ngang tiêu chuẩn học sinh','Vở và Sổ',12000.0000,'https://bizweb.dktcdn.net/100/220/344/products/3-009440fd-8f97-4ed2-b13a-da3cf0c3b3e8.jpg?v=1717672137120',500,1,'2025-12-07 22:03:14','2025-12-07 22:24:39'),(9,'Thước Kẻ Nhựa 30cm','Thước nhựa trong suốt, có chia vạch mm','Dụng cụ học tập',8000.0000,'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTjTFdph1c3AaxpH3fd_iLsT8l7tbfki83wIw&s',400,1,'2025-12-07 22:03:14','2025-12-07 22:25:15'),(10,'Gôm Tẩy Thiên Long E-037','Gôm tẩy trắng, không bị vụn','Dụng cụ học tập',3000.0000,'https://bizweb.dktcdn.net/thumb/1024x1024/100/379/648/products/compressed-20240731-092356.jpg?v=1722422869147',600,1,'2025-12-07 22:03:14','2025-12-07 22:25:54'),(11,'Bộ Compa Học Sinh','Bộ compa toán học, hộp nhựa','Dụng cụ học tập',35000.0000,'https://butmaisenviet.vn/wp-content/uploads/2025/07/2-7.webp',180,1,'2025-12-07 22:03:14','2025-12-07 22:26:31'),(12,'Máy Tính Casio FX-580VN X','Máy tính khoa học, 552 chức năng','Dụng cụ học tập',450000.0000,'https://bizweb.dktcdn.net/thumb/1024x1024/100/467/726/products/b231353f30f31863b7dea65440176af8.jpg?v=1744883893557',120,1,'2025-12-07 22:03:14','2025-12-07 22:27:09'),(13,'Kéo Văn Phòng Inox 21cm','Kéo cắt giấy inox sắc bén','Văn phòng phẩm',35000.0000,'https://vn-test-11.slatic.net/shop/70d09edbded418528832310f0541d754.jpeg',250,1,'2025-12-07 22:03:14','2025-12-07 22:27:39'),(14,'Hồ Dán UHU 40g','Hồ dán đa năng, không độc hại','Văn phòng phẩm',25000.0000,'https://down-vn.img.susercontent.com/file/vn-11134207-7r98o-lpuyti25a4hjdd',300,1,'2025-12-07 22:03:14','2025-12-07 22:28:23'),(15,'Băng Keo Trong 2.4cm x 50y','Băng keo trong suốt, dính tốt','Văn phòng phẩm',18000.0000,'https://bangkeohuynhgia.com/upload/product/791419_z2430443039873_bb6ce00ec7db40ac3288c2a22961fc06.jpg_570x510.jpg',350,1,'2025-12-07 22:03:14','2025-12-07 22:29:04');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promotion_usages`
--

DROP TABLE IF EXISTS `promotion_usages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `promotion_usages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `promotion_id` bigint NOT NULL,
  `order_id` bigint NOT NULL,
  `account_id` bigint NOT NULL,
  `discount_amount` decimal(19,4) NOT NULL,
  `used_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_promotion_usage_promo` (`promotion_id`),
  KEY `idx_promotion_usage_order` (`order_id`),
  KEY `idx_promotion_usage_account` (`account_id`),
  CONSTRAINT `promotion_usages_ibfk_1` FOREIGN KEY (`promotion_id`) REFERENCES `promotions` (`id`),
  CONSTRAINT `promotion_usages_ibfk_2` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promotion_usages`
--

LOCK TABLES `promotion_usages` WRITE;
/*!40000 ALTER TABLE `promotion_usages` DISABLE KEYS */;
/*!40000 ALTER TABLE `promotion_usages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promotions`
--

DROP TABLE IF EXISTS `promotions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `promotions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `discount_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `discount_value` decimal(19,4) NOT NULL,
  `min_order_amount` decimal(19,4) DEFAULT '0.0000',
  `max_discount_amount` decimal(19,4) DEFAULT NULL,
  `applicable_to` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ALL',
  `applicable_item_ids` text COLLATE utf8mb4_unicode_ci,
  `max_usage_total` int DEFAULT NULL,
  `max_usage_per_user` int DEFAULT '1',
  `usage_count` int DEFAULT '0',
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `is_stackable` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `idx_promotion_code` (`code`),
  KEY `idx_promotion_active` (`is_active`),
  KEY `idx_promotion_dates` (`start_date`,`end_date`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promotions`
--

LOCK TABLES `promotions` WRITE;
/*!40000 ALTER TABLE `promotions` DISABLE KEYS */;
INSERT INTO `promotions` VALUES (1,'WELCOME10','Giảm 10% cho đơn hàng đầu tiên khi mới tạo tài khoảng','Mã giảm giá 10% cho khách hàng mới','PERCENTAGE',15.0000,100000.0000,50000.0000,'ALL',NULL,110,1,0,'2025-12-04 17:00:00','2026-03-04 16:59:59',1,0,'2025-12-03 22:05:00','2025-12-07 17:44:37',NULL,NULL),(2,'SALE50K','Giảm 50,000đ','Giảm giá cố định 50,000đ cho đơn từ 500,000đ','FIXED_AMOUNT',50000.0000,5000000.0000,60000.0000,'ALL',NULL,500,2,0,'2025-12-03 17:00:00','2026-01-04 16:59:59',1,0,'2025-12-03 22:05:00','2025-12-03 16:27:42',NULL,NULL),(4,'VIP20','Giảm 20% VIP','Mã giảm 20% cho thành viên VIP','PERCENTAGE',20.0000,1000000.0000,200000.0000,'ALL',NULL,100,10,0,'2025-12-03 22:05:00','2026-06-03 22:05:00',1,0,'2025-12-03 22:05:00','2025-12-03 22:05:00',NULL,NULL),(5,'GS_2025','Khuyến Mãi Giáng Sinh','Khuyến Mãi Giáng Sinh giảm cho các đơn hàng mùa đông','PERCENTAGE',20.0000,500000.0000,100000.0000,'ALL',NULL,100,1,0,'2025-12-03 17:00:00','2025-12-30 16:59:59',1,0,'2025-12-03 16:15:55','2025-12-07 17:10:45',NULL,NULL),(6,'TET_2026','Khuyến mãi Tết 2026','Khuyến mãi Tết 2026 giảm tất cả các sản phẩm','ITEM',100000.0000,500000.0000,30000.0000,'SPECIFIC_ITEMS',NULL,1000,1,0,'2025-12-04 17:00:00','2025-12-29 16:59:59',1,0,'2025-12-03 16:30:42','2025-12-03 16:30:42',NULL,NULL);
/*!40000 ALTER TABLE `promotions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refunds`
--

DROP TABLE IF EXISTS `refunds`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refunds` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `payment_id` bigint NOT NULL,
  `amount` decimal(19,4) NOT NULL,
  `refund_type` enum('FULL','PARTIAL') COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('PENDING','PROCESSING','SUCCESS','FAILED','CANCELED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `refund_transaction_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `refunded_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_refund_payment` (`payment_id`),
  KEY `idx_refund_status` (`status`),
  CONSTRAINT `refunds_ibfk_1` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refunds`
--

LOCK TABLES `refunds` WRITE;
/*!40000 ALTER TABLE `refunds` DISABLE KEYS */;
/*!40000 ALTER TABLE `refunds` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipments`
--

DROP TABLE IF EXISTS `shipments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `tracking_number` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `carrier` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('PENDING','PICKED','PACKED','SHIPPED','IN_TRANSIT','OUT_FOR_DELIVERY','DELIVERED','FAILED','RETURNED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `expected_delivery_date` datetime DEFAULT NULL,
  `actual_delivery_date` datetime DEFAULT NULL,
  `warehouse_id` bigint DEFAULT NULL,
  `notes` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tracking_number` (`tracking_number`),
  KEY `warehouse_id` (`warehouse_id`),
  KEY `idx_shipment_order` (`order_id`),
  KEY `idx_shipment_status` (`status`),
  KEY `idx_shipment_tracking` (`tracking_number`),
  CONSTRAINT `shipments_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `shipments_ibfk_2` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipments`
--

LOCK TABLES `shipments` WRITE;
/*!40000 ALTER TABLE `shipments` DISABLE KEYS */;
/*!40000 ALTER TABLE `shipments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_items`
--

DROP TABLE IF EXISTS `stock_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stock_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `warehouse_id` bigint NOT NULL,
  `item_id` bigint NOT NULL,
  `on_hand` bigint NOT NULL DEFAULT '0',
  `reserved` bigint NOT NULL DEFAULT '0',
  `low_stock_threshold` bigint NOT NULL DEFAULT '10',
  `is_low_stock` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_warehouse_item` (`warehouse_id`,`item_id`),
  UNIQUE KEY `UKlkv28acueo919najilv82cgaq` (`warehouse_id`,`item_id`),
  KEY `idx_stock_warehouse` (`warehouse_id`),
  KEY `idx_stock_item` (`item_id`),
  KEY `idx_stock_low` (`is_low_stock`),
  CONSTRAINT `stock_items_ibfk_1` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_items`
--

LOCK TABLES `stock_items` WRITE;
/*!40000 ALTER TABLE `stock_items` DISABLE KEYS */;
INSERT INTO `stock_items` VALUES (1,1,1,100,0,10,0,'2025-12-03 22:05:00','2025-12-03 22:05:00',NULL,NULL),(2,1,2,5,0,10,1,'2025-12-03 22:05:00','2025-12-04 00:05:56',NULL,NULL),(3,1,3,200,0,20,0,'2025-12-03 22:05:00','2025-12-03 22:05:00',NULL,NULL),(4,2,1,150,0,10,0,'2025-12-03 22:05:00','2025-12-03 22:05:00',NULL,NULL),(5,2,4,8,0,10,1,'2025-12-03 22:05:00','2025-12-04 00:05:56',NULL,NULL),(6,3,2,0,0,10,1,'2025-12-03 22:05:00','2025-12-04 00:05:56',NULL,NULL);
/*!40000 ALTER TABLE `stock_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `warehouses`
--

DROP TABLE IF EXISTS `warehouses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `warehouses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `warehouses`
--

LOCK TABLES `warehouses` WRITE;
/*!40000 ALTER TABLE `warehouses` DISABLE KEYS */;
INSERT INTO `warehouses` VALUES (1,'Kho Miền Bắc','123 Đường Kim Mã, Hà Nội','024 1234 5678',1,'2025-12-03 22:05:00','2025-12-03 22:05:00',NULL,NULL),(2,'Kho Miền Nam','456 Đường Nguyễn Huệ, TP.HCM','028 7654 3210',1,'2025-12-03 22:05:00','2025-12-03 22:05:00',NULL,NULL),(3,'Kho Miền Trung','789 Đường Hàng Vôi, Đà Nẵng','0236 7894 5612',1,'2025-12-03 22:05:00','2025-12-03 22:05:00',NULL,NULL);
/*!40000 ALTER TABLE `warehouses` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-08 10:53:00
