package com.ucop.service;

import com.ucop.entity.Order;
import com.ucop.entity.OrderItem;
import com.ucop.entity.Payment;
import com.ucop.entity.StockItem;
import com.ucop.repository.OrderRepository;
import com.ucop.repository.PaymentRepository;
import com.ucop.repository.StockItemRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {
    private final SessionFactory sessionFactory;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final StockItemRepository stockItemRepository;

    public ReportService(SessionFactory sessionFactory, OrderRepository orderRepository,
                        PaymentRepository paymentRepository, StockItemRepository stockItemRepository) {
        this.sessionFactory = sessionFactory;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.stockItemRepository = stockItemRepository;
    }

    // Revenue Reports
    public Map<String, Object> getRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            Query<Object[]> query = session.createQuery(
                "SELECT DATE(o.placedAt), SUM(o.grandTotal), COUNT(o) " +
                "FROM Order o " +
                "WHERE o.status IN (com.ucop.entity.Order$OrderStatus.DELIVERED, com.ucop.entity.Order$OrderStatus.CLOSED) " +
                "AND o.placedAt BETWEEN :start AND :end " +
                "GROUP BY DATE(o.placedAt) " +
                "ORDER BY DATE(o.placedAt)",
                Object[].class
            );
            query.setParameter("start", startDateTime);
            query.setParameter("end", endDateTime);

            List<Object[]> results = query.list();
            
            Map<String, Object> report = new HashMap<>();
            List<Map<String, Object>> dailyData = new ArrayList<>();
            BigDecimal totalRevenue = BigDecimal.ZERO;
            long totalOrders = 0;

            for (Object[] row : results) {
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", row[0]);
                dayData.put("revenue", row[1]);
                dayData.put("orderCount", row[2]);
                dailyData.add(dayData);

                totalRevenue = totalRevenue.add((BigDecimal) row[1]);
                totalOrders += (Long) row[2];
            }

            report.put("dailyData", dailyData);
            report.put("totalRevenue", totalRevenue);
            report.put("totalOrders", totalOrders);
            report.put("averageOrderValue", totalOrders > 0 ? 
                totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP) : 
                BigDecimal.ZERO);

            return report;
        }
    }

    public Map<String, Object> getRevenueByMonth(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        return getRevenueByDateRange(startDate, endDate);
    }

    public Map<String, Object> getRevenueByPaymentMethod(LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            Query<Object[]> query = session.createQuery(
                "SELECT CAST(p.paymentMethod AS string), SUM(p.amount), COUNT(p) " +
                "FROM Payment p " +
                "WHERE p.createdAt BETWEEN :start AND :end " +
                "GROUP BY p.paymentMethod " +
                "ORDER BY SUM(p.amount) DESC",
                Object[].class
            );
            query.setParameter("start", startDateTime);
            query.setParameter("end", endDateTime);

            List<Object[]> results = query.list();
            
            Map<String, Object> report = new HashMap<>();
            List<Map<String, Object>> paymentData = new ArrayList<>();
            BigDecimal totalRevenue = BigDecimal.ZERO;

            for (Object[] row : results) {
                Map<String, Object> methodData = new HashMap<>();
                methodData.put("paymentMethod", row[0]);
                methodData.put("revenue", row[1]);
                methodData.put("count", row[2]);
                paymentData.add(methodData);

                totalRevenue = totalRevenue.add((BigDecimal) row[1]);
            }

            // Calculate percentages
            for (Map<String, Object> methodData : paymentData) {
                BigDecimal revenue = (BigDecimal) methodData.get("revenue");
                double percentage = totalRevenue.compareTo(BigDecimal.ZERO) > 0 ?
                    revenue.divide(totalRevenue, 4, BigDecimal.ROUND_HALF_UP)
                          .multiply(BigDecimal.valueOf(100)).doubleValue() : 0;
                methodData.put("percentage", percentage);
            }

            report.put("paymentMethodData", paymentData);
            report.put("totalRevenue", totalRevenue);

            return report;
        }
    }

    // Order Reports
    public Map<String, Object> getOrderStatistics(LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            // Total orders
            Query<Long> totalQuery = session.createQuery(
                "SELECT COUNT(o) FROM Order o WHERE o.placedAt BETWEEN :start AND :end",
                Long.class
            );
            totalQuery.setParameter("start", startDateTime);
            totalQuery.setParameter("end", endDateTime);
            long totalOrders = totalQuery.uniqueResult();

            // Orders by status
            Query<Object[]> statusQuery = session.createQuery(
                "SELECT CAST(o.status AS string), COUNT(o) FROM Order o " +
                "WHERE o.placedAt BETWEEN :start AND :end " +
                "GROUP BY o.status",
                Object[].class
            );
            statusQuery.setParameter("start", startDateTime);
            statusQuery.setParameter("end", endDateTime);
            List<Object[]> statusResults = statusQuery.list();

            Map<String, Object> report = new HashMap<>();
            report.put("totalOrders", totalOrders);

            Map<String, Long> ordersByStatus = new HashMap<>();
            for (Object[] row : statusResults) {
                ordersByStatus.put((String) row[0], (Long) row[1]);
            }
            report.put("ordersByStatus", ordersByStatus);

            // Specific counts
            report.put("completedOrders", ordersByStatus.getOrDefault("CLOSED", 0L) + ordersByStatus.getOrDefault("DELIVERED", 0L));
            report.put("canceledOrders", ordersByStatus.getOrDefault("CANCELED", 0L));
            report.put("refundedOrders", ordersByStatus.getOrDefault("REFUNDED", 0L));
            report.put("pendingOrders", ordersByStatus.getOrDefault("PLACED", 0L) + ordersByStatus.getOrDefault("PENDING_PAYMENT", 0L) + ordersByStatus.getOrDefault("PAID", 0L));

            return report;
        }
    }

    // Product Reports
    public List<Map<String, Object>> getTopSellingProducts(LocalDate startDate, LocalDate endDate, int limit) {
        try (Session session = sessionFactory.openSession()) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            Query<Object[]> query = session.createQuery(
                "SELECT oi.itemId, SUM(oi.quantity), SUM(oi.quantity * oi.unitPrice), COUNT(DISTINCT o.id) " +
                "FROM OrderItem oi JOIN oi.order o " +
                "WHERE o.status IN (com.ucop.entity.Order$OrderStatus.DELIVERED, com.ucop.entity.Order$OrderStatus.CLOSED) " +
                "AND o.placedAt BETWEEN :start AND :end " +
                "GROUP BY oi.itemId " +
                "ORDER BY SUM(oi.quantity) DESC",
                Object[].class
            );
            query.setParameter("start", startDateTime);
            query.setParameter("end", endDateTime);
            query.setMaxResults(limit);

            List<Object[]> results = query.list();
            List<Map<String, Object>> topProducts = new ArrayList<>();

            for (Object[] row : results) {
                Map<String, Object> product = new HashMap<>();
                product.put("itemId", row[0]);
                product.put("productName", "Sản phẩm #" + row[0]); // Since we don't have product catalog
                product.put("quantitySold", row[1]);
                product.put("totalRevenue", row[2]);
                product.put("orderCount", row[3]);
                topProducts.add(product);
            }

            return topProducts;
        }
    }

    // Inventory Reports
    public List<Map<String, Object>> getLowStockItems() {
        try (Session session = sessionFactory.openSession()) {
            Query<StockItem> query = session.createQuery(
                "FROM StockItem si WHERE si.isLowStock = true OR si.onHand <= si.lowStockThreshold " +
                "ORDER BY si.onHand ASC",
                StockItem.class
            );

            List<StockItem> stockItems = query.list();
            List<Map<String, Object>> lowStockReport = new ArrayList<>();

            for (StockItem item : stockItems) {
                Map<String, Object> stockData = new HashMap<>();
                stockData.put("itemId", item.getItemId());
                stockData.put("warehouseId", item.getWarehouse().getId());
                stockData.put("warehouseName", item.getWarehouse().getName());
                stockData.put("onHand", item.getOnHand());
                stockData.put("reserved", item.getReserved());
                stockData.put("available", item.getAvailable());
                stockData.put("lowStockThreshold", item.getLowStockThreshold());
                stockData.put("quantity", item.getOnHand()); // For backward compatibility
                stockData.put("unitPrice", BigDecimal.ZERO); // Default price, should be fetched from product catalog
                lowStockReport.add(stockData);
            }

            return lowStockReport;
        }
    }

    public Map<String, Object> getInventorySummary() {
        try (Session session = sessionFactory.openSession()) {
            Query<Object[]> query = session.createQuery(
                "SELECT COUNT(si), SUM(si.onHand), SUM(si.reserved), " +
                "SUM(CASE WHEN si.isLowStock = true THEN 1 ELSE 0 END) " +
                "FROM StockItem si",
                Object[].class
            );

            Object[] result = query.uniqueResult();
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalItems", result[0]);
            summary.put("totalOnHand", result[1]);
            summary.put("totalReserved", result[2]);
            summary.put("lowStockCount", result[3]);
            summary.put("totalAvailable", 
                ((Long) result[1]).longValue() - ((Long) result[2]).longValue());

            return summary;
        }
    }

    // Comprehensive Dashboard Data
    public Map<String, Object> getDashboardData(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> dashboard = new HashMap<>();
        
        dashboard.put("revenueData", getRevenueByDateRange(startDate, endDate));
        dashboard.put("paymentMethodData", getRevenueByPaymentMethod(startDate, endDate));
        dashboard.put("orderStatistics", getOrderStatistics(startDate, endDate));
        dashboard.put("topProducts", getTopSellingProducts(startDate, endDate, 10));
        dashboard.put("inventorySummary", getInventorySummary());
        dashboard.put("lowStockItems", getLowStockItems());
        
        return dashboard;
    }

    // Export to CSV (simplified version)
    public String exportRevenueReportToCSV(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = getRevenueByDateRange(startDate, endDate);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> dailyData = (List<Map<String, Object>>) report.get("dailyData");
        
        StringBuilder csv = new StringBuilder();
        csv.append("Date,Revenue,Order Count\n");
        
        for (Map<String, Object> day : dailyData) {
            csv.append(day.get("date")).append(",")
               .append(day.get("revenue")).append(",")
               .append(day.get("orderCount")).append("\n");
        }
        
        return csv.toString();
    }
}
