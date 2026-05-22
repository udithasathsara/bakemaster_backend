-- Admin user: admin / admin123
INSERT IGNORE INTO users (username, password, role) VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ROLE_ADMIN');
-- Staff user: staff / staff123
INSERT IGNORE INTO users (username, password, role) VALUES ('staff', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ROLE_STAFF');

-- Suppliers
INSERT INTO supplier (name, contact, lead_time_days, pricing_score) VALUES ('Flour Mill Inc.', '123-456', 2, 4.5);
INSERT INTO supplier (name, contact, lead_time_days, pricing_score) VALUES ('Dairy Fresh Co.', '789-012', 1, 4.0);

-- Ingredients
INSERT INTO ingredient (name, quantity, unit, expiry_date, reorder_threshold) VALUES ('All-Purpose Flour', 25.0, 'kg', '2026-06-01', 10.0);
INSERT INTO ingredient (name, quantity, unit, expiry_date, reorder_threshold) VALUES ('Butter', 3.5, 'kg', '2026-05-30', 5.0);
INSERT INTO ingredient (name, quantity, unit, expiry_date, reorder_threshold) VALUES ('Eggs', 8.0, 'dozen', '2026-05-28', 4.0);
INSERT INTO ingredient (name, quantity, unit, expiry_date, reorder_threshold) VALUES ('Sugar', 12.0, 'kg', '2026-12-31', 8.0);

-- Customers
INSERT INTO customer (name, phone, email, loyalty_points) VALUES ('Alice Johnson', '111-111', 'alice@mail.com', 20);
INSERT INTO customer (name, phone, email, loyalty_points) VALUES ('Bob Smith', '222-222', 'bob@mail.com', 45);

-- Orders (some pending)
INSERT INTO bakery_orders (customer_id, order_date, status, channel, delivery_deadline) VALUES (1, CURDATE(), 'PENDING', 'WALK_IN', DATE_ADD(CURDATE(), INTERVAL 2 DAY));
INSERT INTO bakery_orders (customer_id, order_date, status, channel, delivery_deadline) VALUES (2, CURDATE(), 'IN_PROGRESS', 'PHONE', DATE_ADD(CURDATE(), INTERVAL 1 DAY));
INSERT INTO bakery_orders (customer_id, order_date, status, channel, delivery_deadline) VALUES (1, CURDATE(), 'BAKING', 'EMAIL', DATE_ADD(CURDATE(), INTERVAL 3 DAY));

-- Order items
INSERT INTO order_item (order_id, product_name, quantity, unit_price) VALUES (1, 'Chocolate Cake', 1, 35.00);
INSERT INTO order_item (order_id, product_name, quantity, unit_price) VALUES (2, 'Croissant', 10, 2.50);
INSERT INTO order_item (order_id, product_name, quantity, unit_price) VALUES (3, 'Vanilla Cupcake', 6, 3.00);