package com.ucop.controller.customer;

import com.ucop.entity.Order;
import com.ucop.service.OrderService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CustomerOrderController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private Label totalOrdersLabel;

    @FXML private TableView<Order> orderTable;

    @FXML private TableColumn<Order, String> orderNumberColumn;
    @FXML private TableColumn<Order, String> orderDateColumn;
    @FXML private TableColumn<Order, String> statusColumn;
    @FXML private TableColumn<Order, String> promoColumn;
    @FXML private TableColumn<Order, String> shipFeeColumn;
    @FXML private TableColumn<Order, String> totalColumn;
    @FXML private TableColumn<Order, String> noteColumn;

    private OrderService orderService;      // inject từ main
    private Long currentAccountId;          // inject từ dashboard/main

    private final ObservableList<Order> tableData = FXCollections.observableArrayList();

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat VND = NumberFormat.getInstance(new Locale("vi", "VN"));

    @FXML
    public void initialize() {
        // ✅ Không crash khi FXML thiếu cột (do chạy nhầm target/classes)
        setupColumnsSafe();
        setupFiltersSafe();

        if (orderTable != null) {
            orderTable.setItems(tableData);
        } else {
            System.out.println("[WARN] orderTable is NULL -> FXML đang không khớp fx:id hoặc đang chạy FXML cũ");
        }
    }

    // ===== Inject setters =====
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void setCurrentAccountId(Long accountId) {
        this.currentAccountId = accountId;
        loadOrders();
    }

    // ===== Actions =====
    @FXML
    public void loadOrders() {
        if (orderService == null) {
            warn("Thiếu OrderService (chưa inject).");
            return;
        }
        if (currentAccountId == null) {
            warn("Thiếu accountId (chưa inject).");
            return;
        }
        if (orderTable == null) {
            System.out.println("[WARN] orderTable is NULL -> FXML không khớp (hãy rebuild)");
            return;
        }

        List<Order> orders = orderService.getOrdersByAccountId(currentAccountId);
        if (orders == null) orders = Collections.emptyList();

        List<Order> filtered = orders.stream()
                .filter(o -> o.getStatus() != Order.OrderStatus.CART)
                .sorted(Comparator.comparing(this::getOrderTimeSafe).reversed())
                .collect(Collectors.toList());

        tableData.setAll(filtered);

        if (totalOrdersLabel != null) {
            totalOrdersLabel.setText("Tổng: " + filtered.size() + " đơn hàng");
        }
    }

    @FXML
    public void applyFilters() {
        if (orderService == null || currentAccountId == null) {
            loadOrders();
            return;
        }

        String kw = (searchField == null || searchField.getText() == null)
                ? ""
                : searchField.getText().trim().toLowerCase();

        String selectedStatusVN = (statusFilter == null) ? null : statusFilter.getValue();

        List<Order> orders = orderService.getOrdersByAccountId(currentAccountId);
        if (orders == null) orders = Collections.emptyList();

        List<Order> filtered = orders.stream()
                .filter(o -> o.getStatus() != Order.OrderStatus.CART)
                .filter(o -> kw.isEmpty() || safe(o.getOrderNumber()).toLowerCase().contains(kw))
                .filter(o -> matchStatusFilter(o, selectedStatusVN))
                .sorted(Comparator.comparing(this::getOrderTimeSafe).reversed())
                .collect(Collectors.toList());

        tableData.setAll(filtered);

        if (totalOrdersLabel != null) {
            totalOrdersLabel.setText("Tổng: " + filtered.size() + " đơn hàng");
        }
    }

    // ===== Safe setup =====
    private void setupColumnsSafe() {
        if (orderNumberColumn != null) {
            orderNumberColumn.setCellValueFactory(cd ->
                    new javafx.beans.property.SimpleStringProperty(safe(cd.getValue().getOrderNumber())));
        } else {
            System.out.println("[WARN] orderNumberColumn is NULL -> FXML cũ/chưa rebuild");
        }

        if (orderDateColumn != null) {
            orderDateColumn.setCellValueFactory(cd ->
                    new javafx.beans.property.SimpleStringProperty(formatTime(getOrderTimeSafe(cd.getValue()))));
        }

        if (statusColumn != null) {
            statusColumn.setCellValueFactory(cd ->
                    new javafx.beans.property.SimpleStringProperty(statusToVietnamese(cd.getValue())));
        }

        if (promoColumn != null) {
            promoColumn.setCellValueFactory(cd ->
                    new javafx.beans.property.SimpleStringProperty(safe(getPromotionCodeSafe(cd.getValue()))));
        }

        if (shipFeeColumn != null) {
            shipFeeColumn.setCellValueFactory(cd ->
                    new javafx.beans.property.SimpleStringProperty(formatMoney(getShippingFeeSafe(cd.getValue()))));
        }

        if (totalColumn != null) {
            totalColumn.setCellValueFactory(cd ->
                    new javafx.beans.property.SimpleStringProperty(formatMoney(getTotalSafe(cd.getValue()))));
        }

        if (noteColumn != null) {
            noteColumn.setCellValueFactory(cd ->
                    new javafx.beans.property.SimpleStringProperty(safe(getNotesSafe(cd.getValue()))));
        } else {
            System.out.println("[WARN] noteColumn is NULL -> FXML cũ/chưa rebuild");
        }
    }

    private void setupFiltersSafe() {
        if (statusFilter == null) {
            System.out.println("[WARN] statusFilter is NULL -> FXML không khớp fx:id");
            return;
        }

        statusFilter.setItems(FXCollections.observableArrayList(
                "Tất cả",
                "Chờ thanh toán",
                "Đã thanh toán",
                "Đã đặt hàng",
                "Chờ giao hàng",
                "Đã nhận hàng",
                "Từ chối nhận hàng",
                "Đã hủy"
        ));
        statusFilter.setValue("Tất cả");
    }

    private boolean matchStatusFilter(Order o, String selectedStatusVN) {
        if (selectedStatusVN == null || selectedStatusVN.equals("Tất cả")) return true;
        return statusToVietnamese(o).equalsIgnoreCase(selectedStatusVN);
    }

    // ✅ Không dùng REJECTED nữa
    private String statusToVietnamese(Order o) {
        boolean isShip = isShippingOrder(o);

        Order.OrderStatus st = o.getStatus();
        if (st == null) return "Không rõ";

        switch (st) {
            case PENDING_PAYMENT:
                return "Chờ thanh toán";
            case PAID:
                return "Đã thanh toán";
            case PLACED:
                return isShip ? "Chờ giao hàng" : "Đã đặt tại cửa hàng";
            case DELIVERED:
                return "Đã nhận hàng";
            case CANCELED:
                return isShip ? "Từ chối nhận hàng" : "Đã hủy";
            default:
                return st.name();
        }
    }

    private boolean isShippingOrder(Order o) {
        String addr = safe(getShippingAddressSafe(o));
        return !addr.isBlank();
    }

    private LocalDateTime getOrderTimeSafe(Order o) {
        LocalDateTime t = null;
        try { t = o.getPlacedAt(); } catch (Exception ignored) {}
        if (t == null) {
            try { t = o.getCreatedAt(); } catch (Exception ignored) {}
        }
        if (t == null) {
            try { t = o.getUpdatedAt(); } catch (Exception ignored) {}
        }
        return (t == null) ? LocalDateTime.now() : t;
    }

    private String formatTime(LocalDateTime t) {
        return (t == null) ? "" : DF.format(t);
    }

    private String formatMoney(BigDecimal v) {
        if (v == null) v = BigDecimal.ZERO;
        return VND.format(v) + " đ";
    }

    private BigDecimal getTotalSafe(Order o) {
        // 1) ưu tiên các field có sẵn trong bảng orders
        try {
            BigDecimal gt = o.getGrandTotal();
            if (gt != null && gt.compareTo(BigDecimal.ZERO) > 0) return gt;
        } catch (Exception ignored) {}

        try {
            BigDecimal due = o.getAmountDue();
            if (due != null && due.compareTo(BigDecimal.ZERO) > 0) return due;
        } catch (Exception ignored) {}

        try {
            BigDecimal sub = o.getSubtotal();
            if (sub != null && sub.compareTo(BigDecimal.ZERO) > 0) return sub;
        } catch (Exception ignored) {}

        // 2) FALLBACK: tính từ OrderItem (nếu orders.total/subtotal chưa được cập nhật)
        BigDecimal fromItems = calculateSubtotalFromItems(o);
        if (fromItems.compareTo(BigDecimal.ZERO) > 0) return fromItems;

        return BigDecimal.ZERO;
    }


    private BigDecimal getShippingFeeSafe(Order o) {
        try { return o.getShippingFee(); } catch (Exception ignored) {}
        return BigDecimal.ZERO;
    }

    private String getPromotionCodeSafe(Order o) {
        try { return o.getPromotionCode(); } catch (Exception ignored) {}
        return "";
    }

    private String getNotesSafe(Order o) {
        try { return o.getNotes(); } catch (Exception ignored) {}
        return "";
    }

    private String getShippingAddressSafe(Order o) {
        try { return o.getShippingAddress(); } catch (Exception ignored) {}
        return "";
    }

    private String safe(String s) {
        return (s == null) ? "" : s;
    }
    @SuppressWarnings("unchecked")
    private BigDecimal calculateSubtotalFromItems(Order o) {
        if (o == null) return BigDecimal.ZERO;

        try {
            // Order.items là LAZY, nếu session đóng có thể fail -> catch và trả 0
            var items = o.getItems();
            if (items == null || items.isEmpty()) return BigDecimal.ZERO;

            BigDecimal sum = BigDecimal.ZERO;

            for (Object it : items) {
                // cố lấy quantity
                int qty = 0;
                try {
                    Object q = it.getClass().getMethod("getQuantity").invoke(it);
                    if (q instanceof Number) qty = ((Number) q).intValue();
                } catch (Exception ignored) {}

                // cố lấy unitPrice
                BigDecimal unit = null;
                unit = tryBigDecimalGetter(it, "getUnitPrice");
                if (unit == null) unit = tryBigDecimalGetter(it, "getPrice");
                if (unit == null) unit = tryBigDecimalGetter(it, "getItemPrice");

                // hoặc lấy lineTotal/subtotal nếu có
                BigDecimal line = tryBigDecimalGetter(it, "getTotal");
                if (line == null) line = tryBigDecimalGetter(it, "getLineTotal");
                if (line == null) line = tryBigDecimalGetter(it, "getSubtotal");

                if (line != null && line.compareTo(BigDecimal.ZERO) > 0) {
                    sum = sum.add(line);
                } else if (unit != null && qty > 0) {
                    sum = sum.add(unit.multiply(BigDecimal.valueOf(qty)));
                }
            }

            return sum;

        } catch (Exception ignored) {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal tryBigDecimalGetter(Object obj, String method) {
        try {
            Object v = obj.getClass().getMethod(method).invoke(obj);
            if (v instanceof BigDecimal) return (BigDecimal) v;
            if (v instanceof Number) return BigDecimal.valueOf(((Number) v).doubleValue());
            return null;
        } catch (Exception ignored) {
            return null;
        }
    }


    private void warn(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Warning");
        a.setHeaderText("Warning");
        a.setContentText(msg);
        a.showAndWait();
    }
}
