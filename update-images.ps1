# Script to update product images in database
# Run: .\update-images.ps1

param(
    [string]$MysqlPath = "C:\xampp\mysql\bin\mysql.exe",
    [string]$DbHost = "localhost",
    [int]$Port = 3307,
    [string]$User = "root",
    [string]$Password = "123456",
    [string]$Database = "ucop_project_javafx"
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  UPDATE PRODUCT IMAGES" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Kiểm tra MySQL có tồn tại không
if (!(Test-Path $MysqlPath)) {
    Write-Host "MySQL not found at: $MysqlPath" -ForegroundColor Yellow
    Write-Host "Searching for MySQL..." -ForegroundColor Yellow
    
    $possiblePaths = @(
        "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
        "C:\xampp\mysql\bin\mysql.exe",
        "C:\wamp64\bin\mysql\mysql8.0.27\bin\mysql.exe"
    )
    
    foreach ($path in $possiblePaths) {
        if (Test-Path $path) {
            $MysqlPath = $path
            Write-Host "Found MySQL at: $MysqlPath" -ForegroundColor Green
            break
        }
    }
    
    if (!(Test-Path $MysqlPath)) {
        Write-Host "Cannot find MySQL. Please install MySQL or specify correct path." -ForegroundColor Red
        exit 1
    }
}

Write-Host "Updating product images..." -ForegroundColor Cyan

# Tạo câu lệnh SQL để cập nhật hình ảnh
$sqlScript = @"
USE $Database;

-- Cập nhật hình ảnh bằng placeholder có màu sắc đẹp
UPDATE items SET image_url = CONCAT('https://via.placeholder.com/400x400/4CAF50/ffffff?text=', REPLACE(name, ' ', '+')) WHERE image_url IS NULL OR image_url = '';

-- Hoặc dùng hình ảnh mẫu từ Unsplash (internet required)
-- UPDATE items SET image_url = 'https://images.unsplash.com/photo-1586075010923-2dd4570fb338?w=400&h=400&fit=crop' WHERE name LIKE '%Băng Keo%';

-- Xem kết quả
SELECT id, name, LEFT(image_url, 50) as image_url FROM items LIMIT 10;
"@

# Tạo file SQL tạm
$tempSqlFile = Join-Path $env:TEMP "update_images_temp.sql"
$sqlScript | Out-File -FilePath $tempSqlFile -Encoding UTF8

try {
    # Run SQL script
    Write-Host "Executing SQL commands..." -ForegroundColor Yellow
    
    # Read and execute SQL directly
    Get-Content $tempSqlFile | & $MysqlPath -h $DbHost -P $Port -u $User -p$Password 2>&1 | Out-Host
    
    $exitCode = $LASTEXITCODE
    $process = [PSCustomObject]@{ ExitCode = $exitCode }
    
    if ($process.ExitCode -eq 0) {
        Write-Host ""
        Write-Host "Images updated successfully!" -ForegroundColor Green
        Write-Host ""
        Write-Host "Notes:" -ForegroundColor Cyan
        Write-Host "  - Updated placeholder URLs for products" -ForegroundColor White
        Write-Host "  - Products will show emojis if no URL or load error" -ForegroundColor White
        Write-Host "  - To use real images, edit: database/update_product_images.sql" -ForegroundColor White
        Write-Host ""
    } else {
        Write-Host "Error occurred. Exit code: $($process.ExitCode)" -ForegroundColor Red
    }
}
catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
finally {
    # Xóa file tạm
    if (Test-Path $tempSqlFile) {
        Remove-Item $tempSqlFile -Force
    }
}

Write-Host ""
Write-Host "Press Enter to close..." -ForegroundColor Gray
Read-Host
