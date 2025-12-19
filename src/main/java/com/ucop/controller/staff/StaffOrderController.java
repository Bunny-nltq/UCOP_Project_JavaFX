package com.ucop.controller.staff;

import com.ucop.entity.Order;
import com.ucop.service.OrderService;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StaffOrderController {

    // ====== FORM (top) ======
    @FXML private TextField txtOrderNumber;
    @FXML private TextField txtAccountId;
    @FXML private ComboBox<String> cbStatus;
    @FXML private TextField txtSubtotal;
    @FXML private TextField txtGrandTotal;
    @FXML private TextField txtPlacedAt;

    @FXML private TextField txtDiscountCode;   // form

    @FXML private TextArea txtNote;

    // ====== TABLE (center) ======
    @FXML private TableView<Order> tblOrders;

    @FXML private TableColumn<Order, Long> colId;
    @FXML private TableColumn<Order, String> colOrderNumber;
    @FXML private TableColumn<Order, Long> colAccountId;
    @FXML private TableColumn<Order, Order.OrderStatus> colStatus;
    @FXML private TableColumn<Order, BigDecimal> colSubtotal;
    @FXML private TableColumn<Order, BigDecimal> colGrandTotal;

    // ✅ NEW column in table
    @FXML private TableColumn<Order, String> colDiscountCode;

    @FXML private TableColumn<Order, Object> colPlacedAt;

    private final ObservableList<Order> orders = FXCollections.observableArrayList();

    private OrderService orderService;
    private boolean uiReady = false;

    @FXML
    public void initialize() {
        cbStatus.setItems(FXCollections.observableArrayList(
                "Chờ xử lý", "Đã xác nhận", "Đang xử lý", "Đang giao", "Đã giao", "Đã hủy"
        ));
        cbStatus.getSelectionModel().selectFirst();

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colOrderNumber.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        colAccountId.setCellValueFactory(new PropertyValueFactory<>("accountId"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colGrandTotal.setCellValueFactory(new PropertyValueFactory<>("grandTotal"));

        // ✅ Discount code column: lấy từ entity bằng reflection
        if (colDiscountCode != null) {
            colDiscountCode.setCellValueFactory(cell ->
                    new ReadOnlyStringWrapper(safe(extractDiscountCode(cell.getValue())))
            );
        }

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Order.OrderStatus st, boolean empty) {
                super.updateItem(st, empty);
                setText(empty || st == null ? null : translateStatus(st));
            }
        });

        colSubtotal.setCellFactory(col -> moneyCell());
        colGrandTotal.setCellFactory(col -> moneyCell());

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        colPlacedAt.setCellValueFactory(new PropertyValueFactory<>("placedAt"));

        colPlacedAt.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Object t, boolean empty) {
                super.updateItem(t, empty);
                if (empty || t == null) {
                    setText(null);
                    return;
                }

                // support LocalDateTime / java.util.Date / java.sql.Timestamp
                try {
                    if (t instanceof java.time.LocalDateTime ldt) {
                        setText(ldt.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        return;
                    }
                    if (t instanceof java.sql.Timestamp ts) {
                        setText(ts.toLocalDateTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        return;
                    }
                    if (t instanceof java.util.Date d) {
                        java.time.LocalDateTime ldt = java.time.LocalDateTime.ofInstant(
                                d.toInstant(), java.time.ZoneId.systemDefault()
                        );
                        setText(ldt.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        return;
                    }

                    setText(t.toString());
                } catch (Exception ex) {
                    setText(t.toString());
                }
            }
        });


        tblOrders.setItems(orders);

        // Click row -> fill form
        tblOrders.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) fillForm(newV);
        });

        uiReady = true;

        if (orderService != null) {
            Platform.runLater(this::loadOrders);
        }
    }

    // Inject service từ StaffDashboard. Inject xong -> tự load DB
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
        if (uiReady) {
            Platform.runLater(this::loadOrders);
        }
    }

    private TableCell<Order, BigDecimal> moneyCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setText(null); return; }
                BigDecimal val = (v == null) ? BigDecimal.ZERO : v;
                setText(String.format("%,.0f đ", val).replace(',', '.'));
            }
        };
    }

    // ====== Buttons ======
    @FXML
    private void loadOrders() {
        if (orderService == null) {
            showError("Thiếu OrderService (chưa inject).");
            return;
        }
        try {
            List<Order> list = orderService.getAllOrders();
            orders.setAll(list);

            if (!orders.isEmpty()) {
                tblOrders.getSelectionModel().selectFirst(); // auto fill form
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Lỗi tải đơn hàng: " + ex.getMessage());
        }
    }

    @FXML
    private void updateStatus() {
        if (orderService == null) { showError("Thiếu OrderService (chưa inject)."); return; }

        Order selected = tblOrders.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Vui lòng chọn 1 đơn hàng trong bảng."); return; }

        String statusVN = cbStatus.getValue();
        if (statusVN == null || statusVN.isBlank()) { showError("Vui lòng chọn trạng thái."); return; }

        Order.OrderStatus newStatus = translateStatusReverse(statusVN);

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Cập nhật trạng thái");
        confirm.setContentText("Cập nhật đơn " + safe(selected.getOrderNumber()) + " sang '" + statusVN + "' ?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    orderService.updateOrderStatus(selected.getId(), newStatus, 1L);
                    loadOrders();
                    showInfo("Cập nhật trạng thái thành công!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Lỗi cập nhật trạng thái: " + ex.getMessage());
                }
            }
        });
    }

    @FXML
    private void cancelOrder() {
        if (orderService == null) { showError("Thiếu OrderService (chưa inject)."); return; }

        Order selected = tblOrders.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Vui lòng chọn 1 đơn hàng trong bảng."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Hủy đơn hàng");
        confirm.setContentText("Bạn có chắc muốn hủy đơn " + safe(selected.getOrderNumber()) + " ?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    orderService.cancelOrder(selected.getId(), 1L);
                    loadOrders();
                    showInfo("Hủy đơn hàng thành công!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Lỗi hủy đơn: " + ex.getMessage());
                }
            }
        });
    }

    // ====== Fill form ======
    private void fillForm(Order o) {
        txtOrderNumber.setText(safe(o.getOrderNumber()));
        txtAccountId.setText(o.getAccountId() == null ? "" : String.valueOf(o.getAccountId()));
        txtSubtotal.setText(formatMoney(o.getSubtotal()));
        txtGrandTotal.setText(formatMoney(o.getGrandTotal()));
        txtPlacedAt.setText(o.getPlacedAt() == null ? "" : o.getPlacedAt().toString());
        txtDiscountCode.setText(safe(extractDiscountCode(o)));
        txtNote.setText("");
        cbStatus.setValue(translateStatus(o.getStatus()));
    }

    private String extractDiscountCode(Order o) {
        String[] getters = {
                "getDiscountCode",
                "getCouponCode",
                "getVoucherCode",
                "getPromoCode",
                "getPromotionCode"
        };
        for (String g : getters) {
            try {
                Method m = o.getClass().getMethod(g);
                Object v = m.invoke(o);
                if (v != null) return v.toString();
            } catch (Exception ignored) {}
        }
        return "";
    }

    private String formatMoney(BigDecimal v) {
        BigDecimal val = (v == null) ? BigDecimal.ZERO : v;
        return String.format("%,.0f đ", val).replace(',', '.');
    }

    private String translateStatus(Order.OrderStatus status) {
        if (status == null) return "Chờ xử lý";
        return switch (status) {
            case PENDING_PAYMENT -> "Chờ xử lý";
            case PAID -> "Đã xác nhận";
            case PACKED -> "Đang xử lý";
            case SHIPPED -> "Đang giao";
            case DELIVERED -> "Đã giao";
            case CANCELED -> "Đã hủy";
            case CLOSED -> "Đã giao";
            default -> "Chờ xử lý";
        };
    }

    private Order.OrderStatus translateStatusReverse(String statusVN) {
        if (statusVN == null) return Order.OrderStatus.PENDING_PAYMENT;
        return switch (statusVN) {
            case "Chờ xử lý" -> Order.OrderStatus.PENDING_PAYMENT;
            case "Đã xác nhận" -> Order.OrderStatus.PAID;
            case "Đang xử lý" -> Order.OrderStatus.PACKED;
            case "Đang giao" -> Order.OrderStatus.SHIPPED;
            case "Đã giao" -> Order.OrderStatus.DELIVERED;
            case "Đã hủy" -> Order.OrderStatus.CANCELED;
            default -> Order.OrderStatus.PENDING_PAYMENT;
        };
    }

    private String safe(String s) { return s == null ? "" : s; }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Thông báo");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Lỗi");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
