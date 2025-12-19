-- CẬP NHẬT HÌNH ẢNH CHO TẤT CẢ SẢN PHẨM
-- Chạy script này để thêm hình ảnh vào database

USE ucop_project_javafx;

-- ============================================
-- CẬP NHẬT HÌNH ẢNH THEO LOẠI SẢN PHẨM
-- ============================================

-- 📝 VĂN PHÒNG PHẨM
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1611095790444-1dfa35e37b52?w=400' WHERE name LIKE '%Bút%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1586281380349-632531db7ed4?w=400' WHERE name LIKE '%Sổ%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1596548438137-d51ea5c83295?w=400' WHERE name LIKE '%Vở%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1554475900-4b0c68e7db12?w=400' WHERE name LIKE '%Kéo%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1544816155-12df9643f363?w=400' WHERE name LIKE '%Thước%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1586075010923-2dd4570fb338?w=400' WHERE name LIKE '%Băng keo%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1588075592446-265fd1e6e76f?w=400' WHERE name LIKE '%Ghim%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1586281380349-632531db7ed4?w=400' WHERE name LIKE '%Bìa%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1596548438137-d51ea5c83295?w=400' WHERE name LIKE '%Hồ%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1455390582262-044cdead277a?w=400' WHERE name LIKE '%Giấy%';

-- 💻 ĐIỆN TỬ
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1587825140708-dfaf72ae4b04?w=400' WHERE name LIKE '%Máy tính%' OR name LIKE '%Laptop%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1612815154858-60aa4c59eaa6?w=400' WHERE name LIKE '%Máy in%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1527814050087-3793815479db?w=400' WHERE name LIKE '%Chuột%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400' WHERE name LIKE '%Bàn phím%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1545127398-14699f92334b?w=400' WHERE name LIKE '%Tai nghe%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1593640495253-23196b27a87f?w=400' WHERE name LIKE '%Loa%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1585792180666-f7347c490ee2?w=400' WHERE name LIKE '%Webcam%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1625948515291-69613efd103f?w=400' WHERE name LIKE '%Ổ cứng%' OR name LIKE '%USB%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1591370874773-6702e8f12fd8?w=400' WHERE name LIKE '%Màn hình%';

-- 📱 PHỤ KIỆN
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1583394838336-acd977736f90?w=400' WHERE name LIKE '%Cáp%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?w=400' WHERE name LIKE '%Sạc%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1591370874773-6702e8f12fd8?w=400' WHERE name LIKE '%Hub%';

-- 🎨 MỸ THUẬT
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1513519245088-0e12902e35ca?w=400' WHERE name LIKE '%Màu%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1452860606245-08befc0ff44b?w=400' WHERE name LIKE '%Cọ%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1561214115-f2f134cc4912?w=400' WHERE name LIKE '%Vẽ%';

-- 🛠️ DỤNG CỤ
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1530124566582-a618bc2615dc?w=400' WHERE name LIKE '%Dao%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1597825835940-1a2c383b9e9d?w=400' WHERE name LIKE '%Kẹp%';

-- 📦 Placeholder cho các sản phẩm còn lại chưa có ảnh
UPDATE items 
SET image_url = CONCAT('https://dummyimage.com/400x400/4CAF50/ffffff&text=', REPLACE(SUBSTRING(name, 1, 20), ' ', '+'))
WHERE image_url IS NULL OR image_url = '';

-- Kiểm tra kết quả
SELECT '✅ CẬP NHẬT HOÀN TẤT' as Status;

SELECT 
    COUNT(*) as 'Tổng sản phẩm',
    SUM(CASE WHEN image_url IS NOT NULL AND image_url != '' THEN 1 ELSE 0 END) as 'Có ảnh',
    SUM(CASE WHEN image_url IS NULL OR image_url = '' THEN 1 ELSE 0 END) as 'Chưa có ảnh'
FROM items;

-- Xem mẫu 15 sản phẩm
SELECT id, LEFT(name, 50) as name, LEFT(image_url, 70) as image_url 
FROM items 
ORDER BY id 
LIMIT 15;
