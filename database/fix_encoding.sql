-- Fix encoding for Vietnamese characters in items table
-- Run this script to fix corrupted Vietnamese text

USE ucop_project_javafx;

-- Update items with correct Vietnamese names
UPDATE items SET name = 'Bút Bi Thiên Long TL-079' WHERE id = 1;
UPDATE items SET name = 'Bút Gel Thiên Long GEL-08' WHERE id = 2;
UPDATE items SET name = 'Bút Lông Dầu Thiên Long PM-08' WHERE id = 3;
UPDATE items SET name = 'Bút Chì 2B Thiên Long PC-052' WHERE id = 4;
UPDATE items SET name = 'Bút Dạ Quang 6 Màu' WHERE id = 5;
UPDATE items SET name = 'Vở Ô Ly Campus 200 Trang' WHERE id = 6;
UPDATE items SET name = 'Sổ Tay Bìa Da A5' WHERE id = 7;
UPDATE items SET name = 'Vở Kẻ Ngang 96 Trang' WHERE id = 8;
UPDATE items SET name = 'Thước Kẻ Nhựa 30cm' WHERE id = 9;
UPDATE items SET name = 'Gôm Tẩy Thiên Long E-037' WHERE id = 10;
UPDATE items SET name = 'Bộ Compa Học Sinh' WHERE id = 11;
UPDATE items SET name = 'Máy Tính Casio FX-580VN X' WHERE id = 12;
UPDATE items SET name = 'Kéo Văn Phòng Inox 21cm' WHERE id = 13;
UPDATE items SET name = 'Hồ Dán UHU 40g' WHERE id = 14;
UPDATE items SET name = 'Băng Keo Trong 2cm' WHERE id = 15;

-- Update descriptions with correct Vietnamese text
UPDATE items SET description = 'Bút bi cao cấp, mực xanh, viết mượt' WHERE id = 1;
UPDATE items SET description = 'Bút gel nước, nhiều màu, viết êm' WHERE id = 2;
UPDATE items SET description = 'Bút lông dầu không lem, màu đen' WHERE id = 3;
UPDATE items SET description = 'Bút chì gỗ, ruột chì 2B, viết đậm' WHERE id = 4;
UPDATE items SET description = 'Bộ bút đánh dấu dạ quang 6 màu rực rỡ' WHERE id = 5;
UPDATE items SET description = 'Vở ô ly khổ A4, giấy trắng, bìa cứng' WHERE id = 6;
UPDATE items SET description = 'Sổ tay cao cấp bìa da, 100 trang' WHERE id = 7;
UPDATE items SET description = 'Vở kẻ ngang tiêu chuẩn học sinh' WHERE id = 8;
UPDATE items SET description = 'Thước nhựa trong suốt, có chia vạch mm' WHERE id = 9;
UPDATE items SET description = 'Gôm tẩy trắng, không bị vụn' WHERE id = 10;
UPDATE items SET description = 'Bộ compa toán học, hộp nhựa' WHERE id = 11;
UPDATE items SET description = 'Máy tính khoa học, 552 chức năng' WHERE id = 12;
UPDATE items SET description = 'Kéo cắt giấy inox sắc bén' WHERE id = 13;
UPDATE items SET description = 'Hồ dán đa năng, không độc hại' WHERE id = 14;
UPDATE items SET description = 'Băng keo trong suốt, dán chắc' WHERE id = 15;

-- Update category names
UPDATE categories SET name = 'Bút viết' WHERE id = 1;
UPDATE categories SET name = 'Vở và Sổ' WHERE id = 2;
UPDATE categories SET name = 'Dụng cụ học tập' WHERE id = 3;
UPDATE categories SET name = 'Văn phòng phẩm' WHERE id = 4;

SELECT 'Encoding fixed successfully!' as status;
