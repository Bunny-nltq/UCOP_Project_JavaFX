package com.ucop.controller;

import com.ucop.entity.Cart;
import com.ucop.entity.Order;
import com.ucop.entity.Promotion;
import com.ucop.service.OrderService;
import com.ucop.service.PromotionService;
import com.ucop.dto.PromotionApplyResultDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for applying promotion codes to cart or order
 */
public class ApplyPromotionController {

    @FXML private TextField txtPromotionCode;
    @FXML private Button btnApply;
    @FXML private Button btnClose;
    @FXML private Label lblCartTotal;
    @FXML private Label lblDiscount;
    @FXML private Label lblFinalTotal;
    @FXML private Label lblMessage;
    @FXML private TableView<Promotion> availablePromotionsTable;
    @FXML private TableColumn<Promotion, String> codeColumn;
    @FXML private TableColumn<Promotion, String> descriptionColumn;
    @FXML private TableColumn<Promotion, String> typeColumn;
    @FXML private TableColumn<Promotion, LocalDateTime> expiryColumn;

    private PromotionService promotionService;
    private OrderService orderService;
    private Cart currentCart;
    private Order currentOrder;
    private Long currentAccountId;
    private ObservableList<Promotion> availablePromotions;
    private BigDecimal currentTotal = BigDecimal.ZERO;
    private BigDecimal discountAmount = BigDecimal.ZERO;
    private String appliedPromotionCode = null;

    @FXML
    public void initialize() {
        availablePromotions = FXCollections.observableArrayList();
        setupTable();
    }

    /**
     * Set services
     */
    public void setServices(PromotionService promotionService, OrderService orderService) {
        this.promotionService = promotionService;
        this.orderService = orderService;
    }

    /**
     * Set cart to apply promotion
     */
    public void setCart(Cart cart, Long accountId) {
        this.currentCart = cart;
        this.currentAccountId = accountId;
        calculateCartTotal();
        loadAvailablePromotions();
    }

    /**
     * Set order to apply promotion
     */
    public void setOrder(Order order, Long accountId) {
        this.currentOrder = order;
        this.currentAccountId = accountId;
        this.currentTotal = order.getSubtotal();
        updateTotalDisplay();
        loadAvailablePromotions();
    }

    /**
     * Setup table columns
     */
    private void setupTable() {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        expiryColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        // Format type column
        typeColumn.setCellFactory(column -> new TableCell<Promotion, String>() {
            @Override
            protected void updateItem(String type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setText(null);
                } else {
                    setText(translatePromotionType(type));
                }
            }
        });

        // Double click to select promotion
        availablePromotionsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Promotion selected = availablePromotionsTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    txtPromotionCode.setText(selected.getCode());
                    handleApply();
                }
            }
        });

        availablePromotionsTable.setItems(availablePromotions);
    }

    /**
     * Calculate cart total
     */
    private void calculateCartTotal() {
        if (currentCart == null || currentCart.getItems().isEmpty()) {
            currentTotal = BigDecimal.ZERO;
        } else {
            currentTotal = currentCart.getItems().stream()
                    .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        updateTotalDisplay();
    }

    /**
     * Load available promotions
     */
    private void loadAvailablePromotions() {
        if (promotionService == null) return;

        try {
            List<Promotion> promotions = promotionService.getActivePromotions();
            availablePromotions.setAll(promotions);
        } catch (Exception e) {
            showError("Không thể tải danh sách mã giảm giá: " + e.getMessage());
        }
    }

    /**
     * Handle apply promotion
     */
    @FXML
    private void handleApply() {
        String code = txtPromotionCode.getText().trim();
        
        if (code.isEmpty()) {
            showMessage("Vui lòng nhập mã giảm giá!", "error");
            return;
        }

        if (promotionService == null) {
            showMessage("Dịch vụ không khả dụng!", "error");
            return;
        }

        try {
            // Apply promotion
            PromotionApplyResultDTO result = promotionService.applyPromotion(
                code, currentAccountId, currentTotal, null
            );

            if (result.isSuccess()) {
                discountAmount = result.getDiscountAmount();
                appliedPromotionCode = code;
                updateTotalDisplay();
                showMessage("Áp dụng mã giảm giá thành công! Giảm " + formatPrice(discountAmount), "success");
            } else {
                showMessage(result.getMessage(), "error");
            }
        } catch (Exception e) {
            showMessage("Lỗi khi áp dụng mã: " + e.getMessage(), "error");
        }
    }

    /**
     * Handle remove promotion
     */
    @FXML
    private void handleRemove() {
        discountAmount = BigDecimal.ZERO;
        appliedPromotionCode = null;
        txtPromotionCode.clear();
        updateTotalDisplay();
        showMessage("Đã xóa mã giảm giá", "info");
    }

    /**
     * Update total display
     */
    private void updateTotalDisplay() {
        if (lblCartTotal != null) {
            lblCartTotal.setText(formatPrice(currentTotal));
        }
        if (lblDiscount != null) {
            lblDiscount.setText("-" + formatPrice(discountAmount));
        }
        if (lblFinalTotal != null) {
            BigDecimal finalTotal = currentTotal.subtract(discountAmount);
            lblFinalTotal.setText(formatPrice(finalTotal));
        }
    }

    /**
     * Get applied promotion code
     */
    public String getAppliedPromotionCode() {
        return appliedPromotionCode;
    }

    /**
     * Get discount amount
     */
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    private String translatePromotionType(String type) {
        if (type == null) return "";
        return switch (type) {
            case "PERCENTAGE" -> "Giảm %";
            case "FIXED_AMOUNT" -> "Giảm cố định";
            case "ITEM_DISCOUNT" -> "Giảm theo sản phẩm";
            case "CART_DISCOUNT" -> "Giảm toàn đơn";
            default -> type;
        };
    }

    private String formatPrice(BigDecimal price) {
        return String.format("%,.0f đ", price);
    }

    private void showMessage(String message, String type) {
        if (lblMessage != null) {
            lblMessage.setText(message);
            switch (type) {
                case "success" -> lblMessage.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                case "error" -> lblMessage.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                default -> lblMessage.setStyle("-fx-text-fill: blue;");
            }
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
