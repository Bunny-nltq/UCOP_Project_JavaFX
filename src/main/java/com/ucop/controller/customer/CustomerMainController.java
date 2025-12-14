package com.ucop.controller.customer;

import com.ucop.service.ProductService;
import com.ucop.service.OrderService;
import com.ucop.service.PromotionService;
import com.ucop.repository.*;
import com.ucop.repository.impl.*;
import com.ucop.util.HibernateUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;

import java.io.IOException;

/**
 * Main Controller cho Customer UI
 * Quản lý navigation giữa các trang customer
 */
public class CustomerMainController {

    @FXML private BorderPane mainContainer;
    @FXML private Label lblWelcome;
    @FXML private Label lblCartCount;

    private ProductService productService;
    private OrderService orderService;
    private PromotionService promotionService;
    private Long currentAccountId = 1L; // TODO: Get from login session

    public void initialize() {
        // Initialize services
        initializeServices();
        
        // Set welcome message
        if (lblWelcome != null) {
            lblWelcome.setText("Chào mừng, Khách hàng!");
        }
        
        // Load products page by default
        loadProductsPage();
    }

    /**
     * Initialize all services with repositories
     */
    private void initializeServices() {
        // Get SessionFactory
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        
        // Initialize repositories
        ProductRepository productRepository = new ProductRepositoryImpl(sessionFactory);
        OrderRepository orderRepository = new OrderRepositoryImpl(sessionFactory);
        CartRepository cartRepository = new CartRepositoryImpl(sessionFactory);
        StockItemRepository stockItemRepository = new StockItemRepositoryImpl(sessionFactory);
        ShipmentRepository shipmentRepository = new ShipmentRepositoryImpl(sessionFactory);
        PromotionRepository promotionRepository = new PromotionRepository(sessionFactory);
        PromotionUsageRepository promotionUsageRepository = new PromotionUsageRepository(sessionFactory);
        
        // Initialize services
        productService = new ProductService(productRepository);
        orderService = new OrderService(orderRepository, cartRepository, stockItemRepository, 
                                        shipmentRepository);
        promotionService = new PromotionService(promotionRepository, promotionUsageRepository);
    }

    /**
     * Load Products page
     */
    @FXML
    private void handleProducts() {
        loadProductsPage();
    }

    private void loadProductsPage() {
        try {
            // Debug: Check if mainContainer is available
            if (mainContainer == null) {
                System.err.println("✗ ERROR: mainContainer is null in CustomerMainController!");
                showError("Lỗi hệ thống: Không thể tải giao diện.");
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/customer/customer-products.fxml"));
            Parent page = loader.load();
            
            // Get controller and pass mainContainer
            CustomerProductControllerV2 controller = loader.getController();
            if (controller != null) {
                controller.setMainContainer(mainContainer);
                System.out.println("✓ Set mainContainer for CustomerProductControllerV2");
            } else {
                System.err.println("✗ WARNING: CustomerProductControllerV2 is null!");
            }
            
            mainContainer.setCenter(page);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Không thể tải trang sản phẩm: " + e.getMessage());
        }
    }

    /**
     * Load My Orders page
     */
    @FXML
    private void handleMyOrders() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/customer/customer-orders.fxml"));
            Parent page = loader.load();
            
            // Inject services into controller
            CustomerOrderController controller = loader.getController();
            controller.setOrderService(orderService);
            controller.setAccountId(currentAccountId);
            
            mainContainer.setCenter(page);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Không thể tải trang đơn hàng: " + e.getMessage());
        }
    }

    /**
     * View Cart
     */
    @FXML
    private void handleCart() {
        // TODO: Implement cart view
        showInfo("Chức năng giỏ hàng đang được phát triển!");
    }

    /**
     * Go to Admin Dashboard
     */
    @FXML
    private void handleAdminDashboard() {
        try {
            // Load dashboard.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/admin/dashboard.fxml"));
            Parent root = loader.load();
            
            // Get current stage
            Stage stage = (Stage) mainContainer.getScene().getWindow();
            
            // Set new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("UCOP - Admin Dashboard");
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Không thể tải trang Admin Dashboard: " + e.getMessage());
        }
    }

    /**
     * Logout
     */
    @FXML
    private void handleLogout() {
        // TODO: Implement logout
        showInfo("Đăng xuất thành công!");
    }

    private void showInfo(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
