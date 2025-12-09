package com.ucop.service;

import javafx.scene.chart.*;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class DashboardService {
    private final ReportService reportService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM");

    public DashboardService(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Create revenue bar chart by date
     */
    public BarChart<String, Number> createRevenueBarChart(LocalDate startDate, LocalDate endDate) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Ngày");
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Doanh thu (VNĐ)");
        
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Doanh thu theo ngày");
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");
        
        Map<String, Object> revenueData = reportService.getRevenueByDateRange(startDate, endDate);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> dailyData = (List<Map<String, Object>>) revenueData.get("dailyData");
        
        if (dailyData != null) {
            for (Map<String, Object> day : dailyData) {
                String dateStr = day.get("date").toString();
                LocalDate date = LocalDate.parse(dateStr);
                BigDecimal revenue = (BigDecimal) day.get("revenue");
                if (revenue != null) {
                    series.getData().add(new XYChart.Data<>(date.format(dateFormatter), revenue.doubleValue()));
                }
            }
        }
        
        barChart.getData().add(series);
        return barChart;
    }

    /**
     * Create revenue line chart for trend analysis
     */
    public LineChart<String, Number> createRevenueLineChart(LocalDate startDate, LocalDate endDate) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Ngày");
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Doanh thu (VNĐ)");
        
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Xu hướng doanh thu");
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");
        
        Map<String, Object> revenueData = reportService.getRevenueByDateRange(startDate, endDate);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> dailyData = (List<Map<String, Object>>) revenueData.get("dailyData");
        
        if (dailyData != null) {
            for (Map<String, Object> day : dailyData) {
                String dateStr = day.get("date").toString();
                LocalDate date = LocalDate.parse(dateStr);
                BigDecimal revenue = (BigDecimal) day.get("revenue");
                if (revenue != null) {
                    series.getData().add(new XYChart.Data<>(date.format(dateFormatter), revenue.doubleValue()));
                }
            }
        }
        
        lineChart.getData().add(series);
        return lineChart;
    }

    /**
     * Create payment method pie chart
     */
    public PieChart createPaymentMethodPieChart(LocalDate startDate, LocalDate endDate) {
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Phương thức thanh toán");
        
        Map<String, Object> paymentData = reportService.getRevenueByPaymentMethod(startDate, endDate);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> methodData = (List<Map<String, Object>>) paymentData.get("paymentMethodData");
        
        if (methodData != null) {
            for (Map<String, Object> method : methodData) {
                String paymentMethod = (String) method.get("paymentMethod");
                BigDecimal revenue = (BigDecimal) method.get("revenue");
                Double percentage = (Double) method.get("percentage");
                
                if (paymentMethod != null && revenue != null) {
                    String translatedMethod = translatePaymentMethod(paymentMethod);
                    PieChart.Data slice = new PieChart.Data(
                        translatedMethod + " (" + String.format("%.1f", percentage) + "%)", 
                        revenue.doubleValue()
                    );
                    pieChart.getData().add(slice);
                }
            }
        }
        
        pieChart.setLegendVisible(true);
        return pieChart;
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

    /**
     * Create top products bar chart
     */
    public BarChart<String, Number> createTopProductsChart(LocalDate startDate, LocalDate endDate, int limit) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Sản phẩm");
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Số lượng bán");
        
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Top " + limit + " sản phẩm bán chạy");
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng");
        
        List<Map<String, Object>> topProducts = reportService.getTopSellingProducts(startDate, endDate, limit);
        
        for (Map<String, Object> product : topProducts) {
            String productName = (String) product.get("productName");
            if (productName == null) {
                productName = "SP-" + product.get("itemId");
            }
            Long quantity = (Long) product.get("quantitySold");
            if (quantity != null) {
                series.getData().add(new XYChart.Data<>(productName, quantity));
            }
        }
        
        barChart.getData().add(series);
        return barChart;
    }

    /**
     * Create order status pie chart
     */
    public PieChart createOrderStatusPieChart(LocalDate startDate, LocalDate endDate) {
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Trạng thái đơn hàng");
        
        Map<String, Object> orderStats = reportService.getOrderStatistics(startDate, endDate);
        @SuppressWarnings("unchecked")
        Map<String, Long> ordersByStatus = (Map<String, Long>) orderStats.get("ordersByStatus");
        
        if (ordersByStatus != null) {
            for (Map.Entry<String, Long> entry : ordersByStatus.entrySet()) {
                String status = translateStatus(entry.getKey());
                Long count = entry.getValue();
                
                if (count != null && count > 0) {
                    PieChart.Data slice = new PieChart.Data(
                        status + " (" + count + ")", 
                        count.doubleValue()
                    );
                    pieChart.getData().add(slice);
                }
            }
        }
        
        pieChart.setLegendVisible(true);
        return pieChart;
    }

    /**
     * Create complete dashboard with all charts
     */
    public VBox createDashboard(LocalDate startDate, LocalDate endDate) {
        VBox dashboard = new VBox(20);
        
        // Add revenue charts
        dashboard.getChildren().add(createRevenueBarChart(startDate, endDate));
        dashboard.getChildren().add(createRevenueLineChart(startDate, endDate));
        
        // Add payment method pie chart
        dashboard.getChildren().add(createPaymentMethodPieChart(startDate, endDate));
        
        // Add top products chart
        dashboard.getChildren().add(createTopProductsChart(startDate, endDate, 10));
        
        // Add order status pie chart
        dashboard.getChildren().add(createOrderStatusPieChart(startDate, endDate));
        
        return dashboard;
    }

    /**
     * Get summary statistics for dashboard header
     */
    public Map<String, Object> getDashboardSummary(LocalDate startDate, LocalDate endDate) {
        return reportService.getDashboardData(startDate, endDate);
    }

    private String translateStatus(String status) {
        switch (status) {
            case "PENDING": return "Chờ xử lý";
            case "CONFIRMED": return "Đã xác nhận";
            case "PROCESSING": return "Đang xử lý";
            case "SHIPPED": return "Đã giao vận";
            case "DELIVERED": return "Đã giao hàng";
            case "COMPLETED": return "Hoàn thành";
            case "CANCELED": return "Đã hủy";
            case "REFUNDED": return "Đã hoàn tiền";
            default: return status;
        }
    }
}
