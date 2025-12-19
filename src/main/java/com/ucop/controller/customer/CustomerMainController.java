package com.ucop.controller.customer;

import com.ucop.repository.*;
import com.ucop.repository.impl.*;
import com.ucop.service.CartService;
import com.ucop.service.ItemService;
import com.ucop.service.OrderService;
import com.ucop.util.HibernateUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.hibernate.SessionFactory;

import java.lang.reflect.Method;
import java.net.URL;

public class CustomerMainController {

    @FXML private BorderPane mainContainer;
    @FXML private Label lblWelcome;
    @FXML private Label lblCartCount;

    private ItemService itemService;
    private OrderService orderService;
    private CartService cartService;

    // ✅ Nên được set từ LoginController; tạm fallback để app không null crash
    private Long currentAccountId = 1L;

    @FXML
    public void initialize() {
        System.out.println("[DEBUG][CustomerMain] initialize()");

        initializeServices();
        ensureCartService();

        if (lblWelcome != null) {
            lblWelcome.setText("Chào mừng, Khách hàng!");
        }

        Platform.runLater(() -> {
            updateCartCount();
            handleItems();
        });
    }

    // ================= SERVICES =================

    private void initializeServices() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        if (sessionFactory == null) throw new IllegalStateException("SessionFactory is null");

        itemService = new ItemService();

        CartRepository cartRepository = new CartRepositoryImpl(sessionFactory);
        OrderRepository orderRepository = new OrderRepositoryImpl(sessionFactory);
        StockItemRepository stockItemRepository = new StockItemRepositoryImpl(sessionFactory);
        ShipmentRepository shipmentRepository = new ShipmentRepositoryImpl(sessionFactory);

        cartService = new CartService(cartRepository);

        orderService = new OrderService(
                orderRepository,
                cartRepository,
                stockItemRepository,
                shipmentRepository
        );
    }

    private void ensureCartService() {
        if (cartService != null) return;

        System.out.println("[DEBUG][CustomerMain] cartService null -> fallback create");
        SessionFactory sf = HibernateUtil.getSessionFactory();
        cartService = new CartService(new CartRepositoryImpl(sf));
        System.out.println("[DEBUG][CustomerMain] cartService fallback created=" + (cartService != null));
    }

    // ================= NAV =================

    @FXML
    public void handleItems() {
        loadItemsPage();
    }

    @FXML
    public void handleMyOrders() {
        loadOrdersPage();
    }

    @FXML
    public void handleCart() {
        loadCartPage();
    }

    @FXML
    public void handleLogout() {
        showError("Chức năng đăng xuất chưa triển khai.");
    }

    // ================= LOAD PAGES =================

    private void loadItemsPage() {
        try {
            if (mainContainer == null) {
                System.out.println("[DEBUG][CustomerMain] mainContainer is NULL -> FXML inject failed");
                return;
            }

            ensureCartService();

            URL fxml = getClass().getResource("/UI/customer/customer_items.fxml");
            if (fxml == null) {
                showError("Không tìm thấy FXML: /UI/customer/customer_items.fxml");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxml);
            Parent page = loader.load();
            Object controller = loader.getController();

            if (controller == null) {
                showError("Controller = null (check fx:controller trong customer_items.fxml)");
                return;
            }

            // ✅ Inject mềm: controller con có gì thì set cái đó
            injectCommon(controller);

            mainContainer.setCenter(page);
            updateCartCount();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Không thể tải trang sản phẩm: " + e.getMessage());
        }
    }

    private void loadOrdersPage() {
        try {
            if (mainContainer == null) return;

            URL fxml = getClass().getResource("/UI/customer/customer_orders.fxml");
            if (fxml == null) {
                showError("Không tìm thấy FXML: /UI/customer/customer_orders.fxml");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxml);
            Parent page = loader.load();
            Object controller = loader.getController();

            if (controller != null) {
                injectCommon(controller);
                // thường Orders chỉ cần orderService + accountId, injectCommon đã lo
            }

            mainContainer.setCenter(page);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Không thể tải trang đơn hàng: " + e.getMessage());
        }
    }

    private void loadCartPage() {
        try {
            if (mainContainer == null) return;

            ensureCartService();

            URL fxml = getClass().getResource("/UI/customer/customer_cart.fxml");
            if (fxml == null) {
                showError("Không tìm thấy FXML: /UI/customer/customer_cart.fxml");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxml);
            Parent page = loader.load();
            Object controller = loader.getController();

            if (controller != null) {
                injectCommon(controller);

                // ✅ Một số CartController của bạn dùng setServices(orderService,itemService,cartService)
                invokeIfExists(controller, "setServices",
                        new Class[]{OrderService.class, ItemService.class, CartService.class},
                        new Object[]{orderService, itemService, cartService});

                // ✅ Một số CartController cần loadCart() sau khi inject
                invokeIfExists(controller, "loadCart", new Class[]{}, new Object[]{});
            }

            mainContainer.setCenter(page);
            updateCartCount();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Không thể tải trang giỏ hàng: " + e.getMessage());
        }
    }

    // ================= INJECTION (ROBUST) =================

    /**
     * Inject những thứ hay dùng: mainController, services, currentAccountId.
     * Dùng reflection để tránh lỗi do khác tên setter giữa các controller con.
     */
    private void injectCommon(Object controller) {
        // Main controller setter: setMainController(...) hoặc setCustomerMainController(...)
        invokeIfExists(controller, "setMainController",
                new Class[]{CustomerMainController.class}, new Object[]{this});
        invokeIfExists(controller, "setCustomerMainController",
                new Class[]{CustomerMainController.class}, new Object[]{this});

        // Services: setItemService, setCartService, setOrderService
        invokeIfExists(controller, "setItemService",
                new Class[]{ItemService.class}, new Object[]{itemService});
        invokeIfExists(controller, "setCartService",
                new Class[]{CartService.class}, new Object[]{cartService});
        invokeIfExists(controller, "setOrderService",
                new Class[]{OrderService.class}, new Object[]{orderService});

        // accountId: setCurrentAccountId(Long)
        invokeIfExists(controller, "setCurrentAccountId",
                new Class[]{Long.class}, new Object[]{currentAccountId});
    }

    private void invokeIfExists(Object target, String methodName, Class<?>[] paramTypes, Object[] args) {
        if (target == null) return;
        try {
            Method m = target.getClass().getMethod(methodName, paramTypes);
            m.invoke(target, args);
        } catch (NoSuchMethodException ignored) {
            // controller không có method này -> bỏ qua
        } catch (Exception e) {
            System.out.println("[DEBUG][CustomerMain] invoke fail: "
                    + target.getClass().getSimpleName() + "." + methodName + " -> " + e.getMessage());
        }
    }

    // ================= CART COUNT =================

    public void updateCartCount() {
        try {
            if (lblCartCount == null) return;
            ensureCartService();
            if (cartService == null) return;

            if (currentAccountId == null) {
                lblCartCount.setText("0");
                return;
            }

            var cart = cartService.getOrCreateCart(currentAccountId);
            int count = cartService.getCartItemCount(cart.getId());
            lblCartCount.setText(String.valueOf(count));

        } catch (Exception e) {
            System.out.println("[DEBUG][CustomerMain] updateCartCount error: " + e.getMessage());
        }
    }

    // ================= GETTERS / SETTERS =================

    public ItemService getItemService() { return itemService; }
    public CartService getCartService() { return cartService; }
    public OrderService getOrderService() { return orderService; }
    public Long getCurrentAccountId() { return currentAccountId; }
    public BorderPane getMainContainer() { return mainContainer; }

    public void setCurrentAccountId(Long currentAccountId) {
        this.currentAccountId = currentAccountId;
        System.out.println("[DEBUG][CustomerMain] setCurrentAccountId: " + currentAccountId);
        updateCartCount();
    }

    // ================= UI HELP =================

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
