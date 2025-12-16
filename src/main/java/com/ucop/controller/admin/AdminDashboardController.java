package com.ucop.controller.admin;

import com.ucop.entity.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AdminDashboardController {

    @FXML private StackPane contentArea;
    @FXML private Label lblAdmin;

    private User admin;

    /** SET USER SAU LOGIN */
    public void setAdmin(User user) {
        this.admin = user;
        lblAdmin.setText("Hello, " + (user != null ? user.getUsername() : "Admin"));
    }

    /** LOAD VIEW DÙNG CHUNG */
    private void loadView(String fileName) {
        String path = "/UI/admin/" + fileName;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent ui = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(ui);

            System.out.println("✓ Loaded: " + path);

        } catch (Exception e) {
            System.out.println("❌ Cannot load: " + path);
            e.printStackTrace();
        }
    }

    // ================= MENU HÀM BẮT SỰ KIỆN =================

    @FXML public void showPromotionManagement()     { loadView("promotion_manager.fxml"); }
    @FXML public void openUserMgmt()       { loadView("user_manager.fxml"); }
    @FXML public void openAudit() {loadView("auditLog_manager.fxml");}
    @FXML public void showRevenueReport()           { loadView("report_manager.fxml"); }


    @FXML public void showWarehouseManagement()     { loadView("warehouse_manager.fxml"); }
    @FXML public void showOrderManagement()         { loadView("order_manager.fxml"); }
    @FXML public void exportReport()                { loadView("export_report.fxml"); }

    @FXML public void refreshDashboard()            { loadView("dashboard_view.fxml"); }

    /** LOGOUT */
    @FXML
    public void logout() {
        try {
            Parent loginUI = FXMLLoader.load(
                    getClass().getResource("/UI/customer/customer_login.fxml")
            );

            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(loginUI));
            stage.show();

        } catch (Exception e) {
            System.out.println("❌ Logout failed!");
            e.printStackTrace();
        }
    }
}
