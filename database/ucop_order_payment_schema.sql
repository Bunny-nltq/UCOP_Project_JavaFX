-- UCOP Order & Payment Core Database Schema (SQL Server)
-- Universal Commerce & Operations Platform

USE [UCOP];

-- ============================================
-- 1. WAREHOUSE & STOCK MANAGEMENT
-- ============================================

CREATE TABLE warehouses (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100) NOT NULL,
    address NVARCHAR(255),
    phone NVARCHAR(20),
    is_active BIT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME NOT NULL DEFAULT GETDATE(),
    created_by NVARCHAR(100),
    updated_by NVARCHAR(100)
);

CREATE TABLE stock_items (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    warehouse_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    on_hand BIGINT NOT NULL DEFAULT 0,
    reserved BIGINT NOT NULL DEFAULT 0,
    low_stock_threshold BIGINT NOT NULL DEFAULT 10,
    is_low_stock BIT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME NOT NULL DEFAULT GETDATE(),
    created_by NVARCHAR(100),
    updated_by NVARCHAR(100),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    UNIQUE (warehouse_id, item_id)
);

CREATE INDEX idx_stock_warehouse ON stock_items(warehouse_id);
CREATE INDEX idx_stock_item ON stock_items(item_id);
CREATE INDEX idx_stock_low ON stock_items(is_low_stock);

-- ============================================
-- 2. CART & CART ITEMS
-- ============================================

CREATE TABLE carts (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    account_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME NOT NULL DEFAULT GETDATE()
);

CREATE TABLE cart_items (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    cart_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    quantity BIGINT NOT NULL,
    unit_price DECIMAL(19,4) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME NOT NULL DEFAULT GETDATE(),
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE
);

CREATE INDEX idx_cart_account ON carts(account_id);
CREATE INDEX idx_cart_items ON cart_items(cart_id);

-- ============================================
-- 3. ORDERS & ORDER ITEMS
-- ============================================

CREATE TABLE orders (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    account_id BIGINT NOT NULL,
    status NVARCHAR(50) NOT NULL DEFAULT 'CART',
    order_number NVARCHAR(50) UNIQUE,
    shipping_name NVARCHAR(100),
    shipping_phone NVARCHAR(20),
    shipping_address NVARCHAR(255),
    shipping_city NVARCHAR(50),
    shipping_postal_code NVARCHAR(20),
    subtotal DECIMAL(19,4) NOT NULL DEFAULT 0,
    item_discount DECIMAL(19,4) NOT NULL DEFAULT 0,
    cart_discount DECIMAL(19,4) NOT NULL DEFAULT 0,
    tax_amount DECIMAL(19,4) NOT NULL DEFAULT 0,
    shipping_fee DECIMAL(19,4) NOT NULL DEFAULT 0,
    cod_fee DECIMAL(19,4) NOT NULL DEFAULT 0,
    gateway_fee DECIMAL(19,4) NOT NULL DEFAULT 0,
    grand_total DECIMAL(19,4) NOT NULL DEFAULT 0,
    amount_due DECIMAL(19,4) NOT NULL DEFAULT 0,
    promotion_code NVARCHAR(50),
    notes NVARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME NOT NULL DEFAULT GETDATE(),
    created_by NVARCHAR(100),
    updated_by NVARCHAR(100),
    placed_at DATETIME,
    paid_at DATETIME,
    shipped_at DATETIME,
    delivered_at DATETIME
);

CREATE TABLE order_items (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    order_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    quantity BIGINT NOT NULL,
    unit_price DECIMAL(19,4) NOT NULL,
    item_discount DECIMAL(19,4) DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME NOT NULL DEFAULT GETDATE(),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE INDEX idx_order_account ON orders(account_id);
CREATE INDEX idx_order_status ON orders(status);
CREATE INDEX idx_order_number ON orders(order_number);
CREATE INDEX idx_order_items ON order_items(order_id);

-- ============================================
-- 4. PAYMENT & REFUND
-- ============================================

CREATE TABLE payments (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    order_id BIGINT NOT NULL,
    payment_method NVARCHAR(50) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    status NVARCHAR(50) NOT NULL DEFAULT 'PENDING',
    transaction_id NVARCHAR(100),
    reference_number NVARCHAR(100),
    notes NVARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME NOT NULL DEFAULT GETDATE(),
    created_by NVARCHAR(100),
    updated_by NVARCHAR(100),
    paid_at DATETIME,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE TABLE refunds (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    payment_id BIGINT NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    refund_type NVARCHAR(50) NOT NULL,
    status NVARCHAR(50) NOT NULL DEFAULT 'PENDING',
    reason NVARCHAR(500),
    refund_transaction_id NVARCHAR(100),
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME NOT NULL DEFAULT GETDATE(),
    created_by NVARCHAR(100),
    updated_by NVARCHAR(100),
    refunded_at DATETIME,
    FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE CASCADE
);

CREATE INDEX idx_payment_order ON payments(order_id);
CREATE INDEX idx_payment_status ON payments(status);
CREATE INDEX idx_payment_method ON payments(payment_method);
CREATE INDEX idx_refund_payment ON refunds(payment_id);
CREATE INDEX idx_refund_status ON refunds(status);

-- ============================================
-- 5. SHIPMENT & APPOINTMENT
-- ============================================

CREATE TABLE shipments (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    order_id BIGINT NOT NULL,
    tracking_number NVARCHAR(100) UNIQUE,
    carrier NVARCHAR(100),
    status NVARCHAR(50) NOT NULL DEFAULT 'PENDING',
    expected_delivery_date DATETIME,
    actual_delivery_date DATETIME,
    warehouse_id BIGINT,
    notes NVARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME NOT NULL DEFAULT GETDATE(),
    created_by NVARCHAR(100),
    updated_by NVARCHAR(100),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id)
);

CREATE TABLE appointments (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    order_id BIGINT NOT NULL,
    appointment_number NVARCHAR(100) UNIQUE,
    scheduled_time DATETIME NOT NULL,
    actual_time DATETIME,
    location NVARCHAR(255),
    status NVARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    service_provider NVARCHAR(100),
    notes NVARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME NOT NULL DEFAULT GETDATE(),
    created_by NVARCHAR(100),
    updated_by NVARCHAR(100),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE INDEX idx_shipment_order ON shipments(order_id);
CREATE INDEX idx_shipment_status ON shipments(status);
CREATE INDEX idx_shipment_tracking ON shipments(tracking_number);
CREATE INDEX idx_appointment_order ON appointments(order_id);
CREATE INDEX idx_appointment_status ON appointments(status);
CREATE INDEX idx_appointment_scheduled ON appointments(scheduled_time);

-- ============================================
-- SAMPLE DATA (Optional)
-- ============================================

-- Insert sample warehouses
INSERT INTO warehouses (name, address, phone, is_active)
VALUES 
    (N'Kho Miền Bắc', N'123 Đường Kim Mã, Hà Nội', '024 1234 5678', 1),
    (N'Kho Miền Nam', N'456 Đường Nguyễn Huệ, TP.HCM', '028 7654 3210', 1),
    (N'Kho Miền Trung', N'789 Đường Hàng Vôi, Đà Nẵng', '0236 7894 5612', 1);

-- Insert sample stock items (assuming items with ID 1-5 exist in catalog)
INSERT INTO stock_items (warehouse_id, item_id, on_hand, reserved, low_stock_threshold, is_low_stock)
VALUES 
    (1, 1, 100, 0, 10, 0),
    (1, 2, 50, 0, 10, 0),
    (1, 3, 200, 0, 20, 0),
    (2, 1, 150, 0, 10, 0),
    (2, 4, 30, 0, 10, 0),
    (3, 2, 80, 0, 10, 0);
