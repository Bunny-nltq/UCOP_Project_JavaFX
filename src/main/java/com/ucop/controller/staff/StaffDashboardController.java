package com.ucop.controller.staff;

import com.ucop.entity.User;

import com.ucop.repository.CartRepository;
import com.ucop.repository.OrderRepository;
import com.ucop.repository.ShipmentRepository;
import com.ucop.repository.StockItemRepository;
import com.ucop.repository.impl.CartRepositoryImpl;
import com.ucop.repository.impl.OrderRepositoryImpl;
import com.ucop.repository.impl.ShipmentRepositoryImpl;
import com.ucop.repository.impl.StockItemRepositoryImpl;

import com.ucop.service.OrderService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class StaffDashboardController {

    @FXML private StackPane contentArea;
    @FXML private Label lblStaff;

    private User staff;

    // ===== Services (create once) =====
    private static SessionFactory SF;

    private static SessionFactory getSessionFactory() {
        if (SF == null) {
            SF = new Configuration().configure().buildSessionFactory();
        }
        return SF;
    }

    private OrderService orderService;

    private OrderService getOrderService() {
        if (orderService == null) {
            SessionFactory sf = getSessionFactory();

            CartRepository cartRepository = new CartRepositoryImpl(sf);
            OrderRepository orderRepository = new OrderRepositoryImpl(sf);
            StockItemRepository stockItemRepository = new StockItemRepositoryImpl(sf);
            ShipmentRepository shipmentRepository = new ShipmentRepositoryImpl(sf);

            orderService = new OrderService(orderRepository, cartRepository, stockItemRepository, shipmentRepository);
        }
        return orderService;
    }

    /** Nhận user từ LoginController */
    public void setStaff(User user) {
        this.staff = user;
        if (lblStaff != null && user != null) {
            lblStaff.setText("Hello, " + user.getUsername());
        }
    }

    /** Generic loader for staff UI pages */
    private void loadView(String fileName) {
        String path = "/UI/staff/" + fileName;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent ui = loader.load();

            // ✅ Inject services for specific controllers
            Object ctrl = loader.getController();
            injectToChildController(ctrl);

            contentArea.getChildren().setAll(ui);

        } catch (Exception e) {
            System.out.println("❌ Cannot load: " + path);
            e.printStackTrace();
        }
    }

    private void injectToChildController(Object ctrl) {
        if (ctrl == null) return;

        // ✅ Orders page: inject OrderService để load DB lên bảng
        if (ctrl instanceof StaffOrderController c) {
            c.setOrderService(getOrderService());
            // setOrderService() sẽ tự gọi loadOrders() sau initialize
        }
    }

    // ================= MENU ==================
    @FXML public void openOrders() { loadView("staff_orders.fxml"); }

    @FXML
    public void openCategoryManager() {
        loadAdminModule("category_manager.fxml");
    }

    @FXML
    public void openItemManager() {
        loadAdminModule("items_manager.fxml");
    }

    @FXML
    public void openPromotions() {
        loadAdminModule("staff_promotions.fxml");
    }

    private void loadAdminModule(String fileName) {
        String path = "/UI/staff/" + fileName;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent ui = loader.load();
            contentArea.getChildren().setAll(ui);

        } catch (Exception e) {
            System.out.println("❌ Cannot load staff module: " + path);
            e.printStackTrace();
        }
    }

    // ================= LOGOUT ==================
    @FXML
    public void logout() {
        try {
            Parent login = FXMLLoader.load(getClass().getResource("/UI/customer/customer_login.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(login));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
