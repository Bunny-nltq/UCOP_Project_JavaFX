package com.ucop.controller.staff;

import com.ucop.entity.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class StaffDashboardController {

    @FXML private StackPane contentArea;
    @FXML private Label lblStaff;

    private User staff;

    /** Nhận user từ LoginController */
    public void setStaff(User user) {
        this.staff = user;
        if (lblStaff != null) {
            lblStaff.setText("Hello, " + user.getUsername());
        }
    }

    /** Generic loader for staff UI pages */
    private void loadView(String fileName) {
        String path = "/UI/staff/" + fileName;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent ui = loader.load();
            contentArea.getChildren().setAll(ui);

        } catch (Exception e) {
            System.out.println("❌ Cannot load: " + path);
            e.printStackTrace();
        }
    }

    // ================= MENU ==================

    @FXML public void openOrders()     { loadView("staff_orders.fxml"); }
    @FXML public void openTickets()    { loadView("staff_tickets.fxml"); }
    @FXML public void openWarehouse()  { loadView("staff_warehouse.fxml"); }
    @FXML public void openPayments()   { loadView("staff_payments.fxml"); }
    @FXML public void openShipment()   { loadView("staff_shipment.fxml"); }
    @FXML public void openRefunds()    { loadView("staff_refund.fxml"); }

    // ============ NEW: CATEGORY & ITEM MANAGEMENT ============

    /** Mở giao diện quản lý danh mục */
    @FXML
    public void openCategoryManager() {
        loadAdminModule("category_manager.fxml");
    }

    /** Mở giao diện quản lý sản phẩm */
    @FXML
    public void openItemManager() {
        loadAdminModule("items_manager.fxml");
    }

    /** Load các FXML của admin (vì category & items nằm trong thư mục admin) */
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
