package com.ucop.controller.customer;

import com.ucop.entity.Item;
import com.ucop.entity.Cart;
import com.ucop.service.CartService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.math.BigDecimal;

public class ProductDetailController {

    // ================= UI =================
    @FXML private Label lblProductName;
    @FXML private Label lblCategory;
    @FXML private Label lblPrice;
    @FXML private Label lblStock;
    @FXML private TextArea txtDescription;
    @FXML private Spinner<Integer> spnQuantity;
    @FXML private Button btnAddToCart;
    @FXML private StackPane imageContainer;

    // ================= CONTEXT =================
    private Item item;
    private Cart currentCart;
    private CartService cartService;
    private CustomerMainController mainController;

    private Long currentAccountId = 1L; // TODO lấy từ session

    // ================= INIT =================
    @FXML
    public void initialize() {
        spnQuantity.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1)
        );
    }

    // ================= SETTERS =================
    public void setItem(Item item) {
        this.item = item;
        displayItemDetails();
    }

    public void setMainController(CustomerMainController controller) {
        this.mainController = controller;
        this.cartService = controller.getCartService();
        this.currentAccountId = controller.getCurrentAccountId();
        this.currentCart = cartService.getOrCreateCart(currentAccountId);
    }

    // ================= DISPLAY =================
    private void displayItemDetails() {
        if (item == null) return;

        lblProductName.setText(item.getName());
        lblCategory.setText(
                item.getCategory() != null
                        ? item.getCategory().getName()
                        : "Chưa phân loại"
        );

        lblPrice.setText(formatPrice(item.getPrice()));

        long stock = item.getStock() != null ? item.getStock() : 0;
        boolean active = item.getStatus() != null && item.getStatus() == 1;
        boolean available = active && stock > 0;

        lblStock.setText(
                available ? "Còn hàng (" + stock + ")" : "Hết hàng"
        );
        lblStock.setStyle(
                available
                        ? "-fx-text-fill:#27ae60; -fx-font-weight:bold"
                        : "-fx-text-fill:#e74c3c; -fx-font-weight:bold"
        );

        txtDescription.setText(
                item.getDescription() != null
                        ? item.getDescription()
                        : "Không có mô tả chi tiết."
        );

        renderImage();

        btnAddToCart.setDisable(!available);

        if (available) {
            spnQuantity.setValueFactory(
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(
                            1,
                            (int) Math.min(stock, 99),
                            1
                    )
            );
        }
    }

    private void renderImage() {
        imageContainer.getChildren().clear();

        if (item.getImagePath() != null && !item.getImagePath().isBlank()) {
            try {
                ImageView iv = new ImageView(
                        new Image(item.getImagePath(), 380, 380, true, true)
                );
                iv.setPreserveRatio(true);
                imageContainer.getChildren().add(iv);
                return;
            } catch (Exception ignored) {}
        }

        Label placeholder = new Label("📦");
        placeholder.setFont(Font.font("System", FontWeight.BOLD, 120));
        placeholder.setStyle("-fx-text-fill:#bdc3c7;");
        imageContainer.getChildren().add(placeholder);
    }

    // ================= ACTIONS =================
    @FXML
    private void handleAddToCart() {
        if (item == null || cartService == null || currentCart == null) return;

        int qty = spnQuantity.getValue();
        long stock = item.getStock() != null ? item.getStock() : 0;

        if (qty > stock) {
            showError("Số lượng vượt quá tồn kho!");
            return;
        }

        cartService.addToCart(
                currentCart.getId(),
                item.getId().longValue(),
                qty,
                item.getPrice()
        );

        showInfo("Đã thêm vào giỏ hàng!");

        // ⭐ cập nhật badge giỏ hàng
        if (mainController != null) {
            mainController.updateCartCount();
        }
    }

    @FXML
    private void handleBack() {
        if (mainController != null) {
            mainController.updateCartCount();
            // quay lại danh sách đúng kiến trúc
            mainController.getMainContainer().setCenter(
                    mainController.getMainContainer().getCenter()
            );
        }
    }

    // ================= UTIL =================
    private String formatPrice(BigDecimal price) {
        if (price == null) return "0 ₫";
        return String.format("%,.0f ₫", price);
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
