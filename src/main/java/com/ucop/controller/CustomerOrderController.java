package com.ucop.controller;

import com.ucop.entity.Order;
import com.ucop.service.OrderService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for Customer Order Management
 */
public class CustomerOrderController {

    @FXML
    private TableView<Order> orderTable;

    @FXML
    private TableColumn<Order, String> orderNumberColumn;

    @FXML
    private TableColumn<Order, LocalDateTime> orderDateColumn;

    @FXML
    private TableColumn<Order, Order.OrderStatus> statusColumn;

    @FXML
    private TableColumn<Order, BigDecimal> totalColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> statusFilter;

    @FXML
    private TextArea orderDetailsArea;

    @FXML
    private Label totalOrdersLabel;

    private OrderService orderService;
    private Long currentAccountId;
    private ObservableList<Order> orders;

    @FXML
    public void initialize() {
        orders = FXCollections.observableArrayList();
        
        setupTableColumns();
        setupFilters();
        setupEventHandlers();
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void setAccountId(Long accountId) {
        this.currentAccountId = accountId;
        loadOrders();
    }

    private void setupTableColumns() {
        orderNumberColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("placedAt"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("grandTotal"));
        
        // Format status column
        statusColumn.setCellFactory(column -> new TableCell<Order, Order.OrderStatus>() {
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
        
        // Format total column
        totalColumn.setCellFactory(column -> new TableCell<Order, BigDecimal>() {
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
        statusFilter.setItems(FXCollections.observableArrayList(
            "Tất cả", "Chờ xử lý", "Đang giao", "Đã giao", "Đã hủy"
        ));
        statusFilter.setValue("Tất cả");
        statusFilter.setOnAction(e -> applyFilters());
        
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void setupEventHandlers() {
        orderTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    displayOrderDetails(newSelection);
                }
            }
        );
    }

    private void loadOrders() {
        if (orderService == null || currentAccountId == null) {
            return;
        }
        
        try {
            // Load orders for current account
            // List<Order> orderList = orderService.getOrdersByAccountId(currentAccountId);
            // orders.setAll(orderList);
            orderTable.setItems(orders);
            applyFilters();
        } catch (Exception e) {
            showError("Ỗi! Không thể tải danh sách đơn hàng: " + e.getMessage());
        }
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String status = statusFilter.getValue();
        
        ObservableList<Order> filtered = orders.filtered(order -> {
            boolean matches = true;
            
            // Search filter
            if (!searchText.isEmpty()) {
                matches = order.getOrderNumber().toLowerCase().contains(searchText);
            }
            
            // Status filter
            if (!status.equals("Ất cả")) {
                Order.OrderStatus translatedStatus = translateStatusReverse(status);
                matches = matches && order.getStatus().equals(translatedStatus);
            }
            
            return matches;
        });
        
        orderTable.setItems(filtered);
        totalOrdersLabel.setText("Tổng: " + filtered.size() + " đơn hàng");
    }

    private void displayOrderDetails(Order order) {
        StringBuilder details = new StringBuilder();
        details.append("Đơn hàng: ").append(order.getOrderNumber()).append("\n\n");
        details.append("Ngày đặt: ").append(order.getPlacedAt()).append("\n");
        details.append("Trạng thái: ").append(translateStatus(order.getStatus())).append("\n\n");
        details.append("Thông tin giao hàng:\n");
        details.append("Tên: ").append(order.getShippingName()).append("\n");
        details.append("Điện thoại: ").append(order.getShippingPhone()).append("\n");
        details.append("Địa chỉ: ").append(order.getShippingAddress()).append("\n\n");
        details.append("Tổng tiền: ").append(String.format("%,.0f VNĐ", order.getGrandTotal()));
        
        orderDetailsArea.setText(details.toString());
    }

    @FXML
    private void handleViewDetails() {
        Order selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Open detailed order view
        }
    }

    @FXML
    private void handleCancelOrder() {
        Order selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Vui lòng chọn đơn hàng cần hủy!");
            return;
        }
        
        if (!Order.OrderStatus.PENDING_PAYMENT.equals(selected.getStatus())) {
            showError("Chỉ có thể hủy đơn hàng đang chờ xử lý!");
            return;
        }
        
        // Confirm and cancel order
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Hủy đơn hàng");
        confirm.setContentText("Bạn có chắc muốn hủy đơn hàng này?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // orderService.cancelOrder(selected.getId());
                loadOrders();
            }
        });
    }

    private String translateStatus(Order.OrderStatus status) {
        if (status == null) return "";
        switch (status) {
            case PENDING_PAYMENT: return "Chờ xử lý";
            case PAID: return "Đã xác nhận";
            case PACKED: return "Đang xử lý";
            case SHIPPED: return "Đang giao";
            case DELIVERED: return "Đã giao";
            case CLOSED: return "Hoàn thành";
            case CANCELED: return "Đã hủy";
            default: return status.name();
        }
    }

    private Order.OrderStatus translateStatusReverse(String status) {
        switch (status) {
            case "Chờ xử lý": return Order.OrderStatus.PENDING_PAYMENT;
            case "Đang giao": return Order.OrderStatus.SHIPPED;
            case "Đã giao": return Order.OrderStatus.DELIVERED;
            case "Đã hủy": return Order.OrderStatus.CANCELED;
            default: return Order.OrderStatus.CART;
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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ỗi!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
