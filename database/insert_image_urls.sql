-- Script để thêm URL hình ảnh vào database
-- Chạy script này trong MySQL Workbench hoặc command line

USE ucop_project_javafx;

-- Cập nhật hình ảnh sản phẩm bằng URL từ các nguồn miễn phí

-- Văn phòng phẩm
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1586075010923-2dd4570fb338?w=400' WHERE name LIKE '%Băng Keo%' LIMIT 5;
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1611095790444-1dfa35e37b52?w=400' WHERE name LIKE '%Bút%' LIMIT 5;
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1596548438137-d51ea5c83295?w=400' WHERE name LIKE '%Hồ%' LIMIT 5;
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1554475900-4b0c68e7db12?w=400' WHERE name LIKE '%Kéo%' LIMIT 5;
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1544816155-12df9643f363?w=400' WHERE name LIKE '%Thước%' LIMIT 5;
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400' WHERE name LIKE '%Sổ%' OR name LIKE '%Vở%' LIMIT 5;

-- Thiết bị điện tử
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1587825140708-dfaf72ae4b04?w=400' WHERE name LIKE '%Máy Tính%' LIMIT 5;
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1612815154858-60aa4c59eaa6?w=400' WHERE name LIKE '%Máy In%' LIMIT 5;
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400' WHERE name LIKE '%Bàn Phím%' LIMIT 5;
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1527814050087-3793815479db?w=400' WHERE name LIKE '%Chuột%' LIMIT 5;
UPDATE items SET image_url = 'https://images.unsplash.com/photo-1545127398-14699f92334b?w=400' WHERE name LIKE '%Tai Nghe%' LIMIT 5;

-- Hoặc dùng placeholder màu đẹp cho tất cả sản phẩm chưa có ảnh
UPDATE items 
SET image_url = CONCAT('https://dummyimage.com/400x400/4CAF50/ffffff&text=', REPLACE(SUBSTRING(name, 1, 20), ' ', '+'))
WHERE image_url IS NULL OR image_url = '';

-- Kiểm tra kết quả
SELECT id, name, image_url FROM items LIMIT 15;
