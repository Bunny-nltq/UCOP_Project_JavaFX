# Script PowerShell để kiểm tra hình ảnh trong database
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  KIỂM TRA HÌNH ẢNH SẢN PHẨM" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan

$mysqlPath = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
$host_db = "127.0.0.1"
$port = "3307"
$user = "root"
$pass = "123456"
$database = "ucop_project_javafx"

# Kiểm tra xem MySQL có tồn tại không
if (Test-Path $mysqlPath) {
    Write-Host "`n✓ Tìm thấy MySQL" -ForegroundColor Green
    
    # Tạo SQL query
    $query = @"
USE $database;
SELECT 
    COUNT(*) as total,
    SUM(CASE WHEN image_url IS NOT NULL AND image_url != '' THEN 1 ELSE 0 END) as has_image,
    SUM(CASE WHEN image_url IS NULL OR image_url = '' THEN 1 ELSE 0 END) as no_image
FROM items;

SELECT id, name, 
    CASE 
        WHEN image_url IS NULL OR image_url = '' THEN 'Chưa có'
        ELSE 'Có ảnh'
    END as status
FROM items LIMIT 10;
"@

    # Thực thi query
    Write-Host "`n📊 Đang kiểm tra database..." -ForegroundColor Yellow
    & $mysqlPath -h$host_db -P$port -u$user -p$pass -e $query
    
} else {
    Write-Host "`n⚠ Không tìm thấy MySQL tại: $mysqlPath" -ForegroundColor Red
    Write-Host "`n💡 Hướng dẫn:" -ForegroundColor Yellow
    Write-Host "1. Mở MySQL Workbench"
    Write-Host "2. Chạy file: database/quick_check_images.sql"
    Write-Host "3. Nếu chưa có ảnh, chạy: database/add_all_images.sql"
}

Write-Host "`n=====================================" -ForegroundColor Cyan
Write-Host "✅ HOÀN TẤT" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Cyan
