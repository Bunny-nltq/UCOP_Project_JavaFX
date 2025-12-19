package com.ucop.controller.customer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ucop.entity.Cart;
import com.ucop.entity.CartItem;
import com.ucop.entity.Item;
import com.ucop.entity.Order;
import com.ucop.service.CartService;
import com.ucop.service.ItemService;
import com.ucop.service.OrderService;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.File;

public class CustomerCartController {

    @FXML private VBox cartItemsContainer;
    @FXML private Label lblEmptyCart;
    @FXML private VBox cartSummary;
    @FXML private Label lblTotalAmount;
    @FXML private Label lblTotalItems;
    @FXML private Label lblSubtotalAmount;
    @FXML private Label lblDiscountAmount;

    private CartService cartService;
    private ItemService itemService;
    private OrderService orderService;
    private CustomerMainController mainController;

    private Long currentAccountId;
    private Cart currentCart;
    private BigDecimal discountAmount = BigDecimal.ZERO;

    // ================= INJECT =================
    public void setServices(OrderService orderService,
                            ItemService itemService,
                            CartService cartService) {
        this.orderService = orderService;
        this.itemService = itemService;
        this.cartService = cartService;
    }

    public void setMainController(CustomerMainController mainController) {
        this.mainController = mainController;
    }

    public void setCurrentAccountId(Long accountId) {
        this.currentAccountId = accountId;
        loadCart();
    }

    // ================= LOAD CART =================
    private void loadCart() {
        if (cartService == null || currentAccountId == null) return;
        currentCart = cartService.getOrCreateCart(currentAccountId);
        displayCartItems();
        updateCartSummary();
        if (mainController != null) mainController.updateCartCount();
    }

    // ================= DISPLAY =================
    private void displayCartItems() {
        cartItemsContainer.getChildren().clear();

        if (currentCart == null || currentCart.getItems().isEmpty()) {
            lblEmptyCart.setVisible(true);
            cartSummary.setVisible(false);
            return;
        }

        lblEmptyCart.setVisible(false);
        cartSummary.setVisible(true);

        for (CartItem ci : currentCart.getItems()) {
            cartItemsContainer.getChildren().add(createCartItemCard(ci));
        }
    }

    private VBox createCartItemCard(CartItem cartItem) {
        // ✅ FIX: itemId trong CartItem là Long -> getItemById(Long)
        Item item = itemService.getItemById(cartItem.getItemId());

        VBox card = new VBox(10);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color:white;-fx-border-color:#ddd");

        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);

        ImageView iv = new ImageView();
        iv.setFitWidth(70);
        iv.setFitHeight(70);
        iv.setPreserveRatio(true);

        // ✅ FIX: load ảnh local path hoặc URL
        if (item != null && item.getImagePath() != null) {
            String p = item.getImagePath();
            File f = new File(p);
            if (f.exists()) {
                iv.setImage(new Image(f.toURI().toString(), true));
            } else if (p.startsWith("http://") || p.startsWith("https://")) {
                iv.setImage(new Image(p, true));
            }
        }

        VBox info = new VBox(5);
        Label name = new Label(item != null ? item.getName() : "Sản phẩm");
        name.setFont(Font.font(null, FontWeight.BOLD, 14));

        Label price = new Label(formatPrice(cartItem.getUnitPrice()));
        price.setStyle("-fx-text-fill:#e53935");

        Spinner<Integer> qty = new Spinner<>(1, 99, cartItem.getQuantity());
        qty.valueProperty().addListener((o, oldV, newV) -> {
            cartService.updateItemQuantity(
                    currentCart.getId(),
                    cartItem.getItemId(),
                    newV
            );
            loadCart(); // ✅ refresh lại UI + summary
        });

        Button btnRemove = new Button("Xóa");
        btnRemove.setOnAction(e -> {
            cartService.removeItemFromCart(
                    currentCart.getId(),
                    cartItem.getItemId()
            );
            loadCart(); // ✅ refresh lại UI + summary
        });

        info.getChildren().addAll(name, price, qty, btnRemove);
        row.getChildren().addAll(iv, info);

        Label subtotal = new Label("Thành tiền: " + formatPrice(cartItem.getSubtotal()));
        subtotal.setStyle("-fx-font-weight:bold;-fx-text-fill:#4caf50");

        card.getChildren().addAll(row, subtotal);
        return card;
    }

    // ================= SUMMARY =================
    private void updateCartSummary() {
        BigDecimal subtotal = currentCart.getItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = currentCart.getItems().stream()
                .mapToInt(CartItem::getQuantity).sum();

        lblSubtotalAmount.setText(formatPrice(subtotal));
        lblDiscountAmount.setText(formatPrice(discountAmount));

        BigDecimal total = subtotal.subtract(discountAmount);
        lblTotalAmount.setText(formatPrice(total));
        lblTotalItems.setText(totalItems + " sản phẩm");
    }

    // ================= ACTION =================
    @FXML
    private void handleClearCart() {
        List<CartItem> copy = new ArrayList<>(currentCart.getItems());
        for (CartItem ci : copy) {
            cartService.removeItemFromCart(currentCart.getId(), ci.getItemId());
        }
        loadCart();
    }

    @FXML
    private void handleCheckout() {
        Order order = orderService.placeOrder(
                currentCart.getId(),
                Order.OrderStatus.PENDING_PAYMENT
        );
        showInfo("Đặt hàng thành công!\nMã đơn: " + order.getOrderNumber());
        loadCart();
        mainController.handleMyOrders();
    }

    private String formatPrice(BigDecimal p) {
        return String.format("%,.0f đ", p);
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}
