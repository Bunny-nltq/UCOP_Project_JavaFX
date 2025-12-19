package com.ucop.controller.customer;

import com.ucop.entity.Order;
import com.ucop.service.OrderService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CustomerOrderController {

    @FXML private TableView<Order> orderTable;

    @FXML private TableColumn<Order, String> orderNumberColumn;
    @FXML private TableColumn<Order, LocalDateTime> orderDateColumn;
    @FXML private TableColumn<Order, Order.OrderStatus> statusColumn;
    @FXML private TableColumn<Order, BigDecimal> totalColumn;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;

    @FXML private VBox orderDetailsBox;      // ✅ FIX: map với fx:id bên FXML
    @FXML private TextArea orderDetailsArea;

    @FXML private Label totalOrdersLabel;

    private OrderService orderService;
    private Long currentAccountId;
    private final ObservableList<Order> orders = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupFilters();
        setupEventHandlers();

        // trạng thái ban đầu
        if (orderDetailsBox != null) {
            orderDetailsBox.setVisible(false);
            orderDetailsBox.setManaged(false);
        }
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void setCurrentAccountId(Long currentAccountId) {
        this.currentAccountId = currentAccountId;
        loadOrders();
    }

    private void setupTableColumns() {
        orderNumberColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("placedAt"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("grandTotal"));

        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Order.OrderStatus status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(translateStatus(status));
                    setStyle(getStatusStyle(status));
                }
            }
        });

        totalColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f VNĐ", total));
                }
            }
        });
    }

    private void setupFilters() {
        // ✅ Cho khớp với UI của bạn
        statusFilter.setItems(FXCollections.observableArrayList(
                "Tất cả",
                "Chờ xử lý",
                "Đã xác nhận",
                "Đang xử lý",
                "Đang giao",
                "Đã giao",
                "Hoàn thành",
                "Đã hủy"
        ));
        statusFilter.setValue("Tất cả");

        statusFilter.setOnAction(e -> applyFilters());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void setupEventHandlers() {
        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                displayOrderDetails(newSel);
                if (orderDetailsBox != null) {
                    orderDetailsBox.setVisible(true);
                    orderDetailsBox.setManaged(true);
                }
            }
        });
    }

    @FXML
    private void loadOrders() {
        if (orderService == null || currentAccountId == null) return;

        try {
            List<Order> orderList = orderService.getOrdersByAccountId(currentAccountId);
            orders.setAll(orderList);
            orderTable.setItems(orders);

            // reset details
            if (orderDetailsArea != null) orderDetailsArea.clear();
            if (orderDetailsBox != null) {
                orderDetailsBox.setVisible(false);
                orderDetailsBox.setManaged(false);
            }

            applyFilters();
        } catch (Exception e) {
            showError("Lỗi! Không thể tải danh sách đơn hàng: " + e.getMessage());
        }
    }

    // ✅ FIX: phải có @FXML vì FXML gọi onAction="#applyFilters"
    @FXML
    private void applyFilters() {
        String searchText = (searchField != null && searchField.getText() != null)
                ? searchField.getText().trim().toLowerCase()
                : "";

        String statusText = (statusFilter != null && statusFilter.getValue() != null)
                ? statusFilter.getValue()
                : "Tất cả";

        ObservableList<Order> filtered = orders.filtered(order -> {
            boolean matches = true;

            if (!searchText.isEmpty()) {
                String on = (order.getOrderNumber() == null) ? "" : order.getOrderNumber().toLowerCase();
                matches = on.contains(searchText);
            }

            if (matches && !"Tất cả".equals(statusText)) {
                Order.OrderStatus want = translateStatusReverse(statusText);
                matches = (want != null) && want.equals(order.getStatus());
            }

            return matches;
        });

        orderTable.setItems(filtered);

        if (totalOrdersLabel != null) {
            totalOrdersLabel.setText("Tổng: " + filtered.size() + " đơn hàng");
        }
    }

    private void displayOrderDetails(Order order) {
        StringBuilder details = new StringBuilder();
        details.append("Đơn hàng: ").append(nullSafe(order.getOrderNumber())).append("\n\n");
        details.append("Ngày đặt: ").append(order.getPlacedAt()).append("\n");
        details.append("Trạng thái: ").append(translateStatus(order.getStatus())).append("\n\n");

        details.append("Thông tin giao hàng:\n");
        details.append("Tên: ").append(nullSafe(order.getShippingName())).append("\n");
        details.append("Điện thoại: ").append(nullSafe(order.getShippingPhone())).append("\n");
        details.append("Địa chỉ: ").append(nullSafe(order.getShippingAddress())).append("\n\n");

        if (order.getGrandTotal() != null) {
            details.append("Tổng tiền: ").append(String.format("%,.0f VNĐ", order.getGrandTotal()));
        } else {
            details.append("Tổng tiền: 0 VNĐ");
        }

        orderDetailsArea.setText(details.toString());
    }

    private String translateStatus(Order.OrderStatus status) {
        if (status == null) return "";
        switch (status) {
            case PENDING_PAYMENT: return "Chờ xử lý";
            case PAID:           return "Đã xác nhận";
            case PACKED:         return "Đang xử lý";
            case SHIPPED:        return "Đang giao";
            case DELIVERED:      return "Đã giao";
            case CLOSED:         return "Hoàn thành";
            case CANCELED:       return "Đã hủy";
            default:             return status.name();
        }
    }

    private Order.OrderStatus translateStatusReverse(String status) {
        switch (status) {
            case "Chờ xử lý":   return Order.OrderStatus.PENDING_PAYMENT;
            case "Đã xác nhận": return Order.OrderStatus.PAID;
            case "Đang xử lý":  return Order.OrderStatus.PACKED;
            case "Đang giao":   return Order.OrderStatus.SHIPPED;
            case "Đã giao":     return Order.OrderStatus.DELIVERED;
            case "Hoàn thành":  return Order.OrderStatus.CLOSED;
            case "Đã hủy":      return Order.OrderStatus.CANCELED;
            default:            return null; // "Tất cả" hoặc không hợp lệ
        }
    }

    private String getStatusStyle(Order.OrderStatus status) {
        if (status == null) return "";
        switch (status) {
            case DELIVERED:
            case CLOSED:
                return "-fx-text-fill: green; -fx-font-weight: bold;";
            case CANCELED:
            case REFUNDED:
                return "-fx-text-fill: red; -fx-font-weight: bold;";
            case SHIPPED:
                return "-fx-text-fill: blue; -fx-font-weight: bold;";
            default:
                return "-fx-text-fill: orange; -fx-font-weight: bold;";
        }
    }

    private String nullSafe(String s) {
        return (s == null) ? "" : s;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
