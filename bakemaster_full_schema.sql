-- =============================================================================
-- BakeMaster Complete Database Creation & Initialization Script
-- Database Engine: MySQL 8.0+
-- Database Name: bakemaster
-- Description: Complete schema containing all 13 tables, relationships,
--              foreign key constraints, and seed data.
-- =============================================================================

CREATE DATABASE IF NOT EXISTS `bakemaster` 
  DEFAULT CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;

USE `bakemaster`;

-- Temporarily disable foreign key checks during table drop/creation
SET FOREIGN_KEY_CHECKS = 0;

-- -----------------------------------------------------------------------------
-- 1. Table: users (Authentication & Role-Based Access Control)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(255) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `role` VARCHAR(50) NOT NULL COMMENT 'ROLE_SUPER_ADMIN, ROLE_ADMIN, ROLE_MANAGER, ROLE_STAFF',
  `full_name` VARCHAR(255) DEFAULT NULL,
  `email` VARCHAR(255) DEFAULT NULL,
  `phone` VARCHAR(50) DEFAULT NULL,
  `active` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 2. Table: staff (Bakery Workers, Shift Schedules & Operational Roles)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `staff`;
CREATE TABLE `staff` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `role` VARCHAR(100) DEFAULT NULL COMMENT 'BAKER, DECORATOR, DELIVERY, CASHIER, MANAGER',
  `phone` VARCHAR(50) DEFAULT NULL,
  `email` VARCHAR(255) DEFAULT NULL,
  `shift_start` TIME DEFAULT NULL,
  `shift_end` TIME DEFAULT NULL,
  `active` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 3. Table: supplier (Raw Ingredient Suppliers & Lead Times)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `supplier`;
CREATE TABLE `supplier` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `contact` VARCHAR(255) DEFAULT NULL,
  `lead_time_days` INT NOT NULL DEFAULT 0,
  `pricing_score` DOUBLE NOT NULL DEFAULT 0.0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 4. Table: ingredient (Raw Inventory & Perishables Tracking)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `ingredient`;
CREATE TABLE `ingredient` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `quantity` DOUBLE NOT NULL DEFAULT 0.0,
  `unit` VARCHAR(50) DEFAULT NULL COMMENT 'kg, dozen, litre, bottle',
  `expiry_date` DATE DEFAULT NULL,
  `reorder_threshold` DOUBLE NOT NULL DEFAULT 0.0,
  `cost_per_unit` DOUBLE NOT NULL DEFAULT 0.0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 5. Table: products (Finished Bakery Goods & Catalog)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL UNIQUE,
  `category` VARCHAR(100) DEFAULT NULL COMMENT 'CAKE, BREAD, PASTRY, COOKIE, CUPCAKE',
  `selling_price` DOUBLE NOT NULL DEFAULT 0.0,
  `cost_price` DOUBLE NOT NULL DEFAULT 0.0,
  `shelf_life_days` INT NOT NULL DEFAULT 0,
  `description` VARCHAR(1000) DEFAULT NULL,
  `image_url` VARCHAR(500) DEFAULT NULL,
  `active` TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 6. Table: recipe_items (Bill of Materials / Ingredients per Product)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `recipe_items`;
CREATE TABLE `recipe_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `product_id` BIGINT NOT NULL,
  `ingredient_id` BIGINT NOT NULL,
  `quantity_required` DOUBLE NOT NULL,
  `unit` VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_recipe_product_idx` (`product_id`),
  KEY `fk_recipe_ingredient_idx` (`ingredient_id`),
  CONSTRAINT `fk_recipe_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_recipe_ingredient` FOREIGN KEY (`ingredient_id`) REFERENCES `ingredient` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 7. Table: customer (Bakery Customers & Loyalty Points)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `phone` VARCHAR(50) DEFAULT NULL,
  `email` VARCHAR(255) DEFAULT NULL,
  `loyalty_points` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 8. Table: bakery_orders (Customer Orders & Lifecycle Tracking)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `bakery_orders`;
CREATE TABLE `bakery_orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `customer_id` BIGINT NOT NULL,
  `order_date` DATE DEFAULT NULL,
  `status` VARCHAR(50) NOT NULL COMMENT 'RECEIVED, CONFIRMED, IN_PROGRESS, COMPLETED, DELIVERED, CANCELLED',
  `channel` VARCHAR(50) DEFAULT NULL COMMENT 'WALK_IN, PHONE, EMAIL',
  `delivery_address` VARCHAR(255) DEFAULT NULL,
  `delivery_deadline` DATE DEFAULT NULL,
  `total_amount` DOUBLE NOT NULL DEFAULT 0.0,
  `payment_status` VARCHAR(50) DEFAULT NULL COMMENT 'PENDING, PARTIAL_DEPOSIT, PAID',
  `notes` VARCHAR(1000) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_orders_customer_idx` (`customer_id`),
  CONSTRAINT `fk_orders_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 9. Table: order_item (Line Items within Customer Orders)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `product_id` BIGINT DEFAULT NULL,
  `product_name` VARCHAR(255) NOT NULL,
  `quantity` INT NOT NULL,
  `unit_price` DOUBLE NOT NULL DEFAULT 0.0,
  `subtotal` DOUBLE NOT NULL DEFAULT 0.0,
  `special_instructions` VARCHAR(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_order_item_order_idx` (`order_id`),
  KEY `fk_order_item_product_idx` (`product_id`),
  CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `bakery_orders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_order_item_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 10. Table: production_tasks (Kitchen Baking/Decorating/Packaging Tasks)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `production_tasks`;
CREATE TABLE `production_tasks` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT DEFAULT NULL,
  `order_item_id` BIGINT DEFAULT NULL,
  `product_name` VARCHAR(255) DEFAULT NULL,
  `quantity` INT DEFAULT 1,
  `task_type` VARCHAR(50) DEFAULT NULL COMMENT 'BAKING, DECORATING, PACKAGING, DELIVERY',
  `status` VARCHAR(50) DEFAULT 'PENDING' COMMENT 'PENDING, IN_PROGRESS, COMPLETED, CANCELLED',
  `assigned_staff_id` BIGINT DEFAULT NULL,
  `scheduled_start` DATETIME DEFAULT NULL,
  `scheduled_end` DATETIME DEFAULT NULL,
  `actual_start` DATETIME DEFAULT NULL,
  `actual_end` DATETIME DEFAULT NULL,
  `priority` INT NOT NULL DEFAULT 2 COMMENT '1=High, 2=Medium, 3=Low',
  `notes` VARCHAR(1000) DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_prod_task_order_idx` (`order_id`),
  KEY `fk_prod_task_item_idx` (`order_item_id`),
  KEY `fk_prod_task_staff_idx` (`assigned_staff_id`),
  CONSTRAINT `fk_prod_task_order` FOREIGN KEY (`order_id`) REFERENCES `bakery_orders` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_prod_task_item` FOREIGN KEY (`order_item_id`) REFERENCES `order_item` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_prod_task_staff` FOREIGN KEY (`assigned_staff_id`) REFERENCES `staff` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 11. Table: waste_logs (Ingredient Loss, Spoilage, and Burnt Batches)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `waste_logs`;
CREATE TABLE `waste_logs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `ingredient_id` BIGINT DEFAULT NULL,
  `ingredient_name` VARCHAR(255) NOT NULL,
  `quantity` DOUBLE NOT NULL,
  `unit` VARCHAR(50) DEFAULT NULL,
  `reason` VARCHAR(100) NOT NULL COMMENT 'EXPIRED, SPOILED, BURNT_IN_OVEN, DAMAGED_IN_TRANSIT, OTHER',
  `waste_date` DATE NOT NULL,
  `estimated_cost_loss` DOUBLE NOT NULL DEFAULT 0.0,
  `logged_by` VARCHAR(255) DEFAULT NULL,
  `notes` VARCHAR(500) DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_waste_ingredient_idx` (`ingredient_id`),
  CONSTRAINT `fk_waste_ingredient` FOREIGN KEY (`ingredient_id`) REFERENCES `ingredient` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 12. Table: purchase_order (Supplier Raw Material Replenishment Orders)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `purchase_order`;
CREATE TABLE `purchase_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `supplier_id` BIGINT NOT NULL,
  `order_date` DATE DEFAULT NULL,
  `received_date` DATE DEFAULT NULL,
  `status` VARCHAR(50) DEFAULT 'PENDING' COMMENT 'PENDING, SENT, RECEIVED, CANCELLED',
  `total_amount` DOUBLE NOT NULL DEFAULT 0.0,
  PRIMARY KEY (`id`),
  KEY `fk_po_supplier_idx` (`supplier_id`),
  CONSTRAINT `fk_po_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 13. Table: purchase_order_item (Items in Supplier Purchase Order)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `purchase_order_item`;
CREATE TABLE `purchase_order_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `po_id` BIGINT NOT NULL,
  `ingredient_id` BIGINT DEFAULT NULL,
  `ingredient_name` VARCHAR(255) DEFAULT NULL,
  `quantity` DOUBLE NOT NULL DEFAULT 0.0,
  PRIMARY KEY (`id`),
  KEY `fk_po_item_po_idx` (`po_id`),
  KEY `fk_po_item_ingredient_idx` (`ingredient_id`),
  CONSTRAINT `fk_po_item_po` FOREIGN KEY (`po_id`) REFERENCES `purchase_order` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_po_item_ingredient` FOREIGN KEY (`ingredient_id`) REFERENCES `ingredient` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
-- SEED DATA (Default Admin/Users, Products, Recipes, Shifts & Inventory)
-- Password for all default users is: password123
-- =============================================================================

-- 1. Default Users
INSERT INTO `users` (`id`, `username`, `password`, `role`, `full_name`, `email`, `phone`, `active`, `created_at`) VALUES 
(1, 'superadmin', '$2a$10$pzmVdNzc4WjHjMykUjWUreSAxXczYOCgVQiJcGYuQcHMpXzcCBDQC', 'ROLE_SUPER_ADMIN', 'Super Admin', 'super@bakemaster.com', '111-111', 1, NOW()),
(2, 'admin', '$2a$10$pzmVdNzc4WjHjMykUjWUreSAxXczYOCgVQiJcGYuQcHMpXzcCBDQC', 'ROLE_ADMIN', 'Admin User', 'admin@bakemaster.com', '222-222', 1, NOW()),
(3, 'manager', '$2a$10$pzmVdNzc4WjHjMykUjWUreSAxXczYOCgVQiJcGYuQcHMpXzcCBDQC', 'ROLE_MANAGER', 'Manager User', 'manager@bakemaster.com', '333-333', 1, NOW()),
(4, 'staff1', '$2a$10$pzmVdNzc4WjHjMykUjWUreSAxXczYOCgVQiJcGYuQcHMpXzcCBDQC', 'ROLE_STAFF', 'Staff One', 'staff1@bakemaster.com', '444-444', 1, NOW())
ON DUPLICATE KEY UPDATE `username`=`username`;

-- 2. Staff Members
INSERT INTO `staff` (`id`, `name`, `role`, `phone`, `email`, `shift_start`, `shift_end`, `active`, `created_at`) VALUES
(1, 'John Baker', 'BAKER', '555-101', 'john@bakery.com', '06:00:00', '14:00:00', 1, NOW()),
(2, 'Mary Decorator', 'DECORATOR', '555-102', 'mary@bakery.com', '08:00:00', '16:00:00', 1, NOW()),
(3, 'Tom Delivery', 'DELIVERY', '555-103', 'tom@bakery.com', '09:00:00', '17:00:00', 1, NOW()),
(4, 'Sarah Cashier', 'CASHIER', '555-104', 'sarah@bakery.com', '10:00:00', '18:00:00', 1, NOW())
ON DUPLICATE KEY UPDATE `name`=`name`;

-- 3. Suppliers
INSERT INTO `supplier` (`id`, `name`, `contact`, `lead_time_days`, `pricing_score`) VALUES
(1, 'Flour Mill Inc.', '123-456', 2, 4.5),
(2, 'Dairy Fresh Co.', '789-012', 1, 4.0),
(3, 'Sugar & Spice Supplies', '345-678', 3, 3.8)
ON DUPLICATE KEY UPDATE `name`=`name`;

-- 4. Ingredients
INSERT INTO `ingredient` (`id`, `name`, `quantity`, `unit`, `expiry_date`, `reorder_threshold`, `cost_per_unit`) VALUES
(1, 'All-Purpose Flour', 60.0, 'kg', '2026-09-01', 20.0, 2.50),
(2, 'Butter', 18.5, 'kg', '2026-07-15', 10.0, 8.00),
(3, 'Eggs', 35.0, 'dozen', '2026-06-25', 15.0, 3.20),
(4, 'Sugar', 30.0, 'kg', '2026-10-01', 12.0, 1.80),
(5, 'Milk', 12.0, 'litre', '2026-06-20', 6.0, 2.40),
(6, 'Vanilla Extract', 2.0, 'bottle', '2026-11-01', 3.0, 14.00),
(7, 'Baking Powder', 5.0, 'kg', '2026-09-15', 2.0, 4.50),
(8, 'Whipping Cream', 4.0, 'litre', '2026-06-15', 5.0, 6.50),
(9, 'Cocoa Powder', 8.5, 'kg', '2026-10-15', 4.0, 7.50),
(10, 'Yeast', 4.0, 'kg', '2026-08-10', 2.0, 5.00),
(11, 'Fresh Strawberries', 3.0, 'kg', CURDATE() + INTERVAL 2 DAY, 4.0, 9.00)
ON DUPLICATE KEY UPDATE `name`=`name`;

-- 5. Finished Bakery Products
INSERT INTO `products` (`id`, `name`, `category`, `selling_price`, `cost_price`, `shelf_life_days`, `description`, `image_url`, `active`) VALUES
(1, 'Chocolate Fudge Cake', 'CAKE', 45.00, 14.50, 3, 'Rich dark chocolate layer cake with creamy fudge frosting', 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=300', 1),
(2, 'Vanilla Dream Cupcake', 'CUPCAKE', 3.50, 0.95, 3, 'Fluffy Madagascar vanilla cupcake with buttercream swirl', 'https://images.unsplash.com/photo-1519869325930-281384150729?w=300', 1),
(3, 'French Butter Croissant', 'PASTRY', 3.00, 0.90, 2, 'Golden layered all-butter flaky Parisian croissant', 'https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=300', 1),
(4, 'Artisan Sourdough Loaf', 'BREAD', 6.00, 1.40, 4, 'Naturally fermented crusty artisanal sourdough bread', 'https://images.unsplash.com/photo-1509440159596-0249088772ff?w=300', 1),
(5, 'Fresh Strawberry Tart', 'PASTRY', 7.50, 2.60, 2, 'Sweet pastry crust filled with crème pâtissière and glazed strawberries', 'https://images.unsplash.com/photo-1464305795204-6f5bbfc7fb81?w=300', 1)
ON DUPLICATE KEY UPDATE `name`=`name`;

-- 6. Recipe Items (Bill of Materials)
INSERT INTO `recipe_items` (`id`, `product_id`, `ingredient_id`, `quantity_required`, `unit`) VALUES
(1, 1, 1, 0.45, 'kg'),
(2, 1, 2, 0.25, 'kg'),
(3, 1, 3, 0.33, 'dozen'),
(4, 1, 4, 0.30, 'kg'),
(5, 1, 9, 0.15, 'kg'),
(6, 1, 6, 0.05, 'bottle'),
(7, 2, 1, 0.06, 'kg'),
(8, 2, 2, 0.04, 'kg'),
(9, 2, 3, 0.08, 'dozen'),
(10, 2, 4, 0.04, 'kg'),
(11, 2, 5, 0.03, 'litre'),
(12, 3, 1, 0.12, 'kg'),
(13, 3, 2, 0.08, 'kg'),
(14, 3, 10, 0.01, 'kg'),
(15, 3, 5, 0.03, 'litre'),
(16, 4, 1, 0.40, 'kg'),
(17, 4, 10, 0.02, 'kg')
ON DUPLICATE KEY UPDATE `quantity_required`=VALUES(`quantity_required`);

-- 7. Customers
INSERT INTO `customer` (`id`, `name`, `phone`, `email`, `loyalty_points`) VALUES
(1, 'Alice Johnson', '111-111', 'alice@mail.com', 120),
(2, 'Bob Smith', '222-222', 'bob@mail.com', 85),
(3, 'Carol White', '333-333', 'carol@mail.com', 210),
(4, 'David Brown', '444-444', 'david@mail.com', 45)
ON DUPLICATE KEY UPDATE `name`=`name`;

-- 8. Customer Orders
INSERT INTO `bakery_orders` (`id`, `customer_id`, `order_date`, `status`, `channel`, `delivery_address`, `delivery_deadline`, `total_amount`, `payment_status`, `notes`) VALUES
(1, 1, CURDATE() - INTERVAL 3 DAY, 'DELIVERED', 'WALK_IN', '12 Rose Lane, Colombo', CURDATE() - INTERVAL 2 DAY, 45.00, 'PAID', 'Pickup at 3 PM'),
(2, 2, CURDATE() - INTERVAL 2 DAY, 'DELIVERED', 'PHONE', '45 Baker St, Colombo', CURDATE() - INTERVAL 1 DAY, 36.00, 'PAID', 'Call upon arrival'),
(3, 3, CURDATE() - INTERVAL 1 DAY, 'COMPLETED', 'EMAIL', '89 Park Ave, Colombo', CURDATE(), 84.00, 'PAID', 'Box with ribbon'),
(4, 4, CURDATE(), 'IN_PROGRESS', 'WALK_IN', 'Counter Pickup', CURDATE() + INTERVAL 1 DAY, 45.00, 'PARTIAL_DEPOSIT', 'Birthday inscription: Happy Birthday Kevin'),
(5, 1, CURDATE(), 'CONFIRMED', 'PHONE', '12 Rose Lane, Colombo', CURDATE() + INTERVAL 2 DAY, 42.00, 'PAID', 'Please deliver before noon'),
(6, 2, CURDATE(), 'RECEIVED', 'EMAIL', '45 Baker St, Colombo', CURDATE() + INTERVAL 1 DAY, 18.00, 'PENDING', 'Fresh morning bake requested')
ON DUPLICATE KEY UPDATE `status`=VALUES(`status`);

-- 9. Order Items
INSERT INTO `order_item` (`id`, `order_id`, `product_id`, `product_name`, `quantity`, `unit_price`, `subtotal`, `special_instructions`) VALUES
(1, 1, 1, 'Chocolate Fudge Cake', 1, 45.00, 45.00, 'Standard chocolate icing'),
(2, 2, 3, 'French Butter Croissant', 12, 3.00, 36.00, 'Extra golden crust'),
(3, 3, 2, 'Vanilla Dream Cupcake', 24, 3.50, 84.00, 'Pastel pink sprinkles'),
(4, 4, 1, 'Chocolate Fudge Cake', 1, 45.00, 45.00, 'Write: Happy Birthday Kevin'),
(5, 5, 2, 'Vanilla Dream Cupcake', 12, 3.50, 42.00, 'Blue buttercream'),
(6, 6, 3, 'French Butter Croissant', 6, 3.00, 18.00, 'Warm pack')
ON DUPLICATE KEY UPDATE `product_name`=VALUES(`product_name`);

-- 10. Kitchen Production Tasks
INSERT INTO `production_tasks` (`id`, `order_id`, `order_item_id`, `product_name`, `quantity`, `task_type`, `status`, `assigned_staff_id`, `scheduled_start`, `scheduled_end`, `priority`, `notes`) VALUES
(1, 4, 4, 'Chocolate Fudge Cake', 1, 'BAKING', 'COMPLETED', 1, DATE_ADD(CURDATE(), INTERVAL 6 HOUR), DATE_ADD(CURDATE(), INTERVAL 8 HOUR), 1, 'Bake 1x Chocolate Fudge Cake base'),
(2, 4, 4, 'Chocolate Fudge Cake', 1, 'DECORATING', 'IN_PROGRESS', 2, DATE_ADD(CURDATE(), INTERVAL 8 HOUR), DATE_ADD(CURDATE(), INTERVAL 10 HOUR), 2, 'Happy Birthday Kevin inscription in gold'),
(3, 4, 4, 'Chocolate Fudge Cake', 1, 'PACKAGING', 'PENDING', 3, DATE_ADD(CURDATE(), INTERVAL 10 HOUR), DATE_ADD(CURDATE(), INTERVAL 11 HOUR), 3, 'Box in large cake packaging'),
(4, 5, 5, 'Vanilla Dream Cupcake', 12, 'BAKING', 'PENDING', 1, DATE_ADD(CURDATE(), INTERVAL 9 HOUR), DATE_ADD(CURDATE(), INTERVAL 11 HOUR), 1, 'Bake batch of 12 cupcakes')
ON DUPLICATE KEY UPDATE `status`=VALUES(`status`);

-- 11. Food Spoilage & Waste Logs
INSERT INTO `waste_logs` (`id`, `ingredient_id`, `ingredient_name`, `quantity`, `unit`, `reason`, `waste_date`, `estimated_cost_loss`, `logged_by`, `notes`) VALUES
(1, 8, 'Whipping Cream', 1.5, 'litre', 'EXPIRED', CURDATE() - INTERVAL 5 DAY, 9.75, 'John Baker', 'Carton past expiry date in walk-in chiller'),
(2, 11, 'Fresh Strawberries', 0.8, 'kg', 'SPOILED', CURDATE() - INTERVAL 2 DAY, 7.20, 'Mary Decorator', 'Bruised and moldy strawberries discarded'),
(3, 1, 'All-Purpose Flour', 2.0, 'kg', 'BURNT_IN_OVEN', CURDATE() - INTERVAL 1 DAY, 5.00, 'John Baker', 'Oven temperature sensor error burnt croissant batch')
ON DUPLICATE KEY UPDATE `reason`=VALUES(`reason`);

-- 12. Purchase Orders
INSERT INTO `purchase_order` (`id`, `supplier_id`, `order_date`, `received_date`, `status`, `total_amount`) VALUES
(1, 1, CURDATE() - INTERVAL 7 DAY, CURDATE() - INTERVAL 5 DAY, 'RECEIVED', 150.00),
(2, 2, CURDATE() - INTERVAL 2 DAY, NULL, 'SENT', 80.00),
(3, 3, CURDATE(), NULL, 'PENDING', 45.00)
ON DUPLICATE KEY UPDATE `status`=VALUES(`status`);

-- 13. Purchase Order Items
INSERT INTO `purchase_order_item` (`id`, `po_id`, `ingredient_id`, `ingredient_name`, `quantity`) VALUES
(1, 1, 1, 'All-Purpose Flour', 50.0),
(2, 1, 7, 'Baking Powder', 10.0),
(3, 2, 2, 'Butter', 10.0),
(4, 3, 4, 'Sugar', 25.0)
ON DUPLICATE KEY UPDATE `quantity`=VALUES(`quantity`);
