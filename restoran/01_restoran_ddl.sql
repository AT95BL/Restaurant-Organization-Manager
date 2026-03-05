-- ============================================================
-- RESTORAN - DDL Skripta za generisanje šeme baze podataka
-- Ciljni DBMS: MySQL 8.0
-- Autor: Student ETF Banja Luka
-- ============================================================

CREATE DATABASE IF NOT EXISTS `restoran`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE `restoran`;

SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- Tabela: role
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id`   INT          NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45)  NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabela: employee
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
  `id`      INT          NOT NULL AUTO_INCREMENT,
  `name`    VARCHAR(45)  NOT NULL,
  `email`   VARCHAR(45)  NOT NULL,
  `phone`   VARCHAR(45)  DEFAULT NULL,
  `salary`  DOUBLE       NOT NULL,
  `Role_id` INT          NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `employee_email_UNIQUE` (`email`),
  KEY `fk_Employee_Role_idx` (`Role_id`),
  CONSTRAINT `fk_Employee_Role`
    FOREIGN KEY (`Role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabela: user_account  (za login u aplikaciju)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `user_account`;
CREATE TABLE `user_account` (
  `id`          INT          NOT NULL AUTO_INCREMENT,
  `username`    VARCHAR(45)  NOT NULL,
  `password`    VARCHAR(255) NOT NULL,
  `Employee_id` INT          NOT NULL,
  `active`      TINYINT      NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ua_username_UNIQUE` (`username`),
  KEY `fk_UserAccount_Employee_idx` (`Employee_id`),
  CONSTRAINT `fk_UserAccount_Employee`
    FOREIGN KEY (`Employee_id`) REFERENCES `employee` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabela: shift
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `shift`;
CREATE TABLE `shift` (
  `id`          INT   NOT NULL AUTO_INCREMENT,
  `date`        DATE  NOT NULL,
  `start_time`  TIME  DEFAULT NULL,
  `end_time`    TIME  DEFAULT NULL,
  `Employee_id` INT   NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_Shift_Employee_idx` (`Employee_id`),
  CONSTRAINT `fk_Shift_Employee`
    FOREIGN KEY (`Employee_id`) REFERENCES `employee` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabela: supplier
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `supplier`;
CREATE TABLE `supplier` (
  `id`      INT          NOT NULL AUTO_INCREMENT,
  `name`    VARCHAR(100) NOT NULL,
  `email`   VARCHAR(100) DEFAULT NULL,
  `phone`   VARCHAR(45)  DEFAULT NULL,
  `address` VARCHAR(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `supplier_name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabela: ingredient
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `ingredient`;
CREATE TABLE `ingredient` (
  `id`          INT          NOT NULL AUTO_INCREMENT,
  `name`        VARCHAR(45)  NOT NULL,
  `unit`        VARCHAR(20)  NOT NULL DEFAULT 'kom',
  `stock_qty`   DECIMAL(8,2) NOT NULL DEFAULT 0,
  `Supplier_id` INT          DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ingredient_name_UNIQUE` (`name`),
  KEY `fk_Ingredient_Supplier_idx` (`Supplier_id`),
  CONSTRAINT `fk_Ingredient_Supplier`
    FOREIGN KEY (`Supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabela: category
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id`   INT         NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `category_name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabela: item  (stavke menija)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `item`;
CREATE TABLE `item` (
  `id`          INT            NOT NULL AUTO_INCREMENT,
  `name`        VARCHAR(45)    NOT NULL,
  `price`       DECIMAL(8,2)   NOT NULL,
  `on_menu`     TINYINT        NOT NULL DEFAULT 1,
  `description` VARCHAR(200)   DEFAULT NULL,
  `Category_id` INT            NOT NULL,
  `picture`     VARCHAR(500)   DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `item_name_UNIQUE` (`name`),
  KEY `fk_Item_Category_idx` (`Category_id`),
  CONSTRAINT `fk_Item_Category`
    FOREIGN KEY (`Category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabela: itemhasingredient
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `itemhasingredient`;
CREATE TABLE `itemhasingredient` (
  `Item_id`       INT NOT NULL,
  `Ingredient_id` INT NOT NULL,
  `quantity`      INT NOT NULL,
  PRIMARY KEY (`Item_id`, `Ingredient_id`),
  KEY `fk_IHI_Ingredient_idx` (`Ingredient_id`),
  CONSTRAINT `fk_IHI_Item`
    FOREIGN KEY (`Item_id`) REFERENCES `item` (`id`),
  CONSTRAINT `fk_IHI_Ingredient`
    FOREIGN KEY (`Ingredient_id`) REFERENCES `ingredient` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabela: _table  (stolovi u restoranu)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `_table`;
CREATE TABLE `_table` (
  `id`       INT         NOT NULL AUTO_INCREMENT,
  `capacity` INT         NOT NULL,
  `location` VARCHAR(45) NOT NULL DEFAULT 'Sala',
  `status`   VARCHAR(20) NOT NULL DEFAULT 'slobodan',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabela: customer
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
  `id`    INT         NOT NULL AUTO_INCREMENT,
  `name`  VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) DEFAULT NULL,
  `phone` VARCHAR(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabela: reservation
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `reservation`;
CREATE TABLE `reservation` (
  `id`          INT         NOT NULL AUTO_INCREMENT,
  `date`        DATE        NOT NULL,
  `time`        TIME        NOT NULL,
  `duration`    INT         DEFAULT NULL COMMENT 'trajanje u minutima',
  `note`        VARCHAR(200) DEFAULT NULL,
  `status`      VARCHAR(20) NOT NULL DEFAULT 'aktivna',
  `_Table_id`   INT         NOT NULL,
  `Customer_id` INT         NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_Reservation_Table_idx` (`_Table_id`),
  KEY `fk_Reservation_Customer_idx` (`Customer_id`),
  CONSTRAINT `fk_Reservation_Table`
    FOREIGN KEY (`_Table_id`) REFERENCES `_table` (`id`),
  CONSTRAINT `fk_Reservation_Customer`
    FOREIGN KEY (`Customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabela: discount
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `discount`;
CREATE TABLE `discount` (
  `id`         INT           NOT NULL AUTO_INCREMENT,
  `code`       VARCHAR(50)   DEFAULT NULL,
  `percentage` DECIMAL(5,2)  NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `discount_code_UNIQUE` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabela: paymenttype
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `paymenttype`;
CREATE TABLE `paymenttype` (
  `id`   INT         NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabela: payment
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
  `id`              INT           NOT NULL AUTO_INCREMENT,
  `amount`          DECIMAL(10,2) NOT NULL,
  `timestamp`       TIMESTAMP(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `PaymentType_id`  INT           NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_Payment_PaymentType_idx` (`PaymentType_id`),
  CONSTRAINT `fk_Payment_PaymentType`
    FOREIGN KEY (`PaymentType_id`) REFERENCES `paymenttype` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabela: `order`
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id`          INT          NOT NULL AUTO_INCREMENT,
  `status`      VARCHAR(45)  NOT NULL DEFAULT 'otvoren',
  `timestamp`   TIMESTAMP(6) NULL DEFAULT CURRENT_TIMESTAMP(6),
  `note`        VARCHAR(200) DEFAULT NULL,
  `Employee_id` INT          NOT NULL,
  `Customer_id` INT          DEFAULT NULL,
  `Discount_id` INT          DEFAULT NULL,
  `_Table_id`   INT          DEFAULT NULL,
  `Payment_id`  INT          NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_Order_Employee_idx`  (`Employee_id`),
  KEY `fk_Order_Customer_idx`  (`Customer_id`),
  KEY `fk_Order_Discount_idx`  (`Discount_id`),
  KEY `fk_Order_Table_idx`     (`_Table_id`),
  KEY `fk_Order_Payment_idx`   (`Payment_id`),
  CONSTRAINT `fk_Order_Employee`
    FOREIGN KEY (`Employee_id`) REFERENCES `employee` (`id`),
  CONSTRAINT `fk_Order_Customer`
    FOREIGN KEY (`Customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `fk_Order_Discount`
    FOREIGN KEY (`Discount_id`) REFERENCES `discount` (`id`),
  CONSTRAINT `fk_Order_Table`
    FOREIGN KEY (`_Table_id`) REFERENCES `_table` (`id`),
  CONSTRAINT `fk_Order_Payment`
    FOREIGN KEY (`Payment_id`) REFERENCES `payment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabela: ordereditem
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `ordereditem`;
CREATE TABLE `ordereditem` (
  `id`       INT           NOT NULL AUTO_INCREMENT,
  `quantity` INT           NOT NULL,
  `price`    DECIMAL(8,2)  NOT NULL,
  `Item_id`  INT           NOT NULL,
  `Order_id` INT           NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_OI_Item_idx`  (`Item_id`),
  KEY `fk_OI_Order_idx` (`Order_id`),
  CONSTRAINT `fk_OI_Item`
    FOREIGN KEY (`Item_id`) REFERENCES `item` (`id`),
  CONSTRAINT `fk_OI_Order`
    FOREIGN KEY (`Order_id`) REFERENCES `order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabela: purchase  (nabavka od dobavljača)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `purchase`;
CREATE TABLE `purchase` (
  `id`          INT          NOT NULL AUTO_INCREMENT,
  `timestamp`   TIMESTAMP(6) NULL DEFAULT CURRENT_TIMESTAMP(6),
  `Supplier_id` INT          DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_Purchase_Supplier_idx` (`Supplier_id`),
  CONSTRAINT `fk_Purchase_Supplier`
    FOREIGN KEY (`Supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabela: purchaseitemingredient
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `purchaseitemingredient`;
CREATE TABLE `purchaseitemingredient` (
  `id`                  INT NOT NULL AUTO_INCREMENT,
  `Ingredient_id`       INT DEFAULT NULL,
  `Item_id`             INT DEFAULT NULL,
  `Purchase_id`         INT NOT NULL,
  `item_quantity`       INT NOT NULL DEFAULT 0,
  `ingredient_quantity` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `fk_PII_Item_idx`       (`Item_id`),
  KEY `fk_PII_Purchase_idx`   (`Purchase_id`),
  KEY `fk_PII_Ingredient_idx` (`Ingredient_id`),
  CONSTRAINT `fk_PII_Ingredient`
    FOREIGN KEY (`Ingredient_id`) REFERENCES `ingredient` (`id`),
  CONSTRAINT `fk_PII_Item`
    FOREIGN KEY (`Item_id`) REFERENCES `item` (`id`),
  CONSTRAINT `fk_PII_Purchase`
    FOREIGN KEY (`Purchase_id`) REFERENCES `purchase` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
