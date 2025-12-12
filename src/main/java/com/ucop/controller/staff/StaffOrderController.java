package com.ucop.controller.staff;

import com.ucop.entity.Order;
import com.ucop.service.OrderService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for Staff Order Management
 */
public class StaffOrderController {

    @FXML
    private TableView<Order> orderTable;

    @FXML
    private TableColumn<Order, String> orderNumberColumn;

    @FXML
    private TableColumn<Order, LocalDateTime> orderDateColumn;

    @FXML
    private TableColumn<Order, String> customerColumn;

    @FXML
    private TableColumn<Order, Order.OrderStatus> statusColumn;

    @FXML
    private TableColumn<Order, BigDecimal> totalColumn;

    @FXML
    private ComboBox<String> statusFilter;

    @FXML
    private TextField searchField;

    @FXML
    private TextArea orderDetailsArea;

    @FXML
    private Label totalOrdersLabel;

    @FXML
    private Button btnUpdateStatus;

    @FXML
    private Button btnCancelOrder;

    @FXML
    private ComboBox<String> statusUpdateCombo;

    private OrderService orderService;
    private ObservableList<Order> orders;

    @FXML
    public void initialize() {
        orders = FXCollections.observableArrayList();

        setupTableColumns();
        setupFilters();
        setupStatusUpdateOptions();
        setupEventHandlers();
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
        loadOrders();
    }

    private void setupTableColumns() {
        orderNumberColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("placedAt"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("shippingName"));
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
            "Tất cả", "Chờ xử lý", "Đã xác nhận", "Đang xử lý", "Đang giao", "Đã giao", "Đã hủy"
        ));
        statusFilter.setValue("Tất cả");
        statusFilter.setOnAction(e -> applyFilters());

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void setupStatusUpdateOptions() {
        statusUpdateCombo.setItems(FXCollections.observableArrayList(
            "Đã xác nhận", "Đang xử lý", "Đang giao", "Đã giao", "Đã hủy"
        ));
    }

    private void setupEventHandlers() {
        orderTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    displayOrderDetails(newSelection);
                    updateStatusUpdateButton(newSelection);
                }
            }
        );
    }

    private void loadOrders() {
        if (orderService == null) {
            return;
        }

        try {
            List<Order> orderList = orderService.getAllOrders();
            orders.setAll(orderList);
            orderTable.setItems(orders);
            applyFilters();
        } catch (Exception e) {
            showError("Lỗi tải danh sách đơn hàng: " + e.getMessage());
        }
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String status = statusFilter.getValue();

        ObservableList<Order> filtered = orders.filtered(order -> {
            boolean matches = true;

            // Search filter
            if (!searchText.isEmpty()) {
                matches = order.getOrderNumber().toLowerCase().contains(searchText) ||
                         (order.getShippingName() != null &&
                          order.getShippingName().toLowerCase().contains(searchText));
            }

            // Status filter
            if (!status.equals("Tất cả")) {
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
        details.append("Mã đơn: ").append(order.getOrderNumber()).append("\n\n");
        details.append("Ngày đặt: ").append(order.getPlacedAt()).append("\n");
        details.append("Trạng thái: ").append(translateStatus(order.getStatus())).append("\n\n");
        details.append("Thông tin khách hàng:\n");
        details.append("Tên: ").append(order.getShippingName()).append("\n");
        details.append("Điện thoại: ").append(order.getShippingPhone()).append("\n");
        details.append("Địa chỉ: ").append(order.getShippingAddress()).append("\n\n");

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            details.append("Chi tiết sản phẩm:\n");
            for (var item : order.getItems()) {
                details.append("- ").append(item.getItemName() != null ? item.getItemName() : "N/A")
                       .append(" (SL: ").append(item.getQuantity())
                       .append(", ĐG: ").append(String.format("%,.0f", item.getUnitPrice())).append(" VNĐ)\n");
            }
            details.append("\n");
        }

        details.append("Tổng tiền: ").append(String.format("%,.0f VNĐ", order.getGrandTotal()));

        orderDetailsArea.setText(details.toString());
    }

    private void updateStatusUpdateButton(Order order) {
        boolean canUpdate = order != null &&
                           (order.getStatus() == Order.OrderStatus.PENDING_PAYMENT ||
                            order.getStatus() == Order.OrderStatus.PAID ||
                            order.getStatus() == Order.OrderStatus.PACKED ||
                            order.getStatus() == Order.OrderStatus.SHIPPED);

        btnUpdateStatus.setDisable(!canUpdate);
        btnCancelOrder.setDisable(order == null ||
                                 order.getStatus() == Order.OrderStatus.CANCELED ||
                                 order.getStatus() == Order.OrderStatus.DELIVERED);
    }

    @FXML
    private void handleUpdateStatus() {
        Order selected = orderTable.getSelectionModel().getSelectedItem();
        String newStatusStr = statusUpdateCombo.getValue();

        if (selected == null) {
            showError("Vui lòng chọn đơn hàng!");
            return;
        }

        if (newStatusStr == null || newStatusStr.isEmpty()) {
            showError("Vui lòng chọn trạng thái mới!");
            return;
        }

        Order.OrderStatus newStatus = translateStatusReverse(newStatusStr);

        // Validate status transition
        if (!isValidStatusTransition(selected.getStatus(), newStatus)) {
            showError("Không thể chuyển trạng thái này!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Cập nhật trạng thái đơn hàng");
        confirm.setContentText("Bạn có chắc muốn cập nhật trạng thái đơn hàng thành '" + newStatusStr + "'?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    orderService.updateOrderStatus(selected.getId(), newStatus, 1L);
                    loadOrders();
                    showInfo("Cập nhật trạng thái thành công!");
                } catch (Exception e) {
                    showError("Lỗi cập nhật trạng thái: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleCancelOrder() {
        Order selected = orderTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Vui lòng chọn đơn hàng!");
            return;
        }

        if (selected.getStatus() == Order.OrderStatus.CANCELED) {
            showError("Đơn hàng đã bị hủy!");
            return;
        }

        if (selected.getStatus() == Order.OrderStatus.DELIVERED) {
            showError("Không thể hủy đơn hàng đã giao!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Hủy đơn hàng");
        confirm.setContentText("Bạn có chắc muốn hủy đơn hàng này?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    orderService.cancelOrder(selected.getId(), 1L);
                    loadOrders();
                    showInfo("Hủy đơn hàng thành công!");
                } catch (Exception e) {
                    showError("Lỗi hủy đơn hàng: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleRefresh() {
        loadOrders();
    }

    private boolean isValidStatusTransition(Order.OrderStatus current, Order.OrderStatus next) {
        switch (current) {
            case PENDING_PAYMENT:
                return next == Order.OrderStatus.PAID || next == Order.OrderStatus.CANCELED;
            case PAID:
                return next == Order.OrderStatus.PACKED || next == Order.OrderStatus.CANCELED;
            case PACKED:
                return next == Order.OrderStatus.SHIPPED || next == Order.OrderStatus.CANCELED;
            case SHIPPED:
                return next == Order.OrderStatus.DELIVERED;
            default:
                return false;
        }
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
            case "Đã xác nhận": return Order.OrderStatus.PAID;
            case "Đang xử lý": return Order.OrderStatus.PACKED;
            case "Đang giao": return Order.OrderStatus.SHIPPED;
            case "Đã giao": return Order.OrderStatus.DELIVERED;
            case "Đã hủy": return Order.OrderStatus.CANCELED;
            default: return Order.OrderStatus.PENDING_PAYMENT;
        }
    }

    private String getStatusStyle(Order.OrderStatus status) {
        if (status == null) return "";
        switch (status) {
            case DELIVERED:
            case CLOSED:
                return "-fx-text-fill: green; -fx-font-weight: bold;";
            case CANCELED:
                return "-fx-text-fill: red; -fx-font-weight: bold;";
            case SHIPPED:
                return "-fx-text-fill: blue; -fx-font-weight: bold;";
            case PAID:
            case PACKED:
                return "-fx-text-fill: orange; -fx-font-weight: bold;";
            default:
                return "-fx-text-fill: gray; -fx-font-weight: bold;";
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
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
}
