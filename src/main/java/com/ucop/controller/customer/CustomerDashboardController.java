package com.ucop.controller.customer;

import com.ucop.entity.User;
import com.ucop.service.CartService;
import com.ucop.service.ItemService;
import com.ucop.service.OrderService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class CustomerDashboardController {

    @FXML
    private StackPane contentArea;

    private User currentUser;
    private Long currentAccountId;

    // Services được inject từ bên ngoài
    private ItemService itemService;
    private CartService cartService;
    private OrderService orderService;

    @FXML
    public void initialize() {
        System.out.println("[DEBUG] CustomerDashboardController initialize()");
    }

    // ================== INJECT SERVICES ==================
    public void setItemService(ItemService itemService) {
        this.itemService = itemService;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    // LoginController gọi hàm này
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            this.currentAccountId = user.getId() == null ? null : user.getId().longValue();
        }
        openItems();
    }

    public void setCurrentAccountId(Long id) {
        this.currentAccountId = id;
    }

    // ================== LOAD VIEW ==================
    private void loadView(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/customer/" + fxmlName));
            Parent ui = loader.load();

            Object ctrl = loader.getController();
            boolean ok = injectToChildController(ctrl, fxmlName);
            if (!ok) return; // ✅ thiếu service thì không mở trang đó

            contentArea.getChildren().setAll(ui);

        } catch (Exception e) {
            e.printStackTrace();
            contentArea.getChildren().setAll(new Label("Không thể tải: " + fxmlName));
        }
    }

    /**
     * @return true nếu inject OK (được phép hiển thị view), false nếu thiếu service quan trọng.
     */
    private boolean injectToChildController(Object ctrl, String fxmlName) {
        if (ctrl == null) return true;

        // Items
        if (ctrl instanceof CustomerItemsController c) {
            c.setCurrentAccountId(currentAccountId);

            if (itemService != null) c.setItemService(itemService);
            if (cartService != null) c.setCartService(cartService);

            c.setCustomerMainController(this);
            return true;
        }

        // Cart
        if (ctrl instanceof CustomerCartController c) {
            c.setCurrentAccountId(currentAccountId);

            if (cartService == null) {
                warnMissing("CartService", fxmlName);
                return false;
            }
            c.setCartService(cartService);

            if (orderService != null) c.setOrderService(orderService);

            try {
                c.getClass().getMethod("loadCart").invoke(c);
            } catch (Exception ignored) {}

            return true;
        }

        // Orders
        if (ctrl instanceof CustomerOrderController c) {
            if (orderService == null) {
                warnMissing("OrderService", fxmlName);
                return false; // ✅ chặn không cho vào Orders khi thiếu OrderService
            }
            c.setOrderService(orderService);
            c.setCurrentAccountId(currentAccountId);
            return true;
        }

        // Profile
        if (ctrl instanceof CustomerProfileController c) {
            c.setUser(currentUser);
            return true;
        }

        return true;
    }

    private void warnMissing(String serviceName, String fxmlName) {
        System.out.println("[DEBUG] Missing " + serviceName + " when loading: " + fxmlName);
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Warning");
        a.setHeaderText(null);
        a.setContentText("Thiếu " + serviceName + " (chưa inject).");
        a.showAndWait();
    }

    // ================== PUBLIC API cho ItemsController ==================
    public Long getCurrentAccountId() {
        return currentAccountId;
    }

    public void updateCartCount() {
        System.out.println("[DEBUG] updateCartCount() called");
    }

    public OrderService getOrderService() {
        return orderService;
    }

    // ===== MENU ACTIONS =====
    @FXML
    public void openItems() {
        loadView("customer_items.fxml");
    }

    @FXML
    public void openCart() {
        loadView("customer_cart.fxml");
    }

    @FXML
    public void openOrders() {
        loadView("customer_orders.fxml");
    }

    @FXML
    public void openProfile() {
        loadView("customer_profile.fxml");
    }

    public void handleMyOrders() {
        openOrders();
    }

    @FXML
    public void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/customer/customer_login.fxml"));
            Parent loginUI = loader.load();
            contentArea.getScene().setRoot(loginUI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
