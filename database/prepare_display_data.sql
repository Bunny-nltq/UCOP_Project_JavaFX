-- ================================================
-- KIỂM TRA VÀ CẬP NHẬT DỮ LIỆU ĐỂ HIỂN THỊ ỨNG DỤNG
-- ================================================

USE ucop_project_javafx;

-- ============================================
-- BƯỚC 1: KIỂM TRA DỮ LIỆU HIỆN TẠI
-- ============================================

SELECT '========== 1. KIỂM TRA BẢNG ITEMS ==========' AS '';

-- Kiểm tra xem có dữ liệu không
SELECT 
    COUNT(*) as 'Tổng items',
    SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) as 'Active',
    SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) as 'Inactive',
    SUM(CASE WHEN image_url IS NOT NULL AND image_url != '' THEN 1 ELSE 0 END) as 'Có ảnh',
    SUM(CASE WHEN stock > 0 THEN 1 ELSE 0 END) as 'Còn hàng'
FROM items;

-- Xem 10 items mẫu
SELECT '========== 2. MẪU 10 ITEMS ĐẦU TIÊN ==========' AS '';
SELECT 
    id,
    LEFT(name, 40) as name,
    price,
    stock,
    status,
    CASE 
        WHEN image_url IS NOT NULL AND image_url != '' THEN '✓ Có'
        ELSE '✗ Không'
    END as 'Hình ảnh'
FROM items
ORDER BY id
LIMIT 10;

-- ============================================
-- BƯỚC 2: THÊM HÌNH ẢNH CHO ITEMS (NẾU CHƯA CÓ)
-- ============================================

-- Kiểm tra items chưa có ảnh
SELECT 
    CONCAT('Có ', COUNT(*), ' items chưa có hình ảnh') as 'Cảnh báo'
FROM items
WHERE image_url IS NULL OR image_url = '';

-- CẬP NHẬT HÌNH ẢNH
-- (Chỉ cập nhật nếu chưa có ảnh)

UPDATE items SET image_url = 'https://images.unsplash.com/photo-1611095790444-1dfa35e37b52?w=400' 
WHERE name LIKE '%Bút%' AND (image_url IS NULL OR image_url = '');

UPDATE items SET image_url = 'https://images.unsplash.com/photo-1586281380349-632531db7ed4?w=400' 
WHERE name LIKE '%Sổ%' AND (image_url IS NULL OR image_url = '');

UPDATE items SET image_url = 'https://images.unsplash.com/photo-1596548438137-d51ea5c83295?w=400' 
WHERE name LIKE '%Vở%' AND (image_url IS NULL OR image_url = '');

UPDATE items SET image_url = 'https://images.unsplash.com/photo-1554475900-4b0c68e7db12?w=400' 
WHERE name LIKE '%Kéo%' AND (image_url IS NULL OR image_url = '');

UPDATE items SET image_url = 'https://images.unsplash.com/photo-1544816155-12df9643f363?w=400' 
WHERE name LIKE '%Thước%' AND (image_url IS NULL OR image_url = '');

UPDATE items SET image_url = 'https://images.unsplash.com/photo-1586075010923-2dd4570fb338?w=400' 
WHERE name LIKE '%Băng keo%' AND (image_url IS NULL OR image_url = '');

UPDATE items SET image_url = 'https://images.unsplash.com/photo-1587825140708-dfaf72ae4b04?w=400' 
WHERE name LIKE '%Máy tính%' AND (image_url IS NULL OR image_url = '');

UPDATE items SET image_url = 'https://images.unsplash.com/photo-1612815154858-60aa4c59eaa6?w=400' 
WHERE name LIKE '%Máy in%' AND (image_url IS NULL OR image_url = '');

UPDATE items SET image_url = 'https://images.unsplash.com/photo-1527814050087-3793815479db?w=400' 
WHERE name LIKE '%Chuột%' AND (image_url IS NULL OR image_url = '');

UPDATE items SET image_url = 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400' 
WHERE name LIKE '%Bàn phím%' AND (image_url IS NULL OR image_url = '');

UPDATE items SET image_url = 'https://images.unsplash.com/photo-1545127398-14699f92334b?w=400' 
WHERE name LIKE '%Tai nghe%' AND (image_url IS NULL OR image_url = '');

-- Placeholder cho items còn lại
UPDATE items 
SET image_url = CONCAT('https://dummyimage.com/400x400/4CAF50/ffffff&text=', REPLACE(SUBSTRING(name, 1, 20), ' ', '+'))
WHERE image_url IS NULL OR image_url = '';

-- ============================================
-- BƯỚC 3: XÁC NHẬN SAU KHI CẬP NHẬT
-- ============================================

SELECT '========== 3. XÁC NHẬN CẬP NHẬT ==========' AS '';

SELECT 
    COUNT(*) as 'Tổng items',
    SUM(CASE WHEN image_url IS NOT NULL AND image_url != '' THEN 1 ELSE 0 END) as 'Có ảnh',
    SUM(CASE WHEN image_url IS NULL OR image_url = '' THEN 1 ELSE 0 END) as 'Chưa có ảnh'
FROM items;

-- ============================================
-- BƯỚC 4: DỮ LIỆU MẪU ĐỂ TEST GIAO DIỆN
-- ============================================

SELECT '========== 4. DỮ LIỆU ĐỂ HIỂN THỊ GIAO DIỆN ==========' AS '';

-- Items có stock > 0 và active
SELECT 
    id,
    LEFT(name, 35) as 'Tên sản phẩm',
    FORMAT(price, 0) as 'Giá',
    stock as 'Tồn kho',
    LEFT(image_url, 50) as 'URL (50 ký tự đầu)'
FROM items
WHERE status = 1 AND stock > 0
ORDER BY id
LIMIT 20;

-- ============================================
-- BƯỚC 5: KIỂM TRA CATEGORIES
-- ============================================

SELECT '========== 5. KIỂM TRA CATEGORIES ==========' AS '';

SELECT 
    c.id,
    c.name,
    COUNT(i.id) as 'Số items'
FROM categories c
LEFT JOIN items i ON i.category_id = c.id
GROUP BY c.id, c.name
ORDER BY c.name;

-- ============================================
-- KẾT QUẢ
-- ============================================

SELECT '========== ✅ HOÀN TẤT ==========' AS '';
SELECT 'Dữ liệu đã sẵn sàng để hiển thị trong ứng dụng!' AS 'Status';
SELECT 'Chạy ứng dụng: mvn clean javafx:run' AS 'Next Step';
