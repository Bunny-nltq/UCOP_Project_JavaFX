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
- Controller: `PromotionController.java`
- DTO: `PromotionDTO.java`, `PromotionApplyResultDTO.java`

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
- Controller: `ReportController.java`

### 3. ✅ Dashboard UI

**Các biểu đồ:**
- ✅ Biểu đồ cột (doanh thu theo ngày/tháng) - BarChart
- ✅ Pie chart (payment method)
- ✅ Bar chart (top sản phẩm)
- ✅ Pie chart (trạng thái đơn hàng)
- ✅ Bảng số liệu tổng quan

**Files:**
- Service: `DashboardService.java`
- Controller: `DashboardController.java`

### 4. ✅ Customer Frontend

**Các trang:**
- ✅ Trang danh sách sản phẩm với phân trang
- ✅ Tìm kiếm và lọc sản phẩm
- ✅ Trang My Orders
- ✅ Trang áp mã giảm giá

**Files:**
- Controller: `CustomerProductController.java`, `CustomerOrderController.java`

### 5. ✅ Database

**Tables đã tạo:**
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
│   │   │   ├── PromotionController.java
│   │   │   ├── DashboardController.java
│   │   │   ├── CustomerProductController.java
│   │   │   ├── CustomerOrderController.java
│   │   │   └── ...
│   │   ├── entity/            # JPA Entities
│   │   │   ├── Promotion.java
│   │   │   ├── PromotionUsage.java
│   │   │   └── ...
│   │   ├── repository/        # Data Access Layer
│   │   │   ├── PromotionRepository.java
│   │   │   ├── PromotionUsageRepository.java
│   │   │   └── ...
│   │   ├── service/           # Business Logic
│   │   │   ├── PromotionService.java
│   │   │   ├── ReportService.java
│   │   │   ├── DashboardService.java
│   │   │   └── ...
│   │   ├── dto/               # Data Transfer Objects
│   │   │   ├── PromotionDTO.java
│   │   │   ├── PromotionApplyResultDTO.java
│   │   │   └── ...
│   │   ├── util/              # Utility Classes
│   │   │   ├── HibernateUtil.java
│   │   │   └── ...
│   │   └── MainApp.java       # Main Application
│   └── resources/
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
PromotionDTO dto = new PromotionDTO();
dto.setCode("SUMMER2024");
dto.setDiscountType("PERCENTAGE");
dto.setDiscountValue(new BigDecimal("15"));
// ... set other fields
Promotion promotion = promotionService.createPromotion(dto);

// Apply promotion to order
PromotionApplyResultDTO result = promotionService.applyPromotion("SUMMER2024", order, accountId);
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
