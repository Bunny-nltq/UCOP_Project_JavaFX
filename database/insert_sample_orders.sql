-- Script to check and insert sample orders for testing
-- Database: ucop_project_javafx

USE ucop_project_javafx;

-- Check current orders
SELECT 'Current Orders:' as Info;
SELECT o.id, o.order_number, o.account_id, o.status, o.grand_total, o.placed_at, o.created_at
FROM orders o
ORDER BY o.id DESC
LIMIT 10;

-- Check if account_id = 1 exists (default test account)
SELECT 'Checking Account ID 1:' as Info;
SELECT * FROM orders WHERE account_id = 1;

-- Insert sample orders if none exist for account_id = 1
-- Only insert if no orders exist yet
INSERT INTO orders (
    account_id, 
    status, 
    order_number, 
    shipping_name, 
    shipping_phone, 
    shipping_address,
    shipping_city,
    subtotal,
    grand_total,
    amount_due,
    placed_at,
    created_at
)
SELECT 
    1 as account_id,
    'PENDING_PAYMENT' as status,
    CONCAT('ORD-', DATE_FORMAT(NOW(), '%Y%m%d'), '-001') as order_number,
    'Nguyễn Văn A' as shipping_name,
    '0123456789' as shipping_phone,
    '123 Đường ABC, Quận 1' as shipping_address,
    'TP.HCM' as shipping_city,
    500000 as subtotal,
    500000 as grand_total,
    500000 as amount_due,
    NOW() as placed_at,
    NOW() as created_at
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE account_id = 1)
LIMIT 1;

-- Insert another sample order
INSERT INTO orders (
    account_id, 
    status, 
    order_number, 
    shipping_name, 
    shipping_phone, 
    shipping_address,
    shipping_city,
    subtotal,
    grand_total,
    amount_due,
    placed_at,
    created_at
)
SELECT 
    1 as account_id,
    'PAID' as status,
    CONCAT('ORD-', DATE_FORMAT(NOW(), '%Y%m%d'), '-002') as order_number,
    'Nguyễn Văn A' as shipping_name,
    '0123456789' as shipping_phone,
    '123 Đường ABC, Quận 1' as shipping_address,
    'TP.HCM' as shipping_city,
    750000 as subtotal,
    750000 as grand_total,
    0 as amount_due,
    NOW() - INTERVAL 1 DAY as placed_at,
    NOW() - INTERVAL 1 DAY as created_at
WHERE (SELECT COUNT(*) FROM orders WHERE account_id = 1) < 2
LIMIT 1;

-- Insert third sample order
INSERT INTO orders (
    account_id, 
    status, 
    order_number, 
    shipping_name, 
    shipping_phone, 
    shipping_address,
    shipping_city,
    subtotal,
    grand_total,
    amount_due,
    placed_at,
    created_at
)
SELECT 
    1 as account_id,
    'DELIVERED' as status,
    CONCAT('ORD-', DATE_FORMAT(NOW(), '%Y%m%d'), '-003') as order_number,
    'Nguyễn Văn A' as shipping_name,
    '0123456789' as shipping_phone,
    '123 Đường ABC, Quận 1' as shipping_address,
    'TP.HCM' as shipping_city,
    1200000 as subtotal,
    1200000 as grand_total,
    0 as amount_due,
    NOW() - INTERVAL 3 DAY as placed_at,
    NOW() - INTERVAL 3 DAY as created_at
WHERE (SELECT COUNT(*) FROM orders WHERE account_id = 1) < 3
LIMIT 1;

-- Verify inserted orders
SELECT 'After Insert - Orders for Account 1:' as Info;
SELECT o.id, o.order_number, o.account_id, o.status, o.grand_total, o.placed_at
FROM orders o
WHERE o.account_id = 1
ORDER BY o.placed_at DESC;

-- Show total count
SELECT 'Total Orders Count:' as Info;
SELECT COUNT(*) as total_orders FROM orders WHERE account_id = 1;
