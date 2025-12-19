# UCOP Project - Universal Commerce & Operations Platform

## 📋 Mô Tả Dự Án   

mvn clean javafx:run


Hệ thống quản lý đơn hàng và thanh toán toàn diện cho nền tảng thương mại điện tử, bao gồm:
- ✅ Quản lý Promotion (Khuyến mãi)
- ✅ Báo cáo doanh thu và thống kê
- ✅ Dashboard với biểu đồ trực quan
- ✅ Giao diện khách hàng xem sản phẩm và đơn hàng
- ✅ Quản lý kho hàng và tồn kho
- ✅ Quản lý thanh toán và vận chuyển

## 🚀 Bắt Đầu Nhanh

### Cách 1: Tự động với PowerShell Script

```powershell
# Chạy script tự động setup database
.\setup-database.ps1
```

Script sẽ tự động:
- Kiểm tra MySQL
- Tạo database và import schema
- Cập nhật file cấu hình
- Build project (tùy chọn)

### Cách 2: Manual Setup

Xem hướng dẫn chi tiết trong [SETUP_GUIDE.md](SETUP_GUIDE.md)

## 📌 Chi tiết công việc 

### 1. ✅ Promotion System

**CRUD Promotion với các loại:**
- ✅ % giảm (PERCENTAGE)
- ✅ Giảm số tiền cố định (FIXED_AMOUNT)
- ✅ Mã cho từng Item (ITEM)
- ✅ Mã cho cả giỏ hàng (CART)
- ✅ Giới hạn số lượt sử dụng
- ✅ Giới hạn thời gian
- ✅ Áp dụng mã → trả về số tiền giảm
- ✅ Lưu PromotionUsage khi dùng thành công

**Files:**
- Entity: `Promotion.java`, `PromotionUsage.java`
- Repository: `PromotionRepository.java`, `PromotionUsageRepository.java`
- Service: `PromotionService.java`
- Controller (Admin): `admin/PromotionController.java`, `admin/PromotionFormController.java`, `admin/PromotionManagementController.java`
- Controller (Customer): `customer/ApplyPromotionController.java`
- DAO: `PromotionDAO.java`, `PromotionApplyResultDAO.java`

### 2. ✅ Reporting

**Lấy dữ liệu từ Order + Payment:**
- ✅ Doanh thu theo ngày/tháng
- ✅ Doanh thu theo phương thức thanh toán
- ✅ Tổng số đơn
- ✅ Tổng số đơn canceled/refunded
- ✅ Top sản phẩm bán chạy
- ✅ Tồn kho
- ✅ Export CSV

**Files:**
- Service: `ReportService.java`
- Controller (Admin): `admin/ReportViewController.java`

### 3. ✅ Dashboard UI

**Các biểu đồ:**
- ✅ Biểu đồ cột (doanh thu theo ngày/tháng) - BarChart
- ✅ Pie chart (payment method)
- ✅ Bar chart (top sản phẩm)
- ✅ Pie chart (trạng thái đơn hàng)
- ✅ Bảng số liệu tổng quan

**Files:**
- Service: `DashboardService.java`
- Controller (Admin): `admin/DashboardController.java`
- FXML: `UI/admin/dashboard.fxml`

### 4. ✅ Customer Frontend

**Các trang:**
- ✅ Trang danh sách sản phẩm với phân trang
- ✅ Tìm kiếm và lọc sản phẩm
- ✅ Trang My Orders
- ✅ Trang áp mã giảm giá
- ✅ **Hiển thị hình ảnh sản phẩm từ database**
- ✅ **Tự động fallback sang emoji placeholder**

**Files:**
- Controller (Customer): `customer/CustomerProductController.java`, `customer/CustomerOrderController.java`, `customer/CustomerMainController.java`, `customer/ProductDetailController.java`, `customer/CustomerItemsController.java`
- FXML: `UI/customer/customer-main.fxml`, `UI/customer/customer-products.fxml`, `UI/customer/customer-orders.fxml`, `UI/customer/product-detail.fxml`, `UI/customer/customer_items.fxml`

**Hướng dẫn hiển thị hình ảnh:** Xem [QUICK_START_HINH_ANH.md](QUICK_START_HINH_ANH.md)

### 5. ✅ Hiển Thị Hình Ảnh Sản Phẩm

**Tính năng:**
- ✅ Hiển thị hình ảnh từ URL trong database (`image_url` field)
- ✅ Hỗ trợ hình ảnh từ Unsplash, URL bên ngoài
- ✅ Tự động fallback sang emoji placeholder nếu không có ảnh
- ✅ Emoji thông minh dựa trên tên sản phẩm (✏️ bút, 💻 máy tính, 📓 sổ,...)
- ✅ Xử lý lỗi load ảnh mượt mà

**Quick Start:**
```bash
# 1. Cập nhật hình ảnh trong database
# Chạy file SQL trong MySQL Workbench:
database/update_items_images_real.sql

# 2. Khởi động ứng dụng
mvn clean javafx:run
```

**Hướng dẫn chi tiết:**
- 📘 [QUICK_START_HINH_ANH.md](QUICK_START_HINH_ANH.md) - Hướng dẫn 3 bước nhanh
- 📗 [HUONG_DAN_HIEN_THI_HINH_ANH_THUC.md](HUONG_DAN_HIEN_THI_HINH_ANH_THUC.md) - Hướng dẫn đầy đủ
- 📙 [XAC_NHAN_HIEN_THI_ANH.md](XAC_NHAN_HIEN_THI_ANH.md) - Xác nhận code đã sẵn sàng

**Files liên quan:**
- SQL: `database/update_items_images_real.sql`
- Entity: `entity/Item.java` (field `imageUrl`)
- Controllers: `CustomerItemsController.java`, `ProductDetailController.java`
- Utility: `util/UpdateProductImages.java`

### 6. ✅ Database

**Tables đã tạo:**
- ✅ items - Sản phẩm (có field `image_url`)
- ✅ promotions - Khuyến mãi
- ✅ promotion_usages - Lịch sử sử dụng
- ✅ warehouses - Kho hàng
- ✅ stock_items - Tồn kho
- ✅ orders - Đơn hàng
- ✅ order_items - Chi tiết đơn
- ✅ payments - Thanh toán
- ✅ refunds - Hoàn tiền
- ✅ shipments - Vận chuyển
- ✅ appointments - Lịch hẹn
- ✅ carts, cart_items - Giỏ hàng

## 🛠️ Công Nghệ Sử Dụng

- **Backend:** Java 17+, Spring Framework 5.3.20
- **ORM:** Hibernate 6.3.1
- **Database:** MySQL 8.0
- **UI Framework:** JavaFX 25.0.1
- **Charts:** JavaFX Charts (BarChart, LineChart, PieChart)
- **Build Tool:** Maven
- **Connection Pool:** HikariCP, C3P0

## 📁 Cấu Trúc Project

```
src/
├── main/
│   ├── java/com/ucop/
│   │   ├── controller/        # Controllers cho UI
│   │   │   ├── admin/         # Admin Controllers
│   │   │   │   ├── PromotionController.java
│   │   │   │   ├── PromotionFormController.java
│   │   │   │   ├── PromotionManagementController.java
│   │   │   │   ├── DashboardController.java
│   │   │   │   ├── ReportViewController.java
│   │   │   │   └── WarehouseController.java
│   │   │   ├── customer/      # Customer Controllers
│   │   │   │   ├── CustomerMainController.java
│   │   │   │   ├── CustomerProductController.java
│   │   │   │   ├── CustomerOrderController.java
│   │   │   │   ├── ProductDetailController.java
│   │   │   │   └── ApplyPromotionController.java
│   │   │   ├── shared/        # Shared Controllers
│   │   │   └── staff/         # Staff Controllers
│   │   ├── entity/            # JPA Entities
│   │   │   ├── Promotion.java
│   │   │   ├── PromotionUsage.java
│   │   │   ├── Warehouse.java
│   │   │   └── ...
│   │   ├── repository/        # Data Access Layer
│   │   │   ├── PromotionRepository.java
│   │   │   ├── PromotionUsageRepository.java
│   │   │   └── ...
│   │   ├── service/           # Business Logic
│   │   │   ├── PromotionService.java
│   │   │   ├── ReportService.java
│   │   │   ├── DashboardService.java
│   │   │   ├── WarehouseService.java
│   │   │   └── ...
│   │   ├── dao/               # Data Access Objects (formerly DTO)
│   │   │   ├── PromotionDAO.java
│   │   │   ├── PromotionApplyResultDAO.java
│   │   │   ├── PaymentCalculationDAO.java
│   │   │   └── CartItemDAO.java
│   │   ├── util/              # Utility Classes
│   │   │   ├── HibernateUtil.java
│   │   │   └── ...
│   │   └── MainApp.java       # Main Application
│   └── resources/
│       ├── UI/
│       │   ├── admin/         # Admin FXML
│       │   │   ├── dashboard.fxml
│       │   │   ├── reports.fxml
│       │   │   ├── promotion-management.fxml
│       │   │   └── promotion-form.fxml
│       │   ├── customer/      # Customer FXML
│       │   │   ├── customer-main.fxml
│       │   │   ├── customer-products.fxml
│       │   │   ├── customer-orders.fxml
│       │   │   ├── product-detail.fxml
│       │   │   └── apply-promotion-dialog.fxml
│       │   ├── share/         # Shared FXML
│       │   └── staff/         # Staff FXML
│       ├── style/             # CSS Styles
│       ├── application.properties
│       └── hibernate.cfg.xml
└── database/
    ├── ucop_mysql_schema.sql      # MySQL Schema
    └── ucop_order_payment_schema.sql  # SQL Server Schema (legacy)
```

## 📊 Dữ Liệu Mẫu

### Warehouses (Kho)
1. Kho Miền Bắc - Hà Nội
2. Kho Miền Nam - TP.HCM
3. Kho Miền Trung - Đà Nẵng

### Promotions (Khuyến mãi)
1. **WELCOME10** - Giảm 10% cho đơn đầu tiên (min 100k, max giảm 50k)
2. **SALE50K** - Giảm 50k cho đơn từ 500k
3. **FREESHIP** - Miễn phí ship (30k) cho đơn từ 300k
4. **VIP20** - Giảm 20% VIP (min 1tr, max giảm 200k)

## 🔧 Cấu Hình

### Database Connection

**hibernate.cfg.xml:**
```xml
<property name="hibernate.connection.url">
    jdbc:mysql://localhost:3307/ucop_project_javafx?useSSL=false&amp;serverTimezone=UTC
</property>
<property name="hibernate.connection.username">root</property>
<property name="hibernate.connection.password">123456</property>
```

**application.properties:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3307/ucop_project_javafx?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=123456
```

## 📝 Lệnh Maven Hữu Ích

```powershell
# Clean project
mvn clean

# Compile
mvn compile

# Build JAR
mvn package

# Install dependencies
mvn install

# Run application
mvn javafx:run

# Build và run
mvn clean install javafx:run

# Skip tests
mvn clean install -DskipTests
```

## 🎯 API Service Examples

### PromotionService

```java
// Create promotion
PromotionDAO dao = new PromotionDAO();
dao.setCode("SUMMER2024");
dao.setDiscountType("PERCENTAGE");
dao.setDiscountValue(new BigDecimal("15"));
// ... set other fields
Promotion promotion = promotionService.createPromotion(dao);

// Apply promotion to order
PromotionApplyResultDAO result = promotionService.applyPromotion("SUMMER2024", order, accountId);
if (result.isSuccess()) {
    System.out.println("Discount: " + result.getDiscountAmount());
}
```

### ReportService

```java
// Get revenue report
Map<String, Object> revenue = reportService.getRevenueByDateRange(startDate, endDate);
BigDecimal total = (BigDecimal) revenue.get("totalRevenue");

// Get top products
List<Map<String, Object>> topProducts = reportService.getTopSellingProducts(startDate, endDate, 10);
```

### DashboardService

```java
// Create charts
BarChart revenueChart = dashboardService.createRevenueBarChart(startDate, endDate);
PieChart paymentChart = dashboardService.createPaymentMethodPieChart(startDate, endDate);
```

## 🐛 Troubleshooting

Xem [SETUP_GUIDE.md](SETUP_GUIDE.md) phần Troubleshooting

## 👥 Phân Công

**Phụ trách:**
- ✅ Promotion
- ✅ PromotionUsage
- ✅ Report tổng hợp
- ✅ Dashboard biểu đồ doanh thu/top sản phẩm
- ✅ UI cho Customer xem sản phẩm, xem order
- ✅ Database cho Promotion + Report

## 📄 License

This project is for educational purposes.

## 🎉 Hoàn Thành

Tất cả các yêu cầu đã được implement đầy đủ:
- ✅ Promotion System với đầy đủ tính năng
- ✅ Report Service với các báo cáo chi tiết
- ✅ Dashboard với biểu đồ JavaFX
- ✅ Customer UI Controllers
- ✅ Database schema hoàn chỉnh
- ✅ Dữ liệu mẫu
- ✅ Documentation đầy đủ

Chúc bạn code vui vẻ! 🚀
