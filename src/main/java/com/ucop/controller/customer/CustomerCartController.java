package com.ucop.controller.customer;

import com.ucop.entity.Cart;
import com.ucop.entity.CartItem;
import com.ucop.entity.Item;
import com.ucop.entity.Order;
import com.ucop.repository.*;
import com.ucop.repository.impl.*;
import com.ucop.service.CartService;
import com.ucop.service.ItemService;
import com.ucop.service.OrderService;
import com.ucop.util.HibernateUtil;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.hibernate.SessionFactory;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CustomerCartController {

    // ===== LEFT =====
    @FXML private VBox cartItemsContainer;
    @FXML private Label lblEmptyCart;

    // ===== RIGHT / CHECKOUT =====
    @FXML private VBox checkoutPanel;

    @FXML private TextField txtPromoCode;
    @FXML private Label lblPromoMsg;

    @FXML private RadioButton rbPickup;
    @FXML private RadioButton rbShip;
    private final ToggleGroup deliveryGroup = new ToggleGroup();

    @FXML private VBox shippingForm;
    @FXML private TextField txtShipName;
    @FXML private TextField txtShipPhone;
    @FXML private TextField txtShipAddress;
    @FXML private TextField txtShipCity;
    @FXML private TextField txtShipPostal;
    @FXML private Label lblShipMsg;

    // ===== SUMMARY LABELS =====
    @FXML private Label lblTotalAmount;
    @FXML private Label lblTotalItems;
    @FXML private Label lblSubtotalAmount;
    @FXML private Label lblDiscountAmount;
    @FXML private Label lblShippingFee;

    private CartService cartService;
    private ItemService itemService;
    private OrderService orderService;

    private CustomerMainController mainController;

    private Long currentAccountId;
    private Cart currentCart;

    // promo / fees
    private String appliedPromoCode = null;
    private BigDecimal discountAmount = BigDecimal.ZERO;
    private BigDecimal shippingFee = BigDecimal.ZERO;

    // ================= INJECT (OPTIONAL) =================
    public void setServices(OrderService orderService,
                            ItemService itemService,
                            CartService cartService) {
        this.orderService = orderService;
        this.itemService = itemService;
        this.cartService = cartService;

        ensureServices();
        loadCart();
    }

    public void setMainController(CustomerMainController mainController) {
        this.mainController = mainController;
    }

    public void setCurrentAccountId(Long accountId) {
        this.currentAccountId = accountId;
        ensureServices();
        loadCart();
    }

    // ================= INIT =================
    @FXML
    public void initialize() {
        // setup toggle group
        rbPickup.setToggleGroup(deliveryGroup);
        rbShip.setToggleGroup(deliveryGroup);

        // default mode
        rbPickup.setSelected(true);
        updateDeliveryUI();

        lblPromoMsg.setText("");
        lblShipMsg.setText("");
    }

    // ================= FALLBACK SERVICES =================
    private void ensureServices() {
        SessionFactory sf = HibernateUtil.getSessionFactory();

        if (itemService == null) itemService = new ItemService();

        if (cartService == null) {
            CartRepository cartRepo = new CartRepositoryImpl(sf);
            cartService = new CartService(cartRepo);
        }

        if (orderService == null) {
            OrderRepository orderRepo = new OrderRepositoryImpl(sf);
            CartRepository cartRepo = new CartRepositoryImpl(sf);
            StockItemRepository stockRepo = new StockItemRepositoryImpl(sf);
            ShipmentRepository shipRepo = new ShipmentRepositoryImpl(sf);
            orderService = new OrderService(orderRepo, cartRepo, stockRepo, shipRepo);
        }
    }

    // ================= LOAD CART =================
    private void loadCart() {
        System.out.println("[DEBUG][Cart] loadCart: accountId=" + currentAccountId
                + ", cartService=" + (cartService != null)
                + ", itemService=" + (itemService != null));

        if (currentAccountId == null) {
            showEmpty("Thiếu accountId (chưa inject).");
            return;
        }

        ensureServices();

        currentCart = cartService.getOrCreateCart(currentAccountId);
        displayCartItems();
        updateCartSummary();

        if (mainController != null) mainController.updateCartCount();
    }

    private void showEmpty(String msg) {
        cartItemsContainer.getChildren().clear();
        lblEmptyCart.setText(msg);
        lblEmptyCart.setVisible(true);
        lblEmptyCart.setManaged(true);
        if (checkoutPanel != null) {
            checkoutPanel.setDisable(true);
            checkoutPanel.setOpacity(0.6);
        }
    }

    // ================= DISPLAY =================
    private void displayCartItems() {
        cartItemsContainer.getChildren().clear();

        boolean empty = (currentCart == null || currentCart.getItems() == null || currentCart.getItems().isEmpty());
        if (empty) {
            lblEmptyCart.setText("Giỏ hàng của bạn đang trống");
            lblEmptyCart.setVisible(true);
            lblEmptyCart.setManaged(true);

            if (checkoutPanel != null) {
                checkoutPanel.setDisable(true);
                checkoutPanel.setOpacity(0.6);
            }
            return;
        }

        lblEmptyCart.setVisible(false);
        lblEmptyCart.setManaged(false);

        if (checkoutPanel != null) {
            checkoutPanel.setDisable(false);
            checkoutPanel.setOpacity(1.0);
        }

        for (CartItem ci : currentCart.getItems()) {
            cartItemsContainer.getChildren().add(createCartItemCard(ci));
        }
    }

    private VBox createCartItemCard(CartItem cartItem) {
        Item item = null;
        try {
            Long iid = cartItem.getItemId();
            if (iid != null) item = itemService.getItemById(iid); // ✅ dùng overload Long
        } catch (Exception ignored) {}

        VBox card = new VBox(10);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color:white;-fx-border-color:#ddd;-fx-border-radius:10;-fx-background-radius:10;");

        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);

        ImageView iv = new ImageView();
        iv.setFitWidth(70);
        iv.setFitHeight(70);
        iv.setPreserveRatio(true);

        if (item != null && item.getImagePath() != null) {
            String p = item.getImagePath();
            File f = new File(p);
            if (f.exists()) iv.setImage(new Image(f.toURI().toString(), true));
            else if (p.startsWith("http://") || p.startsWith("https://")) iv.setImage(new Image(p, true));
        }

        VBox info = new VBox(6);

        Label name = new Label(item != null ? item.getName() : ("Item #" + cartItem.getItemId()));
        name.setFont(Font.font(null, FontWeight.BOLD, 14));

        BigDecimal unitPrice = cartItem.getUnitPrice() != null ? cartItem.getUnitPrice() : BigDecimal.ZERO;
        Label price = new Label(formatPrice(unitPrice));
        price.setStyle("-fx-text-fill:#e53935");

        Spinner<Integer> qty = new Spinner<>(1, 99, Math.max(1, cartItem.getQuantity()));
        qty.setEditable(true);
        qty.valueProperty().addListener((o, oldV, newV) -> {
            cartService.updateItemQuantity(currentCart.getId(), cartItem.getItemId(), newV);
            loadCart();
        });

        Button btnRemove = new Button("Xóa");
        btnRemove.setOnAction(e -> {
            cartService.removeItemFromCart(currentCart.getId(), cartItem.getItemId());
            loadCart();
        });

        info.getChildren().addAll(name, price, new Label("Số lượng:"), qty, btnRemove);
        row.getChildren().addAll(iv, info);

        Label subtotal = new Label("Thành tiền: " + formatPrice(cartItem.getSubtotal()));
        subtotal.setStyle("-fx-font-weight:bold;-fx-text-fill:#4caf50");

        card.getChildren().addAll(row, subtotal);
        return card;
    }

    // ================= PROMO =================
    @FXML
    private void handleApplyPromo() {
        String code = (txtPromoCode.getText() == null) ? "" : txtPromoCode.getText().trim();

        if (code.isEmpty()) {
            appliedPromoCode = null;
            discountAmount = BigDecimal.ZERO;
            lblPromoMsg.setText("Đã xóa mã giảm giá.");
            updateCartSummary();
            return;
        }

        // ✅ Demo rule (bạn có thể nối DB promotion sau)
        // SALE10 = giảm 10% tối đa 50k
        if ("SALE10".equalsIgnoreCase(code)) {
            appliedPromoCode = "SALE10";
            lblPromoMsg.setText("Áp dụng mã SALE10 (giảm 10%, tối đa 50.000đ).");
        } else {
            appliedPromoCode = null;
            discountAmount = BigDecimal.ZERO;
            lblPromoMsg.setText("Mã không hợp lệ.");
        }

        updateCartSummary();
    }

    // ================= DELIVERY MODE =================
    @FXML
    private void handleDeliveryModeChanged() {
        updateDeliveryUI();
        updateCartSummary();
    }

    private void updateDeliveryUI() {
        boolean ship = (rbShip != null && rbShip.isSelected());
        shippingForm.setVisible(ship);
        shippingForm.setManaged(ship);
        lblShipMsg.setText("");
    }

    // ================= SUMMARY =================
    private void updateCartSummary() {
        if (currentCart == null || currentCart.getItems() == null) {
            return;
        }

        BigDecimal subtotal = currentCart.getItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = currentCart.getItems().stream()
                .mapToInt(CartItem::getQuantity).sum();

        // shipping fee rule: ship => 30k, pickup => 0
        boolean ship = rbShip != null && rbShip.isSelected();
        shippingFee = ship ? new BigDecimal("30000") : BigDecimal.ZERO;

        // discount rule
        discountAmount = BigDecimal.ZERO;
        if ("SALE10".equalsIgnoreCase(appliedPromoCode)) {
            BigDecimal tenPct = subtotal.multiply(new BigDecimal("0.10"));
            BigDecimal cap = new BigDecimal("50000");
            discountAmount = tenPct.min(cap);
        }

        lblTotalItems.setText(totalItems + " sản phẩm");
        lblSubtotalAmount.setText(formatPrice(subtotal));
        lblDiscountAmount.setText(formatPrice(discountAmount));
        lblShippingFee.setText(formatPrice(shippingFee));

        BigDecimal total = subtotal.subtract(discountAmount).add(shippingFee);
        if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;

        lblTotalAmount.setText(formatPrice(total));
    }

    // ================= ACTION =================
    @FXML
    private void handleClearCart() {
        if (currentCart == null || currentCart.getItems() == null) return;
        List<CartItem> copy = new ArrayList<>(currentCart.getItems());
        for (CartItem ci : copy) {
            cartService.removeItemFromCart(currentCart.getId(), ci.getItemId());
        }
        appliedPromoCode = null;
        discountAmount = BigDecimal.ZERO;
        shippingFee = BigDecimal.ZERO;
        txtPromoCode.setText("");
        lblPromoMsg.setText("");
        loadCart();
    }

    @FXML
    private void handleCheckout() {
        ensureServices();

        // 1) Validate accountId trước
        if (currentAccountId == null) {
            showInfo("Thiếu accountId. Vui lòng đăng nhập lại!");
            return;
        }

        // 2) LUÔN lấy cart mới nhất từ DB trước khi checkout (tránh currentCart bị stale/rỗng giả)
        currentCart = cartService.getOrCreateCart(currentAccountId);

        if (currentCart == null || currentCart.getId() == null) {
            showInfo("Không tìm thấy giỏ hàng!");
            return;
        }

        // 3) Kiểm tra cart rỗng trước khi gọi placeOrder (tránh app crash)
        if (currentCart.getItems() == null || currentCart.getItems().isEmpty()) {
            showInfo("Giỏ hàng trống! Vui lòng thêm sản phẩm trước khi thanh toán.");
            loadCart();
            return;
        }

        boolean ship = rbShip != null && rbShip.isSelected();

        // 4) Validate shipping nếu ship mode
        if (ship) {
            String n = v(txtShipName);
            String p = v(txtShipPhone);
            String a = v(txtShipAddress);
            String c = v(txtShipCity);

            if (n.isEmpty() || p.isEmpty() || a.isEmpty() || c.isEmpty()) {
                lblShipMsg.setText("Vui lòng nhập đầy đủ họ tên, SĐT, địa chỉ, thành phố.");
                return;
            }
            lblShipMsg.setText("");
        }

        // 5) Tạo order từ cart (BẮT lỗi Cart is empty để không văng chương trình)
        Order order;
        try {
            order = orderService.placeOrder(currentCart.getId(), Order.OrderStatus.PENDING_PAYMENT);
        } catch (IllegalArgumentException ex) {
            // placeOrder() sẽ throw "Cart is empty" nếu cart.getItems().isEmpty() :contentReference[oaicite:1]{index=1}
            showInfo("Không thể thanh toán: " + ex.getMessage());
            loadCart();
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
            showInfo("Lỗi khi tạo đơn hàng: " + ex.getMessage());
            loadCart();
            return;
        }

        // 6) Set các field theo bảng orders (promo + ship info) rồi update
        try {
            if (appliedPromoCode != null && !appliedPromoCode.isBlank()) {
                order.setPromotionCode(appliedPromoCode);
            }

            order.setCartDiscount(discountAmount);
            order.setShippingFee(shippingFee);

            if (ship) {
                order.setShippingName(v(txtShipName));
                order.setShippingPhone(v(txtShipPhone));
                order.setShippingAddress(v(txtShipAddress));
                order.setShippingCity(v(txtShipCity));
                order.setShippingPostalCode(v(txtShipPostal));
            } else {
                order.setShippingName(null);
                order.setShippingPhone(null);
                order.setShippingAddress(null);
                order.setShippingCity(null);
                order.setShippingPostalCode(null);
            }

            orderService.update(order);

        } catch (Exception ex) {
            ex.printStackTrace();
            showInfo(
                "Đặt hàng thành công nhưng chưa lưu được thông tin ship/promo.\n" +
                "Bạn cần kiểm tra Order entity có đủ field/setter (promotion_code, shipping_*)"
            );
            // vẫn reload cart để UI đồng bộ
            loadCart();
            return;
        }

        showInfo("Đặt hàng thành công!\nMã đơn: " + order.getOrderNumber());

        // 7) Reload cart view (vì placeOrder() đã clear cart) :contentReference[oaicite:2]{index=2}
        loadCart();

        if (mainController != null) {
            mainController.handleMyOrders();
        }
    }


    private String v(TextField tf) {
        return tf == null || tf.getText() == null ? "" : tf.getText().trim();
    }

    private String formatPrice(BigDecimal p) {
        if (p == null) p = BigDecimal.ZERO;
        return String.format("%,.0f đ", p);
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}
