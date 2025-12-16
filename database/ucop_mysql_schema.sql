-- UCOP Order & Payment Core Database Schema (MySQL)
-- Universal Commerce & Operations Platform

CREATE DATABASE IF NOT EXISTS ucop_project_javafx CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ucop_project_javafx;

-- ============================================
-- 1. WAREHOUSE & STOCK MANAGEMENT
-- ============================================

CREATE TABLE warehouses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(20),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE stock_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    warehouse_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    on_hand BIGINT NOT NULL DEFAULT 0,
    reserved BIGINT NOT NULL DEFAULT 0,
    low_stock_threshold BIGINT NOT NULL DEFAULT 10,
    is_low_stock BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    UNIQUE KEY unique_warehouse_item (warehouse_id, item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_stock_warehouse ON stock_items(warehouse_id);
CREATE INDEX idx_stock_item ON stock_items(item_id);
CREATE INDEX idx_stock_low ON stock_items(is_low_stock);

-- ============================================
-- 2. CUSTOMERS & PRODUCTS
-- ============================================

CREATE TABLE customers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(255),
    city VARCHAR(50),
    postal_code VARCHAR(20),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    price DECIMAL(19,4) NOT NULL,
    image_url VARCHAR(500),
    stock_quantity BIGINT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_customer_account ON customers(account_id);
CREATE INDEX idx_product_category ON products(category);
CREATE INDEX idx_product_active ON products(is_active);
CREATE INDEX idx_product_name ON products(name);

-- ============================================
-- 3. CART & CART ITEMS
-- ============================================

CREATE TABLE carts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE cart_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cart_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    quantity BIGINT NOT NULL,
    unit_price DECIMAL(19,4) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_cart_account ON carts(account_id);
CREATE INDEX idx_cart_items ON cart_items(cart_id);

-- ============================================
-- 3. ORDERS & ORDER ITEMS
-- ============================================

CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'CART',
    order_number VARCHAR(50) UNIQUE,
    shipping_name VARCHAR(100),
    shipping_phone VARCHAR(20),
    shipping_address VARCHAR(255),
    shipping_city VARCHAR(50),
    shipping_postal_code VARCHAR(20),
    subtotal DECIMAL(19,4) NOT NULL DEFAULT 0,
    item_discount DECIMAL(19,4) NOT NULL DEFAULT 0,
    cart_discount DECIMAL(19,4) NOT NULL DEFAULT 0,
    tax_amount DECIMAL(19,4) NOT NULL DEFAULT 0,
    shipping_fee DECIMAL(19,4) NOT NULL DEFAULT 0,
    cod_fee DECIMAL(19,4) NOT NULL DEFAULT 0,
    gateway_fee DECIMAL(19,4) NOT NULL DEFAULT 0,
    grand_total DECIMAL(19,4) NOT NULL DEFAULT 0,
    amount_due DECIMAL(19,4) NOT NULL DEFAULT 0,
    promotion_code VARCHAR(50),
    notes VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    placed_at DATETIME,
    paid_at DATETIME,
    shipped_at DATETIME,
    delivered_at DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    quantity BIGINT NOT NULL,
    unit_price DECIMAL(19,4) NOT NULL,
    item_discount DECIMAL(19,4) DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_order_account ON orders(account_id);
CREATE INDEX idx_order_status ON orders(status);
CREATE INDEX idx_order_number ON orders(order_number);
CREATE INDEX idx_order_items ON order_items(order_id);

-- ============================================
-- 4. PAYMENT & REFUND
-- ============================================

CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    transaction_id VARCHAR(100),
    reference_number VARCHAR(100),
    notes VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    paid_at DATETIME,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE refunds (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_id BIGINT NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    refund_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    reason VARCHAR(500),
    refund_transaction_id VARCHAR(100),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    refunded_at DATETIME,
    FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_payment_order ON payments(order_id);
CREATE INDEX idx_payment_status ON payments(status);
CREATE INDEX idx_payment_method ON payments(payment_method);
CREATE INDEX idx_refund_payment ON refunds(payment_id);
CREATE INDEX idx_refund_status ON refunds(status);

-- ============================================
-- 5. SHIPMENT & APPOINTMENT
-- ============================================

CREATE TABLE shipments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    tracking_number VARCHAR(100) UNIQUE,
    carrier VARCHAR(100),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    expected_delivery_date DATETIME,
    actual_delivery_date DATETIME,
    warehouse_id BIGINT,
    notes VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE appointments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    appointment_number VARCHAR(100) UNIQUE,
    scheduled_time DATETIME NOT NULL,
    actual_time DATETIME,
    location VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    service_provider VARCHAR(100),
    notes VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_shipment_order ON shipments(order_id);
CREATE INDEX idx_shipment_status ON shipments(status);
CREATE INDEX idx_shipment_tracking ON shipments(tracking_number);
CREATE INDEX idx_appointment_order ON appointments(order_id);
CREATE INDEX idx_appointment_status ON appointments(status);
CREATE INDEX idx_appointment_scheduled ON appointments(scheduled_time);

-- ============================================
-- 6. PROMOTIONS & PROMOTION USAGE
-- ============================================

CREATE TABLE promotions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    discount_type VARCHAR(20) NOT NULL, -- PERCENTAGE, FIXED_AMOUNT, ITEM, CART
    discount_value DECIMAL(19,4) NOT NULL,
    min_order_amount DECIMAL(19,4) DEFAULT 0,
    max_discount_amount DECIMAL(19,4),
    applicable_to VARCHAR(20) NOT NULL DEFAULT 'ALL', -- ALL, SPECIFIC_ITEMS, SPECIFIC_CATEGORIES
    applicable_item_ids TEXT, -- Comma-separated item IDs
    max_usage_total INT,
    max_usage_per_user INT DEFAULT 1,
    usage_count INT DEFAULT 0,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_stackable BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE promotion_usages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    promotion_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    discount_amount DECIMAL(19,4) NOT NULL,
    used_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (promotion_id) REFERENCES promotions(id),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_promotion_code ON promotions(code);
CREATE INDEX idx_promotion_active ON promotions(is_active);
CREATE INDEX idx_promotion_dates ON promotions(start_date, end_date);
CREATE INDEX idx_promotion_usage_promo ON promotion_usages(promotion_id);
CREATE INDEX idx_promotion_usage_order ON promotion_usages(order_id);
CREATE INDEX idx_promotion_usage_account ON promotion_usages(account_id);

-- ============================================
-- SAMPLE DATA
-- ============================================

-- Insert sample warehouses
INSERT INTO warehouses (name, address, phone, is_active)
VALUES 
    ('Kho Miền Bắc', '123 Đường Kim Mã, Hà Nội', '024 1234 5678', TRUE),
    ('Kho Miền Nam', '456 Đường Nguyễn Huệ, TP.HCM', '028 7654 3210', TRUE),
    ('Kho Miền Trung', '789 Đường Hàng Vôi, Đà Nẵng', '0236 7894 5612', TRUE);

-- Insert sample stock items (assuming items with ID 1-5 exist in catalog)
INSERT INTO stock_items (warehouse_id, item_id, on_hand, reserved, low_stock_threshold, is_low_stock)
VALUES 
    (1, 1, 100, 0, 10, FALSE),
    (1, 2, 50, 0, 10, FALSE),
    (1, 3, 200, 0, 20, FALSE),
    (2, 1, 150, 0, 10, FALSE),
    (2, 4, 30, 0, 10, FALSE),
    (3, 2, 80, 0, 10, FALSE);

-- Insert sample promotions
INSERT INTO promotions (code, name, description, discount_type, discount_value, min_order_amount, max_discount_amount, start_date, end_date, is_active, max_usage_total, max_usage_per_user)
VALUES 
    ('WELCOME10', 'Giảm 10% cho đơn hàng đầu tiên', 'Mã giảm giá 10% cho khách hàng mới', 'PERCENTAGE', 10.00, 100000, 50000, NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), TRUE, 1000, 1),
    ('SALE50K', 'Giảm 50,000đ', 'Giảm giá cố định 50,000đ cho đơn từ 500,000đ', 'FIXED_AMOUNT', 50000.00, 500000, NULL, NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH), TRUE, 500, 2),
    ('FREESHIP', 'Miễn phí vận chuyển', 'Miễn phí ship cho đơn từ 300,000đ', 'FIXED_AMOUNT', 30000.00, 300000, 30000, NOW(), DATE_ADD(NOW(), INTERVAL 2 MONTH), TRUE, NULL, 5),
    ('VIP20', 'Giảm 20% VIP', 'Mã giảm 20% cho thành viên VIP', 'PERCENTAGE', 20.00, 1000000, 200000, NOW(), DATE_ADD(NOW(), INTERVAL 6 MONTH), TRUE, 100, 10);
