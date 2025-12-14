package com.ucop.controller.admin;

import com.ucop.service.ReportService;
import com.ucop.repository.*;
import com.ucop.repository.impl.*;
import com.ucop.util.HibernateUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ReportViewController {

    @FXML private DatePicker dateFrom;
    @FXML private DatePicker dateTo;
    @FXML private TabPane tabReports;
    @FXML private Label lblStatus;
    @FXML private Label lblLastUpdate;

    // Revenue Tab
    @FXML private TableView<?> tableRevenue;
    @FXML private Label lblRevTotal;
    @FXML private Label lblRevOrders;
    @FXML private Label lblRevAverage;

    // Payment Tab
    @FXML private PieChart chartPaymentMethods;
    @FXML private TableView<?> tablePaymentMethods;

    // Orders Tab
    @FXML private Label lblTotalOrders;
    @FXML private Label lblCompletedOrders;
    @FXML private Label lblCanceledOrders;
    @FXML private Label lblRefundedOrders;
    @FXML private PieChart chartOrderStatus;

    // Products Tab
    @FXML private BarChart<String, Number> chartTopProducts;
    @FXML private TableView<?> tableTopProducts;

    // Inventory Tab
    @FXML private TableView<?> tableLowStock;
    @FXML private Label lblTotalItems;
    @FXML private Label lblLowStockItems;
    @FXML private Label lblOutOfStockItems;

    private ReportService reportService;
    private int initialTabIndex = 0; // Default tab to open
    private DashboardController dashboardController;

    @FXML
    public void initialize() {
        try {
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            OrderRepository orderRepository = new OrderRepositoryImpl(sessionFactory);
            PaymentRepository paymentRepository = new PaymentRepositoryImpl(sessionFactory);
            StockItemRepository stockItemRepository = new StockItemRepositoryImpl(sessionFactory);
            
            reportService = new ReportService(sessionFactory, orderRepository, paymentRepository, stockItemRepository);

            // Set default date range (last 30 days)
            dateTo.setValue(LocalDate.now());
            dateFrom.setValue(LocalDate.now().minusDays(30));

            loadAllReports();
            
            // Select the initial tab AFTER everything is loaded
            if (tabReports != null && initialTabIndex >= 0 && initialTabIndex < tabReports.getTabs().size()) {
                tabReports.getSelectionModel().select(initialTabIndex);
            }
            
            lblStatus.setText("Sẵn sàng");
        } catch (Exception e) {
            showError("Lỗi khởi tạo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Set which tab to select when the window opens (must be called BEFORE scene is shown)
     */
    public void setInitialTab(int index) {
        this.initialTabIndex = index;
    }
    
    /**
     * Select a specific tab (called from DashboardController)
     */
    public void selectTab(int index) {
        if (tabReports != null && index >= 0 && index < tabReports.getTabs().size()) {
            tabReports.getSelectionModel().select(index);
        } else {
            // If TabPane not ready yet, store for later
            this.initialTabIndex = index;
        }
    }
    
    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }
    
    @FXML
    private void handleGoBack() {
        try {
            if (dashboardController != null) {
                dashboardController.showDashboard();
            } else {
                // Fallback: close window if not in dashboard
                javafx.stage.Stage stage = (javafx.stage.Stage) lblStatus.getScene().getWindow();
                stage.close();
            }
        } catch (Exception e) {
            showError("Lỗi khi quay lại: " + e.getMessage());
        }
    }

    @FXML
    private void handleGenerateReport() {
        loadAllReports();
    }

    @FXML
    private void handleExportCSV() {
        try {
            String csv = reportService.exportRevenueReportToCSV(dateFrom.getValue(), dateTo.getValue());
            showSuccess("Đã xuất CSV:\n" + csv.substring(0, Math.min(300, csv.length())) + "...");
        } catch (Exception e) {
            showError("Lỗi xuất CSV: " + e.getMessage());
        }
    }

    @FXML
    private void handleExportExcel() {
        showInfo("Tính năng xuất Excel sẽ được triển khai sau.");
    }

    @FXML
    private void handlePrint() {
        showInfo("Tính năng in báo cáo sẽ được triển khai sau.");
    }

    private void loadAllReports() {
        try {
            lblStatus.setText("Đang tải báo cáo...");
            
            LocalDate from = dateFrom.getValue();
            LocalDate to = dateTo.getValue();

            // Load Revenue Report
            loadRevenueReport(from, to);

            // Load Payment Methods Report
            loadPaymentMethodsReport(from, to);

            // Load Orders Statistics
            loadOrdersStatistics(from, to);

            // Load Top Products
            loadTopProducts(from, to);

            // Load Inventory Report
            loadInventoryReport();

            lblStatus.setText("Đã tải xong");
            
            // Update last update time
            String updateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            lblLastUpdate.setText("Cập nhật lần cuối: " + updateTime);
        } catch (Exception e) {
            showError("Lỗi tải báo cáo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadRevenueReport(LocalDate from, LocalDate to) {
        try {
            Map<String, Object> data = reportService.getRevenueByDateRange(from, to);
            
            BigDecimal total = (BigDecimal) data.get("totalRevenue");
            Long orders = (Long) data.get("totalOrders");
            BigDecimal avg = (BigDecimal) data.get("averageOrderValue");

            lblRevTotal.setText(formatMoney(total));
            lblRevOrders.setText(orders.toString());
            lblRevAverage.setText(formatMoney(avg));
        } catch (Exception e) {
            lblRevTotal.setText("0 đ");
            lblRevOrders.setText("0");
            lblRevAverage.setText("0 đ");
        }
    }

    private void loadPaymentMethodsReport(LocalDate from, LocalDate to) {
        try {
            Map<String, Object> paymentData = reportService.getRevenueByPaymentMethod(from, to);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> methodData = (List<Map<String, Object>>) paymentData.get("paymentMethodData");
            
            chartPaymentMethods.getData().clear();
            
            if (methodData != null && !methodData.isEmpty()) {
                for (Map<String, Object> method : methodData) {
                    String paymentMethod = (String) method.get("paymentMethod");
                    BigDecimal revenue = (BigDecimal) method.get("revenue");
                    Double percentage = (Double) method.get("percentage");
                    
                    if (paymentMethod != null && revenue != null) {
                        String translatedMethod = translatePaymentMethod(paymentMethod);
                        String label = translatedMethod + " (" + String.format("%.1f", percentage) + "%)";
                        PieChart.Data slice = new PieChart.Data(label, revenue.doubleValue());
                        chartPaymentMethods.getData().add(slice);
                    }
                }
            }
        } catch (Exception e) {
            chartPaymentMethods.getData().clear();
            e.printStackTrace();
        }
    }
    
    private String translatePaymentMethod(String method) {
        if (method == null) return "Không xác định";
        switch (method) {
            case "BANK_TRANSFER": return "Chuyển khoản";
            case "WALLET": return "Ví điện tử";
            case "COD": return "Thu hộ (COD)";
            case "GATEWAY": return "Cổng thanh toán";
            default: return method;
        }
    }

    private void loadOrdersStatistics(LocalDate from, LocalDate to) {
        try {
            Map<String, Object> orderStats = reportService.getOrderStatistics(from, to);
            
            Long total = (Long) orderStats.get("totalOrders");
            Long completed = (Long) orderStats.get("completedOrders");
            Long canceled = (Long) orderStats.get("canceledOrders");
            Long refunded = (Long) orderStats.get("refundedOrders");

            lblTotalOrders.setText(total.toString());
            lblCompletedOrders.setText(completed.toString());
            lblCanceledOrders.setText(canceled.toString());
            lblRefundedOrders.setText(refunded.toString());

            // Load order status chart
            chartOrderStatus.getData().clear();
            if (completed > 0) {
                chartOrderStatus.getData().add(new PieChart.Data("Hoàn thành: " + completed, completed));
            }
            if (canceled > 0) {
                chartOrderStatus.getData().add(new PieChart.Data("Đã hủy: " + canceled, canceled));
            }
            if (refunded > 0) {
                chartOrderStatus.getData().add(new PieChart.Data("Hoàn tiền: " + refunded, refunded));
            }
            Long pending = (Long) orderStats.get("pendingOrders");
            if (pending > 0) {
                chartOrderStatus.getData().add(new PieChart.Data("Đang xử lý: " + pending, pending));
            }
        } catch (Exception e) {
            lblTotalOrders.setText("0");
            lblCompletedOrders.setText("0");
            lblCanceledOrders.setText("0");
            lblRefundedOrders.setText("0");
            e.printStackTrace();
        }
    }

    private void loadTopProducts(LocalDate from, LocalDate to) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> productsList = (List<Map<String, Object>>) reportService.getTopSellingProducts(from, to, 10);
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Số lượng bán");
            
            for (Map<String, Object> product : productsList) {
                String name = (String) product.get("productName");
                Number qty = (Number) product.get("quantitySold");
                series.getData().add(new XYChart.Data<>(name, qty));
            }
            
            chartTopProducts.getData().clear();
            chartTopProducts.getData().add(series);
        } catch (Exception e) {
            chartTopProducts.getData().clear();
            e.printStackTrace();
        }
    }

    private void loadInventoryReport() {
        try {
            // Get inventory summary
            Map<String, Object> summary = reportService.getInventorySummary();
            Long totalItems = (Long) summary.get("totalItems");
            Long lowStockCount = (Long) summary.get("lowStockCount");
            
            lblTotalItems.setText(totalItems.toString());
            lblLowStockItems.setText(lowStockCount.toString());
            
            // Count out of stock items (onHand = 0)
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> lowStockItems = (List<Map<String, Object>>) reportService.getLowStockItems();
            long outOfStock = lowStockItems.stream()
                .filter(item -> {
                    Long onHand = (Long) item.get("onHand");
                    return onHand != null && onHand == 0;
                })
                .count();
            lblOutOfStockItems.setText(String.valueOf(outOfStock));
            
        } catch (Exception e) {
            lblTotalItems.setText("0");
            lblLowStockItems.setText("0");
            lblOutOfStockItems.setText("0");
            e.printStackTrace();
        }
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "0 đ";
        return String.format("%,.0f đ", amount);
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
