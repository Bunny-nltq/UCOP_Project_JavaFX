-- UCOP Order & Payment Core Database Schema (MySQL)
-- Universal Commerce & Operations Platform
-- Complete with Roles, Users, Categories, Items, and Sample Data
-- Last Updated: 2025-12-16
-- All duplicate definitions and syntax errors removed

-- Kill existing connections (MySQL >= 5.7.7)
SELECT CONCAT('KILL ', id, ';') FROM INFORMATION_SCHEMA.PROCESSLIST WHERE id <> CONNECTION_ID() AND db = 'ucop_project_javafx' INTO OUTFILE '/tmp/kill_connections.sql';

DROP DATABASE IF EXISTS ucop_project_javafx;
CREATE DATABASE ucop_project_javafx CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ucop_project_javafx;

-- ============================================
-- 1. ROLES
-- ============================================
CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_role_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO roles VALUES 
(1, 'ADMIN', 'Administrator - Full Access', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'STAFF', 'Staff - Order Processing & Inventory', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'CUSTOMER', 'Customer - Shopping & Orders', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================
-- 2. USERS & ACCOUNT PROFILES
-- ============================================
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    active TINYINT(1) DEFAULT 1,
    locked TINYINT(1) DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_username (username),
    KEY idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO users VALUES
(1, 'admin', '0192023a7bbd73250516f069df18b500', 'admin@ucop.com', 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'staff1', '0733b8d9a847bf56f8f308a3e15a3e4d', 'staff1@ucop.com', 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'staff2', '0733b8d9a847bf56f8f308a3e15a3e4d', 'staff2@ucop.com', 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'customer1', '38a7cd1c74e8bde1a0baf04c3ece3f61', 'customer1@ucop.com', 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'customer2', '38a7cd1c74e8bde1a0baf04c3ece3f61', 'customer2@ucop.com', 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'customer3', '38a7cd1c74e8bde1a0baf04c3ece3f61', 'customer3@ucop.com', 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'customer4', '38a7cd1c74e8bde1a0baf04c3ece3f61', 'customer4@ucop.com', 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'customer5', '38a7cd1c74e8bde1a0baf04c3ece3f61', 'customer5@ucop.com', 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

CREATE TABLE account_profiles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(255),
    city VARCHAR(50),
    postal_code VARCHAR(20),
    date_of_birth DATE,
    gender VARCHAR(20),
    avatar_url VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO account_profiles VALUES
(1, 1, 'Nguyễn Văn Admin', '0901000000', '1 Lê Lợi, Hà Nội', 'Hà Nội', '100000', '1990-01-01', 'Male', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 'Trần Thị Staff 1', '0901111111', '2 Nguyễn Huệ, Hà Nội', 'Hà Nội', '100000', '1995-05-15', 'Female', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 3, 'Lê Văn Staff 2', '0901222222', '3 Hoàn Kiếm, Hà Nội', 'Hà Nội', '100000', '1994-03-20', 'Male', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 4, 'Phạm Thị Customer 1', '0901333333', '123 Đường ABC', 'TP.HCM', '700000', '2000-07-10', 'Female', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 5, 'Hoàng Văn Customer 2', '0901444444', '456 Đường XYZ', 'Hà Nội', '100000', '1998-12-25', 'Male', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 6, 'Ngô Thị Customer 3', '0901555555', '789 Đường MNO', 'Đà Nẵng', '500000', '1999-09-08', 'Female', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 7, 'Dương Văn Customer 4', '0901666666', '321 Đường PQR', 'Hải Phòng', '180000', '2001-02-14', 'Male', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 8, 'Tân Nữ Customer 5', '0901777777', '654 Đường STU', 'Huế', '530000', '1997-11-30', 'Female', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================
-- 3. USER ROLES (JOIN TABLE)
-- ============================================
CREATE TABLE user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    KEY idx_role_id (role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO user_roles VALUES
(1, 1), (2, 2), (3, 2), (4, 3), (5, 3), (6, 3), (7, 3), (8, 3);

-- ============================================
-- 4. CATEGORIES (Hierarchical)
-- ============================================
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    parent_id BIGINT,
    image_url VARCHAR(500),
    is_active TINYINT(1) DEFAULT 1,
    display_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    KEY idx_category_parent (parent_id),
    KEY idx_category_active (is_active),
    FOREIGN KEY (parent_id) REFERENCES categories (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO categories VALUES
(1, 'Văn Phòng Phẩm', 'Các sản phẩm dùng trong văn phòng', NULL, NULL, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(2, 'Bút Viết', 'Bút viết các loại', 1, NULL, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(3, 'Vở và Sổ', 'Vở, sổ ghi chép', 1, NULL, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(4, 'Dụng cụ Học tập', 'Thước kẻ, gôm, compa...', 1, NULL, 1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin');

-- ============================================
-- 5. ITEMS (Products/Services)
-- ============================================
CREATE TABLE items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sku VARCHAR(50) UNIQUE,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    category_id BIGINT,
    unit VARCHAR(20),
    price DECIMAL(19,4) NOT NULL,
    weight DECIMAL(10,2),
    image_url VARCHAR(500),
    is_active TINYINT(1) DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    KEY idx_item_category (category_id),
    KEY idx_item_active (is_active),
    KEY idx_item_name (name),
    FOREIGN KEY (category_id) REFERENCES categories (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO items VALUES
(1, 'BUT-BI-001', 'Bút Bi Thiên Long TL-079', 'Bút bi cao cấp, mực xanh, viết mượt', 2, 'cái', 3000.0000, 0.05, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQW3HeWCbo0JsPJdJ3vjJH1WJjB9tC2PbZoXQ&s', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(2, 'BUT-GEL-001', 'Bút Gel Thiên Long GEL-08', 'Bút gel nước, nhiều màu, viết êm', 2, 'cái', 5000.0000, 0.06, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSNwAN-9-YaAG_G8HdHmwAOycQboj-zebCnow&s', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(3, 'BUT-LONG-001', 'Bút Lông Dầu Thiên Long PM-08', 'Bút lông dầu không lem, màu đen', 2, 'cái', 15000.0000, 0.10, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRNCD6R6MyxAMe_9LSnu01Hva1VrRkSXM2RIQ&s', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(4, 'BUT-CHI-001', 'Bút Chì 2B Thiên Long PC-052', 'Bút chì gỗ, ruột chì 2B, viết đậm', 2, 'cái', 4000.0000, 0.08, 'https://product.hstatic.net/1000230347/product/but_chi_bam_thien_long_pc-022_259ef67ff26b442fb163d06997f45f5f_1024x1024.jpg', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(5, 'BUT-DA-QUANG-001', 'Bút Dạ Quang 6 Màu', 'Bộ bút đánh dấu dạ quang 6 màu rực rỡ', 2, 'bộ', 45000.0000, 0.20, 'https://hachihachi.com.vn/Uploads/_6/productimage/4941829006568-(1).jpg', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(6, 'VO-OLY-001', 'Vở Ô Ly Campus 200 Trang', 'Vở ô ly khổ A4, giấy trắng, bìa cứng', 3, 'quyển', 25000.0000, 0.30, 'https://img.lazcdn.com/g/p/678d7fdd129a39605ec43eafe4401abc.jpg_720x720q80.jpg', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(7, 'SO-TAY-001', 'Sổ Tay Bìa Da A5', 'Sổ tay cao cấp bìa da, 100 trang', 3, 'quyển', 85000.0000, 0.20, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQV0xj8wCt9kiqah1icNyEJ7O4s7GKAMPQ07A&s', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(8, 'VO-KE-NGANG-001', 'Vở Kẻ Ngang 96 Trang', 'Vở kẻ ngang tiêu chuẩn học sinh', 3, 'quyển', 12000.0000, 0.15, 'https://bizweb.dktcdn.net/100/220/344/products/3-009440fd-8f97-4ed2-b13a-da3cf0c3b3e8.jpg?v=1717672137120', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(9, 'THUOC-KE-001', 'Thước Kẻ Nhựa 30cm', 'Thước nhựa trong suốt, có chia vạch mm', 4, 'cái', 8000.0000, 0.05, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTjTFdph1c3AaxpH3fd_iLsT8l7tbfki83wIw&s', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(10, 'GOM-TAY-001', 'Gôm Tẩy Thiên Long E-037', 'Gôm tẩy trắng, không bẩn vải', 4, 'cái', 3000.0000, 0.02, 'https://bizweb.dktcdn.net/thumb/1024x1024/100/379/648/products/compressed-20240731-092356.jpg?v=1722422869147', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(11, 'COMPA-001', 'Bộ Compa Học Sinh', 'Bộ compa toán học, hộp nhựa', 4, 'bộ', 35000.0000, 0.15, 'https://butmaisenviet.vn/wp-content/uploads/2025/07/2-7.webp', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(12, 'MAY-TINH-001', 'Máy Tính Casio FX-580VN X', 'Máy tính khoa học, 552 chức năng', 4, 'cái', 450000.0000, 0.20, 'https://bizweb.dktcdn.net/thumb/1024x1024/100/467/726/products/b231353f30f31863b7dea65440176af8.jpg?v=1744883893557', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(13, 'KEO-001', 'Kéo Văn Phòng Inox 21cm', 'Kéo cắt giấy inox sắc bén', 4, 'cái', 35000.0000, 0.10, 'https://vn-test-11.slatic.net/shop/70d09edbded418528832310f0541d754.jpeg', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(14, 'HO-DAN-001', 'Hộ Dán UHU 40g', 'Hộ dán đa năng, không độc hại', 4, 'hộp', 25000.0000, 0.08, 'https://down-vn.img.susercontent.com/file/vn-11134207-7r98o-lpuyti25a4hjdd', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(15, 'BANG-KEO-001', 'Băng Keo Trong 2.4cm x 50y', 'Băng keo trong suốt, dính tốt', 4, 'cuộn', 18000.0000, 0.12, 'https://bangkeohuynhgia.com/upload/product/791419_z2430443039873_bb6ce00ec7db40ac3288c2a22961fc06.jpg_570x510.jpg', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin');

-- ============================================
-- 6. WAREHOUSE & STOCK MANAGEMENT
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
