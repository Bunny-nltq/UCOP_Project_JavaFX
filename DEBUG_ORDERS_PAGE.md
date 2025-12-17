# 🔍 HƯỚNG DẪN DEBUG VÀ KIỂM TRA TRANG ĐƠN HÀNG

## ❗ Vấn đề
Trang "Đơn Hàng Của Tôi" hiển thị "No content in table" - không có dữ liệu từ database.

## ✅ CÁC BƯỚC KHẮC PHỤC

### BƯỚC 1: Kiểm tra và tạo dữ liệu mẫu trong Database

1. **Mở MySQL Workbench** hoặc công cụ MySQL client
2. **Chạy script tạo dữ liệu mẫu:**
   ```bash
   # Mở file này trong MySQL Workbench:
   database/insert_sample_orders.sql
   ```
3. **Thực thi toàn bộ script** (Ctrl + Shift + Enter)
4. **Kiểm tra kết quả:**
   - Script sẽ hiển thị các đơn hàng hiện có
   - Tự động tạo 3 đơn hàng mẫu nếu chưa có
   - Hiển thị tổng số đơn hàng cho account_id = 1

**Hoặc kiểm tra bằng query đơn giản:**
```sql
USE ucop_project_javafx;
SELECT * FROM orders WHERE account_id = 1 ORDER BY placed_at DESC;
```

### BƯỚC 2: Chạy ứng dụng với Debug Logging

```bash
mvn javafx:run
```

### BƯỚC 3: Theo dõi Console Log

Khi ứng dụng khởi động, bạn sẽ thấy:

```
=== Initializing Services ===
✓ SessionFactory: OK
✓ All repositories initialized
✓ ProductService: OK
✓ OrderService: OK
✓ PromotionService: OK
✓ Current Account ID: 1
```

### BƯỚC 4: Click vào "Đơn hàng của tôi"

Trong console, bạn sẽ thấy:

```
=== Loading Orders Page ===
OrderService: OK
Current Account ID: 1
✓ FXML loaded successfully
✓ Controller obtained: OK
✓ OrderService set to controller
✓ Account ID set to controller: 1
✓ Orders page loaded to mainContainer
=== Loading Orders ===
OrderService: OK
Account ID: 1
✓ Loaded X orders from database
```

**X** là số đơn hàng được load. Nếu X = 0, nghĩa là database chưa có đơn hàng.

### BƯỚC 5: Test tạo đơn hàng mới

1. **Vào trang "Sản phẩm"**
2. **Thêm sản phẩm vào giỏ hàng**
3. **Xem giỏ hàng**
4. **Nhấn "Thanh toán"**

Trong console, bạn sẽ thấy:

```
=== Creating Order ===
Cart ID: 1
Account ID: 1
✓ Order created successfully!
Order ID: 123
Order Number: ORD-20251217-001
Account ID: 1
=== Navigating to Orders Page ===
MainController: OK
=== Loading Orders Page ===
...
✓ Loaded X orders from database
```

Sau đó tự động chuyển sang trang "Đơn hàng của tôi" và hiển thị đơn hàng vừa tạo.

## 🐛 CÁC VẤN ĐỀ CÓ THỂ GẶP VÀ CÁCH GIẢI QUYẾT

### Vấn đề 1: "OrderService: NULL"
**Nguyên nhân:** OrderService không được khởi tạo
**Giải pháp:** 
- Kiểm tra lại HibernateUtil.getSessionFactory()
- Kiểm tra hibernate.cfg.xml có đúng cấu hình database không

### Vấn đề 2: "Loaded 0 orders from database"
**Nguyên nhân:** Database chưa có đơn hàng cho account_id = 1
**Giải pháp:**
- Chạy script `database/insert_sample_orders.sql`
- Hoặc tạo đơn hàng mới bằng cách checkout

### Vấn đề 3: "Account ID: null"
**Nguyên nhân:** currentAccountId không được set đúng
**Giải pháp:**
- Kiểm tra CustomerMainController.currentAccountId = 1L
- Hiện tại đang hardcode = 1L cho testing

### Vấn đề 4: "MainController: NULL"
**Nguyên nhân:** CustomerMainController không được truyền vào CartController
**Giải pháp:**
- Kiểm tra CustomerProductControllerV2.setCustomerMainController()
- Đảm bảo được gọi trong CustomerMainController.loadProductsPage()

### Vấn đề 5: Exception khi load orders
**Nguyên nhân:** Lỗi SQL hoặc mapping entity
**Giải pháp:**
- Xem chi tiết exception trong console
- Kiểm tra Order entity mapping
- Kiểm tra OrderRepository query

## 📊 KIỂM TRA DATABASE TRỰC TIẾP

```sql
-- 1. Kiểm tra bảng orders có tồn tại không
USE ucop_project_javafx;
SHOW TABLES LIKE 'orders';

-- 2. Kiểm tra cấu trúc bảng
DESC orders;

-- 3. Kiểm tra dữ liệu
SELECT * FROM orders;

-- 4. Kiểm tra orders theo account_id
SELECT id, order_number, account_id, status, grand_total, placed_at 
FROM orders 
WHERE account_id = 1 
ORDER BY placed_at DESC;

-- 5. Đếm số đơn hàng
SELECT COUNT(*) as total_orders FROM orders WHERE account_id = 1;

-- 6. Kiểm tra các status
SELECT status, COUNT(*) as count 
FROM orders 
GROUP BY status;
```

## 🎯 CHECKLIST ĐỂ KIỂM TRA

- [ ] Database ucop_project_javafx đã được tạo
- [ ] Bảng orders tồn tại và có đúng cấu trúc
- [ ] Có ít nhất 1 order với account_id = 1 trong database
- [ ] Hibernate.cfg.xml có đúng connection string
- [ ] Console log hiển thị "OrderService: OK"
- [ ] Console log hiển thị "Loaded X orders" với X > 0
- [ ] TableView hiển thị dữ liệu (không còn "No content in table")

## 📝 KẾT QUẢ MONG ĐỢI

Sau khi hoàn thành các bước trên:

1. ✅ Trang "Đơn hàng của tôi" hiển thị bảng với các cột:
   - Mã đơn hàng
   - Ngày đặt
   - Trạng thái (với màu sắc)
   - Tổng tiền (định dạng VNĐ)

2. ✅ Click vào một đơn hàng → hiển thị chi tiết ở phía dưới

3. ✅ Sau khi thanh toán → tự động chuyển sang trang orders và thấy đơn hàng mới

4. ✅ Bộ lọc hoạt động:
   - Tìm kiếm theo mã đơn hàng
   - Lọc theo trạng thái
   - Hiển thị tổng số đơn hàng

## 🚀 CHẠY ỨNG DỤNG

```bash
# Compile
mvn compile

# Chạy với debug logging
mvn javafx:run
```

## 📞 NẾU VẪN GẶP VẤN ĐỀ

Cung cấp cho tôi:
1. Screenshot console log khi khởi động app
2. Screenshot console log khi click "Đơn hàng của tôi"
3. Kết quả query: `SELECT * FROM orders WHERE account_id = 1;`
4. Screenshot của trang đơn hàng
