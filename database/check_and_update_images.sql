-- Script kiểm tra và cập nhật hình ảnh sản phẩm
-- Chạy script này trong MySQL Workbench hoặc command line

USE ucop_project_javafx;

-- ============================================
-- 1. KIỂM TRA TRẠNG THÁI HIỆN TẠI
-- ============================================
SELECT '=== THỐNG KÊ HÌNH ẢNH SẢN PHẨM ===' AS '';

SELECT 
    COUNT(*) as 'Tổng sản phẩm',
    SUM(CASE WHEN image_url IS NOT NULL AND image_url != '' THEN 1 ELSE 0 END) as 'Có hình ảnh',
    SUM(CASE WHEN image_url IS NULL OR image_url = '' THEN 1 ELSE 0 END) as 'Chưa có hình ảnh'
FROM items;

-- ============================================
-- 2. HIỂN THỊ MẪU SẢN PHẨM
-- ============================================
SELECT '=== MẪU 10 SẢN PHẨM ĐẦU TIÊN ===' AS '';

SELECT 
    id,
    name,
    CASE 
        WHEN image_url IS NULL OR image_url = '' THEN '[Chưa có ảnh]'
        WHEN LENGTH(image_url) > 50 THEN CONCAT(LEFT(image_url, 47), '...')
        ELSE image_url
    END as image_url
FROM items 
ORDER BY id 
LIMIT 10;

-- ============================================
-- 3. CẬP NHẬT HÌNH ẢNH (NẾU CHƯA CÓ)
-- ============================================
-- Uncomment (bỏ dấu --) các dòng dưới để cập nhật hình ảnh

-- Văn phòng phẩm
-- UPDATE items SET image_url = 'https://images.unsplash.com/photo-1611095790444-1dfa35e37b52?w=400' WHERE name LIKE '%Bút%' AND (image_url IS NULL OR image_url = '');
-- UPDATE items SET image_url = 'https://images.unsplash.com/photo-1554475900-4b0c68e7db12?w=400' WHERE name LIKE '%Kéo%' AND (image_url IS NULL OR image_url = '');
-- UPDATE items SET image_url = 'https://images.unsplash.com/photo-1544816155-12df9643f363?w=400' WHERE name LIKE '%Thước%' AND (image_url IS NULL OR image_url = '');
-- UPDATE items SET image_url = 'https://images.unsplash.com/photo-1596548438137-d51ea5c83295?w=400' WHERE name LIKE '%Sổ%' AND (image_url IS NULL OR image_url = '');
-- UPDATE items SET image_url = 'https://images.unsplash.com/photo-1586281380349-632531db7ed4?w=400' WHERE name LIKE '%Vở%' AND (image_url IS NULL OR image_url = '');
-- UPDATE items SET image_url = 'https://images.unsplash.com/photo-1586075010923-2dd4570fb338?w=400' WHERE name LIKE '%Băng Keo%' AND (image_url IS NULL OR image_url = '');

-- Điện tử
-- UPDATE items SET image_url = 'https://images.unsplash.com/photo-1587825140708-dfaf72ae4b04?w=400' WHERE name LIKE '%Máy Tính%' AND (image_url IS NULL OR image_url = '');
-- UPDATE items SET image_url = 'https://images.unsplash.com/photo-1612815154858-60aa4c59eaa6?w=400' WHERE name LIKE '%Máy In%' AND (image_url IS NULL OR image_url = '');
-- UPDATE items SET image_url = 'https://images.unsplash.com/photo-1527814050087-3793815479db?w=400' WHERE name LIKE '%Chuột%' AND (image_url IS NULL OR image_url = '');
-- UPDATE items SET image_url = 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400' WHERE name LIKE '%Bàn Phím%' AND (image_url IS NULL OR image_url = '');
-- UPDATE items SET image_url = 'https://images.unsplash.com/photo-1545127398-14699f92334b?w=400' WHERE name LIKE '%Tai Nghe%' AND (image_url IS NULL OR image_url = '');

-- Placeholder cho tất cả sản phẩm chưa có ảnh (uncomment để chạy)
-- UPDATE items 
-- SET image_url = CONCAT('https://dummyimage.com/400x400/4CAF50/ffffff&text=', REPLACE(SUBSTRING(name, 1, 20), ' ', '+'))
-- WHERE image_url IS NULL OR image_url = '';

-- ============================================
-- 4. XÁC NHẬN SAU KHI CẬP NHẬT
-- ============================================
SELECT '=== XÁC NHẬN SAU CẬP NHẬT ===' AS '';

SELECT 
    COUNT(*) as 'Tổng sản phẩm',
    SUM(CASE WHEN image_url IS NOT NULL AND image_url != '' THEN 1 ELSE 0 END) as 'Có hình ảnh',
    SUM(CASE WHEN image_url IS NULL OR image_url = '' THEN 1 ELSE 0 END) as 'Chưa có hình ảnh'
FROM items;
