-- Script kiểm tra nhanh hình ảnh sản phẩm
USE ucop_project_javafx;

-- Kiểm tra thống kê
SELECT 
    '📊 THỐNG KÊ' as Info,
    COUNT(*) as 'Tổng sản phẩm',
    SUM(CASE WHEN image_url IS NOT NULL AND image_url != '' THEN 1 ELSE 0 END) as 'Có ảnh',
    SUM(CASE WHEN image_url IS NULL OR image_url = '' THEN 1 ELSE 0 END) as 'Chưa có ảnh'
FROM items;

-- Xem 10 sản phẩm đầu
SELECT 
    id as 'ID',
    LEFT(name, 40) as 'Tên sản phẩm',
    CASE 
        WHEN image_url IS NULL OR image_url = '' THEN '❌ Chưa có'
        ELSE '✅ Có ảnh'
    END as 'Trạng thái',
    LEFT(image_url, 60) as 'URL (60 ký tự đầu)'
FROM items 
ORDER BY id 
LIMIT 10;
