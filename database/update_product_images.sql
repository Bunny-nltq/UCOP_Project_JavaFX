-- Script để cập nhật hình ảnh sản phẩm
-- Sử dụng hình ảnh từ các nguồn miễn phí

USE ucop_project_javafx;

-- Cập nhật hình ảnh cho các sản phẩm văn phòng phẩm
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1586075010923-2dd4570fb338?w=400' WHERE name LIKE '%Băng Keo%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1611095790444-1dfa35e37b52?w=400' WHERE name LIKE '%Bút%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1596548438137-d51ea5c83295?w=400' WHERE name LIKE '%Hồ Dán%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1587825140708-dfaf72ae4b04?w=400' WHERE name LIKE '%Kéo%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1544816155-12df9643f363?w=400' WHERE name LIKE '%Thước%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1586281380349-632531db7ed4?w=400' WHERE name LIKE '%Bìa%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1544816155-12df9643f363?w=400' WHERE name LIKE '%Sổ%';

-- Cập nhật hình ảnh cho máy tính và thiết bị điện tử
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1587825140708-dfaf72ae4b04?w=400' WHERE name LIKE '%Máy Tính%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400' WHERE name LIKE '%Máy In%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1529336953128-a85760f58cb5?w=400' WHERE name LIKE '%Bàn Phím%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1527814050087-3793815479db?w=400' WHERE name LIKE '%Chuột%';
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=400' WHERE name LIKE '%Tai Nghe%';

-- Hoặc sử dụng URL hình ảnh placeholder đơn giản với màu sắc
-- UPDATE items SET image_url = CONCAT('https://via.placeholder.com/400x400/4CAF50/ffffff?text=', REPLACE(name, ' ', '+')) WHERE image_url IS NULL OR image_url = '';

-- Xem kết quả
SELECT id, name, image_url FROM items LIMIT 10;
