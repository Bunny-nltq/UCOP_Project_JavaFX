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

import java.io.IOException;
import java.net.URL;

public class CustomerMainController {

    @FXML private BorderPane mainContainer;
    @FXML private Label lblWelcome;
    @FXML private Label lblCartCount;

    private ItemService itemService;
    private OrderService orderService;
    private CartService cartService;

    // TODO: sau này lấy từ Login, tạm để demo
    private Long currentAccountId = 1L;

    @FXML
    public void initialize() {
        System.out.println("[DEBUG][CustomerMain] initialize()");

        initializeServices();
        ensureCartService();

        System.out.println("[DEBUG][CustomerMain] services: itemService=" + (itemService != null)
                + ", cartService=" + (cartService != null)
                + ", orderService=" + (orderService != null));

        if (lblWelcome != null) {
            lblWelcome.setText("Chào mừng, Khách hàng!");
        }

        // Load UI sau khi scene ready
        Platform.runLater(() -> {
            updateCartCount();
            handleItems();
        });
    }

    private void initializeServices() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        if (sessionFactory == null) throw new IllegalStateException("SessionFactory is null");

        // ItemService tự lấy SessionFactory
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
        var sf = HibernateUtil.getSessionFactory();
        cartService = new CartService(new CartRepositoryImpl(sf));
        System.out.println("[DEBUG][CustomerMain] cartService fallback created=" + (cartService != null));
    }

    // ================= NAV =================

    @FXML
    public void handleItems() {
        loadItemsPage();
    }

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

            CustomerItemsController controller = loader.getController();
            if (controller == null) {
                showError("CustomerItemsController = null (check fx:controller trong customer_items.fxml)");
                return;
            }

            System.out.println("[DEBUG][CustomerMain] itemsController instance = " + controller);

            // ✅ inject đầy đủ
            controller.setCustomerMainController(this);
            controller.setItemService(itemService);
            controller.setCartService(cartService);
            controller.setCurrentAccountId(getCurrentAccountId());

            // ✅ truyền accountId dự phòng (bắt buộc CustomerItemsController phải có setter)
            controller.setCurrentAccountId(currentAccountId);

            mainContainer.setCenter(page);
            updateCartCount();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Không thể tải trang sản phẩm: " + e.getMessage());
        }
    }

    @FXML
    public void handleMyOrders() {
        try {
            if (mainContainer == null) return;

            URL fxml = getClass().getResource("/UI/customer/customer_orders.fxml");
            if (fxml == null) {
                showError("Không tìm thấy FXML: /UI/customer/customer_orders.fxml");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxml);
            Parent page = loader.load();

            CustomerOrderController controller = loader.getController();
            if (controller != null) {
                controller.setOrderService(orderService);
                controller.setCurrentAccountId(currentAccountId);
            }

            mainContainer.setCenter(page);

        } catch (IOException e) {
            showError("Không thể tải trang đơn hàng: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCart() {
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

            CustomerCartController controller = loader.getController();
            if (controller != null) {
                controller.setMainController(this);
                controller.setServices(orderService, itemService, cartService);
                controller.setCurrentAccountId(currentAccountId);
            }

            mainContainer.setCenter(page);
            updateCartCount();

        } catch (IOException e) {
            showError("Không thể tải trang giỏ hàng: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogout() {
        showError("Chức năng đăng xuất chưa triển khai.");
    }

    // ================= CART COUNT =================

    public void updateCartCount() {
        try {
            if (lblCartCount == null) return;
            if (cartService == null) ensureCartService();
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
    public Long getCurrentAccountId() { return currentAccountId; }

    public void setCurrentAccountId(Long currentAccountId) {
        this.currentAccountId = currentAccountId;
        System.out.println("[DEBUG] setCurrentAccountId called: " + currentAccountId);
        updateCartCount();
    }

    public BorderPane getMainContainer() { return mainContainer; }

    // ================= UI HELP =================

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
