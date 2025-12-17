package com.ucop.controller.customer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import com.ucop.entity.Cart;
import com.ucop.entity.CartItem;
import com.ucop.entity.Item;
import com.ucop.entity.Order;
import com.ucop.entity.Product;
import com.ucop.entity.Promotion;
import com.ucop.dao.ProductDAO;
import com.ucop.repository.CartRepository;
import com.ucop.repository.PromotionRepository;
import com.ucop.repository.impl.CartRepositoryImpl;
import com.ucop.service.OrderService;
import com.ucop.service.ProductService;
import com.ucop.service.CartServiceV2;
import com.ucop.util.HibernateUtil;

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
    @FXML private Label lblSubtotalAmount;
    @FXML private Label lblDiscountAmount;
    @FXML private javafx.scene.control.TextField txtPromotionCode;
    @FXML private Button btnApplyPromotion;
    @FXML private Button btnRemovePromotion;
    @FXML private Label lblPromotionMessage;
    @FXML private Button btnClearCart;
    @FXML private Button btnCheckout;
    @FXML private Button btnContinueShopping;

    private OrderService orderService;
    private ProductService productService;
    private CartServiceV2 cartService;
    private ProductDAO productDAO;
    private PromotionRepository promotionRepository;
    private Long currentAccountId;
    private Cart currentCart;
    private CustomerProductController parentController;
    private CustomerMainController mainController;
    private Promotion appliedPromotion;
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @FXML
    public void initialize() {
        // Services will be injected
        // Khởi tạo thêm các service cho Product entity
        CartRepository cartRepository = new CartRepositoryImpl(HibernateUtil.getSessionFactory());
        cartService = new CartServiceV2(cartRepository);
        productDAO = new ProductDAO();
        promotionRepository = new PromotionRepository(HibernateUtil.getSessionFactory());
        
        // Khởi tạo OrderService (cần thiết cho chức năng checkout)
        com.ucop.repository.impl.OrderRepositoryImpl orderRepository = 
            new com.ucop.repository.impl.OrderRepositoryImpl(HibernateUtil.getSessionFactory());
        com.ucop.repository.impl.StockItemRepositoryImpl stockItemRepository = 
            new com.ucop.repository.impl.StockItemRepositoryImpl(HibernateUtil.getSessionFactory());
        com.ucop.repository.impl.ShipmentRepositoryImpl shipmentRepository =
            new com.ucop.repository.impl.ShipmentRepositoryImpl(HibernateUtil.getSessionFactory());
        orderService = new OrderService(orderRepository, cartRepository, stockItemRepository, shipmentRepository);
        
        // TODO: Get from session/login - hiện tại dùng account ID mặc định
        if (currentAccountId == null) {
            currentAccountId = 1L; // Account ID mặc định cho testing
        }
        
        // Load cart data
        loadCart();
        
        // Disable remove promotion button initially
        if (btnRemovePromotion != null) {
            btnRemovePromotion.setDisable(true);
        }
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
     * Set main controller for navigation to orders page
     */
    public void setMainController(CustomerMainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Refresh cart - Public method to reload cart from outside
     */
    public void refreshCart() {
        loadCart();
    }

    /**
     * Load cart data - Hỗ trợ cả V1 (items) và V2 (products)
     */
    private void loadCart() {
        System.out.println("=== Loading Cart ===");
        if (cartService != null && currentAccountId != null) {
            // Sử dụng CartServiceV2 (V2)
            currentCart = cartService.getOrCreateCart(currentAccountId);
            System.out.println("✓ Loaded cart using CartServiceV2 (V2)");
        } else if (orderService != null && currentAccountId != null) {
            // Fallback to OrderService (V1)
            currentCart = orderService.getOrCreateCart(currentAccountId);
            System.out.println("✓ Loaded cart using OrderService (V1)");
        }
        
        if (currentCart != null) {
            System.out.println("✓ Cart ID: " + currentCart.getId() + ", Items: " + currentCart.getItems().size());
            displayCartItems();
            updateCartSummary();
        } else {
            System.err.println("✗ Failed to load cart!");
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
        
        // Auto-update quantity when spinner value changes
        spnQuantity.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue)) {
                handleUpdateQuantity(cartItem, newValue);
            }
        });

        Button btnRemove = new Button("Xóa");
        btnRemove.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        btnRemove.setOnAction(e -> handleRemoveItem(cartItem));

        quantityBox.getChildren().addAll(lblQty, spnQuantity, btnRemove);

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
            if (lblSubtotalAmount != null) lblSubtotalAmount.setText("0 đ");
            if (lblDiscountAmount != null) lblDiscountAmount.setText("0 đ");
            return;
        }

        BigDecimal subtotal = currentCart.getItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = currentCart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        if (lblSubtotalAmount != null) {
            lblSubtotalAmount.setText(formatPrice(subtotal));
        }
        
        if (lblDiscountAmount != null) {
            if (discountAmount.compareTo(BigDecimal.ZERO) > 0) {
                lblDiscountAmount.setText("-" + formatPrice(discountAmount));
                lblDiscountAmount.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            } else {
                lblDiscountAmount.setText(formatPrice(BigDecimal.ZERO));
            }
        }
        
        BigDecimal finalTotal = subtotal.subtract(discountAmount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
        }

        if (lblTotalAmount != null) {
            lblTotalAmount.setText(formatPrice(finalTotal));
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

            // Sử dụng cartService (V2)
            if (cartService != null) {
                cartService.updateProductQuantity(currentCart.getId(), cartItem.getItemId(), newQuantity);
            } else if (orderService != null) {
                orderService.updateCartItemQuantity(currentCart.getId(), cartItem.getItemId(), newQuantity);
            }
            
            loadCart();
            
        } catch (Exception e) {
            showError("Lỗi cập nhật số lượng: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle remove item
     */
    private void handleRemoveItem(CartItem cartItem) {
        try {
            // Sử dụng cartService (V2)
            if (cartService != null) {
                cartService.removeProductFromCart(currentCart.getId(), cartItem.getItemId());
            } else if (orderService != null) {
                orderService.removeFromCart(currentCart.getId(), cartItem.getItemId());
            }
            
            loadCart();
            showInfo("Đã xóa sản phẩm khỏi giỏ hàng!");
        } catch (Exception e) {
            showError("Lỗi xóa sản phẩm: " + e.getMessage());
            e.printStackTrace();
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
                    // Sử dụng cartService hoặc orderService
                    for (CartItem item : currentCart.getItems()) {
                        if (cartService != null) {
                            cartService.removeProductFromCart(currentCart.getId(), item.getItemId());
                        } else if (orderService != null) {
                            orderService.removeFromCart(currentCart.getId(), item.getItemId());
                        }
                    }
                    loadCart();
                    showInfo("Đã xóa tất cả sản phẩm khỏi giỏ hàng!");
                } catch (Exception e) {
                    showError("Lỗi xóa giỏ hàng: " + e.getMessage());
                    e.printStackTrace();
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
            BigDecimal subtotal = currentCart.getItems().stream()
                    .map(CartItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            final BigDecimal finalTotal = subtotal.subtract(discountAmount);
            final BigDecimal totalForDisplay = finalTotal.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : finalTotal;
            
            // Hiển thị xác nhận
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Xác nhận thanh toán");
            confirm.setHeaderText("Thanh toán đơn hàng");
            String message = "Tạm tính: " + formatPrice(subtotal);
            if (appliedPromotion != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
                message += "\nMã khuyến mãi: " + appliedPromotion.getCode();
                message += "\nGiảm giá: -" + formatPrice(discountAmount);
            }
            message += "\n\nTổng tiền: " + formatPrice(totalForDisplay);
            message += "\n\nBạn có muốn thanh toán?";
            confirm.setContentText(message);
            
            final Promotion promotionForLambda = appliedPromotion;
            final BigDecimal discountForLambda = discountAmount;
            
            confirm.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    try {
                        System.out.println("=== Creating Order ===");
                        System.out.println("Cart ID: " + currentCart.getId());
                        System.out.println("Account ID: " + currentAccountId);
                        
                        // Create order with PENDING_PAYMENT status
                        Order order = orderService.placeOrder(currentCart.getId(), Order.OrderStatus.PENDING_PAYMENT);
                        
                        System.out.println("✓ Order created successfully!");
                        System.out.println("Order ID: " + order.getId());
                        System.out.println("Order Number: " + order.getOrderNumber());
                        System.out.println("Account ID: " + order.getAccountId());

                        String successMsg = "Đặt hàng thành công!\n\nMã đơn hàng: " + order.getOrderNumber() + 
                                          "\nTổng tiền: " + formatPrice(totalForDisplay);
                        if (promotionForLambda != null) {
                            successMsg += "\nGiảm giá: " + formatPrice(discountForLambda);
                        }
                        successMsg += "\n\nĐơn hàng của bạn đã được tạo!";
                        
                        showInfo(successMsg);
                        
                        // Reset promotion
                        appliedPromotion = null;
                        discountAmount = BigDecimal.ZERO;
                        if (txtPromotionCode != null) txtPromotionCode.clear();
                        if (lblPromotionMessage != null) lblPromotionMessage.setText("");
                        if (btnRemovePromotion != null) btnRemovePromotion.setDisable(true);

                        // Reload cart to reflect empty cart after order
                        loadCart();
                        
                        System.out.println("=== Navigating to Orders Page ===");
                        System.out.println("MainController: " + (mainController != null ? "OK" : "NULL"));
                        
                        // Navigate to orders page to show the new order
                        if (mainController != null) {
                            mainController.handleMyOrders();
                        } else {
                            System.out.println("⚠️ MainController is null, cannot navigate to orders page");
                            // Fallback to continue shopping if mainController not set
                            handleContinueShopping();
                        }
                    } catch (Exception e) {
                        System.err.println("✗ Error creating order: " + e.getMessage());
                        e.printStackTrace();
                        showError("Lỗi tạo đơn hàng: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            showError("Lỗi: " + e.getMessage());
        }
    }

    @FXML
    private void handleContinueShopping() {
        // Không cần navigate thủ công vì dashboard đã có menu Sản phẩm
        // User chỉ cần click vào menu "Sản phẩm" ở trên dashboard
        System.out.println("Tip: Sử dụng menu 'Sản phẩm' ở trên để quay lại trang sản phẩm");
        
        // Nếu thực sự cần navigate tự động, có thể thêm logic ở đây
        // Nhưng hiện tại để trống để tránh ghi đè lên dashboard
    }

    /**
     * Get product by ID - Hỗ trợ cả Item và Product entity (V2)
     */
    private Item getProductById(Long itemId) {
        // Ưu tiên lấy từ Product entity (V2 - từ bảng products)
        Product productEntity = getProductEntityById(itemId);
        if (productEntity != null) {
            // Convert Product to Item for compatibility
            // Tạo Item wrapper để hiển thị (không cần persist)
            Item item = new Item();
            // id sẽ được generate khi persist, không set ở đây
            item.setName(productEntity.getName());
            item.setDescription(productEntity.getDescription());
            item.setPrice(productEntity.getPrice());
            item.setImageUrl(productEntity.getImageUrl());
            item.setStock(productEntity.getStockQuantity() != null ? productEntity.getStockQuantity().intValue() : 0);
            System.out.println("✓ Loaded product from products table (V2): " + productEntity.getName());
            return item;
        }
        
        // Fallback to old Item service (từ bảng items)
        if (productService != null) {
            try {
                Optional<Item> itemOpt = productService.getProductById(itemId);
                if (itemOpt.isPresent()) {
                    System.out.println("✓ Loaded product from items table (V1): " + itemOpt.get().getName());
                    return itemOpt.get();
                }
            } catch (Exception e) {
                System.err.println("✗ Error getting product from items table: " + e.getMessage());
            }
        }
        return null;
    }
    
    /**
     * Get Product entity by ID (V2 - từ bảng products)
     */
    private Product getProductEntityById(Long itemId) {
        if (productDAO != null) {
            try {
                Optional<Product> productOpt = productDAO.findById(itemId);
                return productOpt.orElse(null);
            } catch (Exception e) {
                System.err.println("✗ Error getting product entity: " + e.getMessage());
                return null;
            }
        }
        return null;
    }
    
    /**
     * Áp dụng mã khuyến mãi
     */
    @FXML
    private void handleApplyPromotion() {
        if (txtPromotionCode == null) return;
        
        String code = txtPromotionCode.getText().trim();
        if (code.isEmpty()) {
            showError("Vui lòng nhập mã khuyến mãi!");
            return;
        }

        try {
            // Tìm promotion
            Optional<Promotion> promotionOpt = promotionRepository.findByCode(code);
            if (promotionOpt.isEmpty()) {
                showError("Mã khuyến mãi không tồn tại!");
                return;
            }

            Promotion promotion = promotionOpt.get();
            
            // Validate promotion
            String error = validatePromotion(promotion);
            if (error != null) {
                showError(error);
                return;
            }

            // Calculate discount
            BigDecimal subtotal = currentCart.getItems().stream()
                    .map(CartItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            discountAmount = calculateDiscount(promotion, subtotal);

            appliedPromotion = promotion;
            
            if (lblPromotionMessage != null) {
                lblPromotionMessage.setText("✓ Đã áp dụng mã: " + code);
                lblPromotionMessage.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            }
            
            if (btnRemovePromotion != null) {
                btnRemovePromotion.setDisable(false);
            }
            
            updateCartSummary();
            showInfo("Đã áp dụng mã khuyến mãi thành công!\nGiảm giá: " + formatPrice(discountAmount));
            
        } catch (Exception e) {
            showError("Lỗi khi áp dụng mã khuyến mãi: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Xóa mã khuyến mãi
     */
    @FXML
    private void handleRemovePromotion() {
        appliedPromotion = null;
        discountAmount = BigDecimal.ZERO;
        
        if (txtPromotionCode != null) {
            txtPromotionCode.clear();
        }
        
        if (lblPromotionMessage != null) {
            lblPromotionMessage.setText("");
        }
        
        if (btnRemovePromotion != null) {
            btnRemovePromotion.setDisable(true);
        }
        
        updateCartSummary();
        showInfo("Đã xóa mã khuyến mãi!");
    }
    
    /**
     * Validate promotion
     */
    private String validatePromotion(Promotion promotion) {
        if (!promotion.getActive()) {
            return "Mã khuyến mãi đã bị vô hiệu hóa!";
        }

        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        if (now.isBefore(promotion.getStartDate())) {
            return "Mã khuyến mãi chưa có hiệu lực!";
        }
        if (now.isAfter(promotion.getEndDate())) {
            return "Mã khuyến mãi đã hết hạn!";
        }

        if (promotion.getMaxUsageTotal() != null && 
            promotion.getUsageCount() >= promotion.getMaxUsageTotal()) {
            return "Mã khuyến mãi đã hết lượt sử dụng!";
        }

        BigDecimal subtotal = currentCart.getItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (promotion.getMinOrderAmount() != null && 
            subtotal.compareTo(promotion.getMinOrderAmount()) < 0) {
            return "Đơn hàng chưa đạt giá trị tối thiểu: " + formatPrice(promotion.getMinOrderAmount());
        }

        return null;
    }
    
    /**
     * Tính discount amount
     */
    private BigDecimal calculateDiscount(Promotion promotion, BigDecimal subtotal) {
        BigDecimal discount = BigDecimal.ZERO;

        String discountType = promotion.getDiscountType();
        if ("PERCENTAGE".equals(discountType)) {
            discount = subtotal.multiply(promotion.getDiscountValue())
                              .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        } else if ("FIXED_AMOUNT".equals(discountType)) {
            discount = promotion.getDiscountValue();
        }

        if (promotion.getMaxDiscountAmount() != null && 
            discount.compareTo(promotion.getMaxDiscountAmount()) > 0) {
            discount = promotion.getMaxDiscountAmount();
        }

        if (discount.compareTo(subtotal) > 0) {
            discount = subtotal;
        }

        return discount;
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
