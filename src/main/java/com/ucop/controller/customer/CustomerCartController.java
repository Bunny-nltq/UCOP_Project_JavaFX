package com.ucop.controller.customer;

import java.io.IOException;
import java.math.BigDecimal;

import com.ucop.entity.Cart;
import com.ucop.entity.CartItem;
import com.ucop.entity.Item;
import com.ucop.entity.Order;
import com.ucop.service.OrderService;
import com.ucop.service.ProductService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Controller for Customer Cart View
 */
public class CustomerCartController {

    @FXML private VBox cartItemsContainer;
    @FXML private Label lblEmptyCart;
    @FXML private VBox cartSummary;
    @FXML private Label lblTotalAmount;
    @FXML private Label lblTotalItems;
    @FXML private Button btnClearCart;
    @FXML private Button btnCheckout;
    @FXML private Button btnContinueShopping;

    private OrderService orderService;
    private ProductService productService;
    private Long currentAccountId;
    private Cart currentCart;
    private CustomerProductController parentController;

    @FXML
    public void initialize() {
        // Services will be injected
    }

    /**
     * Set services
     */
    public void setServices(OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    /**
     * Set current account ID
     */
    public void setCurrentAccountId(Long accountId) {
        this.currentAccountId = accountId;
        loadCart();
    }

    /**
     * Set parent controller for navigation
     */
    public void setParentController(CustomerProductController parentController) {
        this.parentController = parentController;
    }

    /**
     * Load cart data
     */
    private void loadCart() {
        if (orderService != null && currentAccountId != null) {
            currentCart = orderService.getOrCreateCart(currentAccountId);
            displayCartItems();
            updateCartSummary();
        }
    }

    /**
     * Display cart items
     */
    private void displayCartItems() {
        if (cartItemsContainer == null) return;

        // Clear existing items (except empty cart label)
        cartItemsContainer.getChildren().removeIf(node -> !(node instanceof Label && ((Label) node).getId() != null && ((Label) node).getId().equals("lblEmptyCart")));

        if (currentCart == null || currentCart.getItems().isEmpty()) {
            if (lblEmptyCart != null) {
                lblEmptyCart.setVisible(true);
            }
            if (cartSummary != null) {
                cartSummary.setVisible(false);
            }
            return;
        }

        if (lblEmptyCart != null) {
            lblEmptyCart.setVisible(false);
        }
        if (cartSummary != null) {
            cartSummary.setVisible(true);
        }

        // Add cart items
        for (CartItem cartItem : currentCart.getItems()) {
            VBox itemCard = createCartItemCard(cartItem);
            cartItemsContainer.getChildren().add(itemCard);
        }
    }

    /**
     * Create cart item card
     */
    private VBox createCartItemCard(CartItem cartItem) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5;");

        HBox content = new HBox(15);
        content.setAlignment(Pos.CENTER_LEFT);

        // Product image
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(80, 80);
        imageContainer.setStyle("-fx-background-color: #f5f5f5;");

        // Get product details (you might need to fetch from service)
        Item product = getProductById(cartItem.getItemId());
        if (product != null && product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            try {
                ImageView imageView = new ImageView(new Image(product.getImageUrl(), true));
                imageView.setFitWidth(70);
                imageView.setFitHeight(70);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageContainer.getChildren().add(imageView);
            } catch (Exception e) {
                Label imgPlaceholder = new Label("📦");
                imgPlaceholder.setFont(Font.font("System", FontWeight.BOLD, 30));
                imageContainer.getChildren().add(imgPlaceholder);
            }
        } else {
            Label imgPlaceholder = new Label("📦");
            imgPlaceholder.setFont(Font.font("System", FontWeight.BOLD, 30));
            imageContainer.getChildren().add(imgPlaceholder);
        }

        // Product details
        VBox details = new VBox(5);
        details.setAlignment(Pos.CENTER_LEFT);
        VBox.setVgrow(details, Priority.ALWAYS);

        Label lblName = new Label(product != null ? product.getName() : "Sản phẩm " + cartItem.getItemId());
        lblName.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblName.setWrapText(true);

        Label lblPrice = new Label(formatPrice(cartItem.getUnitPrice()));
        lblPrice.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");

        // Quantity controls
        HBox quantityBox = new HBox(10);
        quantityBox.setAlignment(Pos.CENTER_LEFT);

        Label lblQty = new Label("Số lượng:");
        Spinner<Integer> spnQuantity = new Spinner<>();
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, cartItem.getQuantity());
        spnQuantity.setValueFactory(valueFactory);
        spnQuantity.setPrefWidth(80);

        Button btnUpdate = new Button("Cập nhật");
        btnUpdate.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        btnUpdate.setOnAction(e -> handleUpdateQuantity(cartItem, spnQuantity.getValue()));

        Button btnRemove = new Button("Xóa");
        btnRemove.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        btnRemove.setOnAction(e -> handleRemoveItem(cartItem));

        quantityBox.getChildren().addAll(lblQty, spnQuantity, btnUpdate, btnRemove);

        details.getChildren().addAll(lblName, lblPrice, quantityBox);

        // Subtotal
        Label lblSubtotal = new Label("Thành tiền: " + formatPrice(cartItem.getSubtotal()));
        lblSubtotal.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblSubtotal.setStyle("-fx-text-fill: #4CAF50;");

        content.getChildren().addAll(imageContainer, details);

        card.getChildren().addAll(content, lblSubtotal);
        return card;
    }

    /**
     * Update cart summary
     */
    private void updateCartSummary() {
        if (currentCart == null || currentCart.getItems().isEmpty()) {
            if (lblTotalAmount != null) lblTotalAmount.setText("0 đ");
            if (lblTotalItems != null) lblTotalItems.setText("0 sản phẩm");
            return;
        }

        BigDecimal totalAmount = currentCart.getItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = currentCart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        if (lblTotalAmount != null) {
            lblTotalAmount.setText(formatPrice(totalAmount));
        }

        if (lblTotalItems != null) {
            lblTotalItems.setText(totalItems + " sản phẩm");
        }
    }

    /**
     * Handle update quantity
     */
    private void handleUpdateQuantity(CartItem cartItem, int newQuantity) {
        try {
            if (newQuantity <= 0) {
                handleRemoveItem(cartItem);
                return;
            }

            orderService.updateCartItemQuantity(currentCart.getId(), cartItem.getItemId(), newQuantity);
            loadCart();
            showInfo("Đã cập nhật số lượng!");
        } catch (Exception e) {
            showError("Lỗi cập nhật số lượng: " + e.getMessage());
        }
    }

    /**
     * Handle remove item
     */
    private void handleRemoveItem(CartItem cartItem) {
        try {
            orderService.removeFromCart(currentCart.getId(), cartItem.getItemId());
            loadCart();
            showInfo("Đã xóa sản phẩm khỏi giỏ hàng!");
        } catch (Exception e) {
            showError("Lỗi xóa sản phẩm: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearCart() {
        if (currentCart == null || currentCart.getItems().isEmpty()) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Xóa giỏ hàng");
        confirm.setContentText("Bạn có chắc muốn xóa tất cả sản phẩm trong giỏ hàng?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    for (CartItem item : currentCart.getItems()) {
                        orderService.removeFromCart(currentCart.getId(), item.getItemId());
                    }
                    loadCart();
                    showInfo("Đã xóa tất cả sản phẩm khỏi giỏ hàng!");
                } catch (Exception e) {
                    showError("Lỗi xóa giỏ hàng: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleCheckout() {
        if (currentCart == null || currentCart.getItems().isEmpty()) {
            showError("Giỏ hàng trống!");
            return;
        }

        try {
            // Create order with PENDING_PAYMENT status
            Order order = orderService.placeOrder(currentCart.getId(), Order.OrderStatus.PENDING_PAYMENT);

            showInfo("Đơn hàng đã được tạo thành công! Mã đơn hàng: " + order.getOrderNumber());

            // Navigate back to products
            handleContinueShopping();
        } catch (Exception e) {
            showError("Lỗi tạo đơn hàng: " + e.getMessage());
        }
    }

    @FXML
    private void handleContinueShopping() {
        if (parentController != null) {
            try {
                // Navigate back to products
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/customer/customer-products.fxml"));
                Parent root = loader.load();

                CustomerProductController controller = loader.getController();
                controller.setServices(productService, orderService);
                controller.setCurrentAccountId(currentAccountId);

                // Get BorderPane from scene
                BorderPane mainContainer = (BorderPane) cartItemsContainer.getScene().getRoot();
                if (mainContainer != null) {
                    mainContainer.setCenter(root);
                }
            } catch (IOException e) {
                showError("Không thể quay lại trang sản phẩm: " + e.getMessage());
            }
        }
    }

    /**
     * Get product by ID (helper method)
     */
    private Item getProductById(Long itemId) {
        if (productService != null) {
            try {
                return productService.getProductById(itemId).orElse(null);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private String formatPrice(BigDecimal price) {
        return String.format("%,.0f đ", price);
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
