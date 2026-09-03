-- ==========================================================
-- BakeMaster Seed Data for Small-Scale Bakery Management
-- Case Study: Party Treats Bakery
-- ==========================================================

-- 1. Users with Role-Based Access
INSERT IGNORE INTO users (id, username, password, role, full_name, email, phone, active) VALUES 
(1, 'superadmin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ROLE_SUPER_ADMIN', 'Super Admin', 'super@bakemaster.com', '111-111', true),
(2, 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ROLE_ADMIN', 'Admin User', 'admin@bakemaster.com', '222-222', true),
(3, 'manager', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ROLE_MANAGER', 'Manager User', 'manager@bakemaster.com', '333-333', true),
(4, 'staff1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ROLE_STAFF', 'Staff One', 'staff1@bakemaster.com', '444-444', true);

-- 2. Staff Members & Bakery Shifts
INSERT IGNORE INTO staff (id, name, role, phone, email, shift_start, shift_end, active) VALUES
(1, 'John Baker', 'BAKER', '555-101', 'john@bakery.com', '06:00', '14:00', true),
(2, 'Mary Decorator', 'DECORATOR', '555-102', 'mary@bakery.com', '08:00', '16:00', true),
(3, 'Tom Delivery', 'DELIVERY', '555-103', 'tom@bakery.com', '09:00', '17:00', true),
(4, 'Sarah Cashier', 'CASHIER', '555-104', 'sarah@bakery.com', '10:00', '18:00', true);

-- 3. Ingredient Suppliers
INSERT IGNORE INTO supplier (id, name, contact, lead_time_days, pricing_score) VALUES
(1, 'Flour Mill Inc.', '123-456', 2, 4.5),
(2, 'Dairy Fresh Co.', '789-012', 1, 4.0),
(3, 'Sugar & Spice Supplies', '345-678', 3, 3.8);

-- 4. Raw Ingredients (Perishables with Expiry & Reorder Thresholds)
INSERT IGNORE INTO ingredient (id, name, quantity, unit, expiry_date, reorder_threshold, cost_per_unit) VALUES
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
(11, 'Fresh Strawberries', 3.0, 'kg', CURDATE() + INTERVAL 2 DAY, 4.0, 9.00);

-- 5. Finished Bakery Products
INSERT IGNORE INTO products (id, name, category, selling_price, cost_price, shelf_life_days, description, image_url, active) VALUES
(1, 'Chocolate Fudge Cake', 'CAKE', 45.00, 14.50, 3, 'Rich dark chocolate layer cake with creamy fudge frosting', 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=300', true),
(2, 'Vanilla Dream Cupcake', 'CUPCAKE', 3.50, 0.95, 3, 'Fluffy Madagascar vanilla cupcake with buttercream swirl', 'https://images.unsplash.com/photo-1519869325930-281384150729?w=300', true),
(3, 'French Butter Croissant', 'PASTRY', 3.00, 0.90, 2, 'Golden layered all-butter flaky Parisian croissant', 'https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=300', true),
(4, 'Artisan Sourdough Loaf', 'BREAD', 6.00, 1.40, 4, 'Naturally fermented crusty artisanal sourdough bread', 'https://images.unsplash.com/photo-1509440159596-0249088772ff?w=300', true),
(5, 'Fresh Strawberry Tart', 'PASTRY', 7.50, 2.60, 2, 'Sweet pastry crust filled with crème pâtissière and glazed strawberries', 'https://images.unsplash.com/photo-1464305795204-6f5bbfc7fb81?w=300', true);

-- 6. Bill of Materials / Recipes (Linking Products to Raw Ingredients)
-- Chocolate Fudge Cake (id: 1)
INSERT IGNORE INTO recipe_items (id, product_id, ingredient_id, quantity_required, unit) VALUES
(1, 1, 1, 0.45, 'kg'),     -- 450g Flour
(2, 1, 2, 0.25, 'kg'),     -- 250g Butter
(3, 1, 3, 0.33, 'dozen'),  -- 4 Eggs
(4, 1, 4, 0.30, 'kg'),     -- 300g Sugar
(5, 1, 9, 0.15, 'kg'),     -- 150g Cocoa Powder
(6, 1, 6, 0.05, 'bottle'), -- Vanilla

-- Vanilla Dream Cupcake (id: 2)
(7, 2, 1, 0.06, 'kg'),     -- 60g Flour
(8, 2, 2, 0.04, 'kg'),     -- 40g Butter
(9, 2, 3, 0.08, 'dozen'),  -- 1 Egg
(10, 2, 4, 0.04, 'kg'),    -- 40g Sugar
(11, 2, 5, 0.03, 'litre'), -- Milk

-- French Butter Croissant (id: 3)
(12, 3, 1, 0.12, 'kg'),    -- 120g Flour
(13, 3, 2, 0.08, 'kg'),    -- 80g Butter
(14, 3, 10, 0.01, 'kg'),   -- 10g Yeast
(15, 3, 5, 0.03, 'litre'), -- Milk

-- Artisan Sourdough Loaf (id: 4)
(16, 4, 1, 0.40, 'kg'),    -- 400g Flour
(17, 4, 10, 0.02, 'kg');   -- 20g Yeast

-- 7. Customers
INSERT IGNORE INTO customer (id, name, phone, email, loyalty_points) VALUES
(1, 'Alice Johnson', '111-111', 'alice@mail.com', 120),
(2, 'Bob Smith', '222-222', 'bob@mail.com', 85),
(3, 'Carol White', '333-333', 'carol@mail.com', 210),
(4, 'David Brown', '444-444', 'david@mail.com', 45);

-- 8. Realistic Multi-Channel Orders
INSERT IGNORE INTO bakery_orders (id, customer_id, order_date, status, channel, delivery_address, delivery_deadline, total_amount, payment_status, notes) VALUES
(1, 1, CURDATE() - INTERVAL 3 DAY, 'DELIVERED', 'WALK_IN', '12 Rose Lane, Colombo', CURDATE() - INTERVAL 2 DAY, 45.00, 'PAID', 'Pickup at 3 PM'),
(2, 2, CURDATE() - INTERVAL 2 DAY, 'DELIVERED', 'PHONE', '45 Baker St, Colombo', CURDATE() - INTERVAL 1 DAY, 36.00, 'PAID', 'Call upon arrival'),
(3, 3, CURDATE() - INTERVAL 1 DAY, 'COMPLETED', 'EMAIL', '89 Park Ave, Colombo', CURDATE(), 84.00, 'PAID', 'Box with ribbon'),
(4, 4, CURDATE(), 'IN_PROGRESS', 'WALK_IN', 'Counter Pickup', CURDATE() + INTERVAL 1 DAY, 45.00, 'PARTIAL_DEPOSIT', 'Birthday inscription: Happy Birthday Kevin'),
(5, 1, CURDATE(), 'CONFIRMED', 'PHONE', '12 Rose Lane, Colombo', CURDATE() + INTERVAL 2 DAY, 42.00, 'PAID', 'Please deliver before noon'),
(6, 2, CURDATE(), 'RECEIVED', 'EMAIL', '45 Baker St, Colombo', CURDATE() + INTERVAL 1 DAY, 18.00, 'PENDING', 'Fresh morning bake requested');

-- 9. Order Items (Linked to Products)
INSERT IGNORE INTO order_item (id, order_id, product_id, product_name, quantity, unit_price, subtotal, special_instructions) VALUES
(1, 1, 1, 'Chocolate Fudge Cake', 1, 45.00, 45.00, 'Standard chocolate icing'),
(2, 2, 3, 'French Butter Croissant', 12, 3.00, 36.00, 'Extra golden crust'),
(3, 3, 2, 'Vanilla Dream Cupcake', 24, 3.50, 84.00, 'Pastel pink sprinkles'),
(4, 4, 1, 'Chocolate Fudge Cake', 1, 45.00, 45.00, 'Write: Happy Birthday Kevin'),
(5, 5, 2, 'Vanilla Dream Cupcake', 12, 3.50, 42.00, 'Blue buttercream'),
(6, 6, 3, 'French Butter Croissant', 6, 3.00, 18.00, 'Warm pack');

-- 10. Kitchen Production Tasks (Scheduled & Assigned to Bakers)
INSERT IGNORE INTO production_tasks (id, order_id, order_item_id, product_name, quantity, task_type, status, assigned_staff_id, scheduled_start, scheduled_end, priority, notes) VALUES
(1, 4, 4, 'Chocolate Fudge Cake', 1, 'BAKING', 'COMPLETED', 1, DATE_ADD(CURDATE(), INTERVAL 6 HOUR), DATE_ADD(CURDATE(), INTERVAL 8 HOUR), 1, 'Bake 1x Chocolate Fudge Cake base'),
(2, 4, 4, 'Chocolate Fudge Cake', 1, 'DECORATING', 'IN_PROGRESS', 2, DATE_ADD(CURDATE(), INTERVAL 8 HOUR), DATE_ADD(CURDATE(), INTERVAL 10 HOUR), 2, 'Happy Birthday Kevin inscription in gold'),
(3, 4, 4, 'Chocolate Fudge Cake', 1, 'PACKAGING', 'PENDING', 3, DATE_ADD(CURDATE(), INTERVAL 10 HOUR), DATE_ADD(CURDATE(), INTERVAL 11 HOUR), 3, 'Box in large cake packaging'),
(4, 5, 5, 'Vanilla Dream Cupcake', 12, 'BAKING', 'PENDING', 1, DATE_ADD(CURDATE(), INTERVAL 9 HOUR), DATE_ADD(CURDATE(), INTERVAL 11 HOUR), 1, 'Bake batch of 12 cupcakes');

-- 11. Food Spoilage & Waste Logs
INSERT IGNORE INTO waste_logs (id, ingredient_id, ingredient_name, quantity, unit, reason, waste_date, estimated_cost_loss, logged_by, notes) VALUES
(1, 8, 'Whipping Cream', 1.5, 'litre', 'EXPIRED', CURDATE() - INTERVAL 5 DAY, 9.75, 'John Baker', 'Carton past expiry date in walk-in chiller'),
(2, 11, 'Fresh Strawberries', 0.8, 'kg', 'SPOILED', CURDATE() - INTERVAL 2 DAY, 7.20, 'Mary Decorator', 'Bruised and moldy strawberries discarded'),
(3, 1, 'All-Purpose Flour', 2.0, 'kg', 'BURNT_IN_OVEN', CURDATE() - INTERVAL 1 DAY, 5.00, 'John Baker', 'Oven temperature sensor error burnt croissant batch');

-- 12. Purchase Orders
INSERT IGNORE INTO purchase_order (id, supplier_id, order_date, received_date, status, total_amount) VALUES
(1, 1, CURDATE() - INTERVAL 7 DAY, CURDATE() - INTERVAL 5 DAY, 'RECEIVED', 150.00),
(2, 2, CURDATE() - INTERVAL 2 DAY, NULL, 'SENT', 80.00),
(3, 3, CURDATE(), NULL, 'PENDING', 45.00);

-- 13. Purchase Order Items
INSERT IGNORE INTO purchase_order_item (id, po_id, ingredient_id, ingredient_name, quantity) VALUES
(1, 1, 1, 'All-Purpose Flour', 50.0),
(2, 1, 7, 'Baking Powder', 10.0),
(3, 2, 2, 'Butter', 10.0),
(4, 3, 4, 'Sugar', 25.0);