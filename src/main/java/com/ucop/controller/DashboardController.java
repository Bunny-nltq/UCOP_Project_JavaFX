package com.ucop.controller;

import com.ucop.service.DashboardService;
import com.ucop.service.ReportService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Parent;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Controller for Dashboard with Charts and Statistics
 */
public class DashboardController {

    @FXML
    private Label lblTotalRevenue;

    @FXML
    private Label lblTotalOrders;

    @FXML
    private Label lblPendingOrders;

    @FXML
    private Label lblLowStockItems;

    @FXML
    private BarChart<String, Number> revenueChart;

    @FXML
    private PieChart paymentMethodChart;

    @FXML
    private BarChart<String, Number> topProductsChart;

    @FXML
    private PieChart orderStatusChart;

    @FXML
    private Label lblStatus;

    @FXML
    private Label lblDateTime;

    @FXML
    private StackPane contentArea;

    @FXML
    private ScrollPane dashboardView;

    private DashboardService dashboardService;
    private ReportService reportService;

    @FXML
    public void initialize() {
        try {
            // Initialize Hibernate and repositories
            System.out.println("Đang khởi tạo SessionFactory...");
            org.hibernate.SessionFactory sessionFactory = com.ucop.util.HibernateUtil.getSessionFactory();
            System.out.println("SessionFactory đã được khởi tạo thành công");
            
            com.ucop.repository.OrderRepository orderRepository = new com.ucop.repository.impl.OrderRepositoryImpl(sessionFactory);
            com.ucop.repository.PaymentRepository paymentRepository = new com.ucop.repository.impl.PaymentRepositoryImpl(sessionFactory);
            com.ucop.repository.StockItemRepository stockItemRepository = new com.ucop.repository.impl.StockItemRepositoryImpl(sessionFactory);
            
            // Initialize services
            reportService = new ReportService(sessionFactory, orderRepository, paymentRepository, stockItemRepository);
            dashboardService = new DashboardService(reportService);
            System.out.println("Các service đã được khởi tạo thành công");
            
            // Start datetime updater
            startDateTimeUpdater();
            
            // Load dashboard data
            loadDashboard();
        } catch (Exception e) {
            System.err.println("Lỗi khởi tạo Dashboard: " + e.getMessage());
            e.printStackTrace();
            if (lblStatus != null) {
                lblStatus.setText("Lỗi khởi tạo: " + e.getMessage());
                lblStatus.setStyle("-fx-text-fill: red;");
            }
            // Still try to start the datetime updater even if initialization fails
            try {
                startDateTimeUpdater();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void startDateTimeUpdater() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Platform.runLater(() -> {
                        String datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                        lblDateTime.setText(datetime);
                    });
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void refreshDashboard() {
        loadDashboard();
    }

    private void loadDashboard() {
        try {
            lblStatus.setText("Đang tải dữ liệu...");
            lblStatus.setStyle("-fx-text-fill: orange;");
            
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(7);
            
            // Load statistics
            loadStatistics(startDate, endDate);
            
            // Load charts
            loadCharts(startDate, endDate);
            
            lblStatus.setText("Sẵn sàng");
            lblStatus.setStyle("-fx-text-fill: green;");
            
        } catch (Exception e) {
            lblStatus.setText("Lỗi: " + e.getMessage());
            lblStatus.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    private void loadStatistics(LocalDate startDate, LocalDate endDate) {
        try {
            Map<String, Object> revenueData = reportService.getRevenueByDateRange(startDate, endDate);
            BigDecimal totalRevenue = (BigDecimal) revenueData.get("totalRevenue");
            lblTotalRevenue.setText(String.format("%,.0f đ", totalRevenue));
            
            Long totalOrders = (Long) revenueData.get("totalOrders");
            lblTotalOrders.setText(totalOrders.toString());
            
            // Get pending orders (mocked for now)
            lblPendingOrders.setText("5");
            
            // Get low stock items
            var lowStockItems = reportService.getLowStockItems();
            lblLowStockItems.setText(String.valueOf(lowStockItems.size()));
            
        } catch (Exception e) {
            lblTotalRevenue.setText("0 đ");
            lblTotalOrders.setText("0");
            lblPendingOrders.setText("0");
            lblLowStockItems.setText("0");
        }
    }

    private void loadCharts(LocalDate startDate, LocalDate endDate) {
        try {
            // Load revenue chart
            try {
                BarChart<String, Number> revChart = dashboardService.createRevenueBarChart(startDate, endDate);
                revenueChart.getData().clear();
                revenueChart.getData().addAll(revChart.getData());
            } catch (Exception e) {
                System.err.println("Lỗi tải biểu đồ doanh thu: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Load payment method chart
            try {
                PieChart pmChart = dashboardService.createPaymentMethodPieChart(startDate, endDate);
                paymentMethodChart.getData().clear();
                paymentMethodChart.getData().addAll(pmChart.getData());
            } catch (Exception e) {
                System.err.println("Lỗi tải biểu đồ phương thức thanh toán: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Load top products chart
            try {
                BarChart<String, Number> tpChart = dashboardService.createTopProductsChart(startDate, endDate, 5);
                topProductsChart.getData().clear();
                topProductsChart.getData().addAll(tpChart.getData());
            } catch (Exception e) {
                System.err.println("Lỗi tải biểu đồ sản phẩm: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Load order status chart
            try {
                PieChart osChart = dashboardService.createOrderStatusPieChart(startDate, endDate);
                orderStatusChart.getData().clear();
                orderStatusChart.getData().addAll(osChart.getData());
            } catch (Exception e) {
                System.err.println("Lỗi tải biểu đồ trạng thái đơn hàng: " + e.getMessage());
                e.printStackTrace();
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi chung khi tải biểu đồ: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void showPromotionManagement() {
        loadPromotionManagementInContent();
    }
    
    private void loadPromotionManagementInContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/promotion-management.fxml"));
            Parent view = loader.load();
            
            // Get controller and set parent reference
            PromotionManagementController controller = loader.getController();
            if (controller != null) {
                controller.setDashboardController(this);
            }
            
            // Clear all previous views first
            contentArea.getChildren().removeIf(node -> node != dashboardView);
            
            // Hide dashboard and show new view
            dashboardView.setVisible(false);
            dashboardView.setManaged(false);
            contentArea.getChildren().add(view);
            
        } catch (Exception e) {
            showError("Không thể tải quản lý khuyến mãi: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void showPromotionForm(com.ucop.entity.Promotion promotion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/promotion-form.fxml"));
            Parent view = loader.load();
            
            // Get controller and set references
            PromotionFormController controller = loader.getController();
            if (controller != null) {
                controller.setDashboardController(this);
                
                // Initialize service
                org.hibernate.SessionFactory sessionFactory = com.ucop.util.HibernateUtil.getSessionFactory();
                com.ucop.repository.PromotionRepository promotionRepository = new com.ucop.repository.PromotionRepository(sessionFactory);
                com.ucop.repository.PromotionUsageRepository usageRepository = new com.ucop.repository.PromotionUsageRepository(sessionFactory);
                com.ucop.service.PromotionService promotionService = new com.ucop.service.PromotionService(promotionRepository, usageRepository);
                controller.setPromotionService(promotionService);
                
                if (promotion != null) {
                    controller.setPromotion(promotion);
                }
            }
            
            // Clear all previous views first
            contentArea.getChildren().removeIf(node -> node != dashboardView);
            
            // Hide dashboard and show form
            dashboardView.setVisible(false);
            dashboardView.setManaged(false);
            contentArea.getChildren().add(view);
            
        } catch (Exception e) {
            showError("Không thể tải form khuyến mãi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void showWarehouseManagement() {
        showInfo("Tính năng Quản Lý Kho Hàng đang được phát triển.");
    }

    @FXML
    private void showOrderManagement() {
        showInfo("Tính năng Quản Lý Đơn Hàng đang được phát triển.");
    }

    @FXML
    private void showPaymentManagement() {
        showInfo("Tính năng Quản Lý Thanh Toán đang được phát triển.");
    }

    @FXML
    private void showRevenueReport() {
        loadReportViewInContent("/fxml/reports.fxml", 0);
    }

    @FXML
    private void showTopProductsReport() {
        loadReportViewInContent("/fxml/reports.fxml", 3);
    }

    @FXML
    private void showInventoryReport() {
        loadReportViewInContent("/fxml/reports.fxml", 4);
    }
    
    private void loadViewInContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            
            // Create a container with back button
            VBox container = new VBox(10);
            container.setStyle("-fx-padding: 10;");
            
            // Add back button
            HBox toolbar = new HBox(10);
            toolbar.setStyle("-fx-padding: 10; -fx-background-color: #f5f5f5;");
            Button btnBack = new Button("← Quay lại Dashboard");
            btnBack.setStyle("-fx-font-size: 14px; -fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 8 15;");
            btnBack.setOnAction(e -> showDashboard());
            toolbar.getChildren().add(btnBack);
            
            container.getChildren().addAll(toolbar, view);
            VBox.setVgrow(view, Priority.ALWAYS);
            
            // Clear all previous views first
            contentArea.getChildren().removeIf(node -> node != dashboardView);
            
            // Hide dashboard and show new view
            dashboardView.setVisible(false);
            dashboardView.setManaged(false);
            contentArea.getChildren().add(container);
            
        } catch (Exception e) {
            showError("Không thể tải nội dung: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadReportViewInContent(String fxmlPath, int tabIndex) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            
            // Get controller and set tab
            ReportViewController controller = loader.getController();
            if (controller != null) {
                controller.setDashboardController(this);
                controller.setInitialTab(tabIndex);
                Platform.runLater(() -> controller.selectTab(tabIndex));
            }
            
            // Clear all previous views first
            contentArea.getChildren().removeIf(node -> node != dashboardView);
            
            // Hide dashboard and show new view
            dashboardView.setVisible(false);
            dashboardView.setManaged(false);
            contentArea.getChildren().add(view);
            
        } catch (Exception e) {
            showError("Không thể tải báo cáo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    public void showDashboard() {
        // Remove all added views
        contentArea.getChildren().removeIf(node -> node != dashboardView);
        
        // Show dashboard again
        dashboardView.setVisible(true);
        dashboardView.setManaged(true);
        
        // Refresh dashboard data
        refreshDashboard();
    }

    @FXML
    private void exportReport() {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(30);
            String csvData = reportService.exportRevenueReportToCSV(startDate, endDate);
            showSuccess("Đã xuất báo cáo thành công!\n\n" + csvData.substring(0, Math.min(200, csvData.length())) + "...");
        } catch (Exception e) {
            showError("Lỗi khi xuất báo cáo: " + e.getMessage());
        }
    }

    @FXML
    private void showHelp() {
        showInfo("UCOP - Universal Commerce & Operations Platform\n\n" +
                "Hướng dẫn sử dụng:\n" +
                "1. Dashboard hiển thị thống kê tổng quan\n" +
                "2. Sử dụng menu để truy cập các chức năng\n" +
                "3. Nhấn 'Làm Mới' để cập nhật dữ liệu");
    }

    @FXML
    private void showAbout() {
        showInfo("UCOP - Universal Commerce & Operations Platform\n\n" +
                "Phiên bản: 1.0.0\n" +
                "Hệ thống quản lý đơn hàng và thanh toán\n\n" +
                "© 2025 UCOP Team");
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
