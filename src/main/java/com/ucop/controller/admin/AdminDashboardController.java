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

    /** NHẬN USER SAU LOGIN */
    public void setAdmin(User user) {
        this.admin = user;
        lblAdmin.setText("Hello, " + user.getUsername());
    }

    /** HÀM LOAD CHUNG CHO TẤT CẢ FILE FXML */
    private void loadView(String fileName) {
        String path = "/UI/admin/" + fileName;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent ui = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(ui);

        } catch (Exception e) {
            System.out.println("❌ Failed to load FXML: " + path);
            System.out.println("➡ KIỂM TRA FILE: src/main/resources/UI/admin/" + fileName);
            e.printStackTrace();
        }
    }

    // ---------------- MENU ----------------
    @FXML public void openUserMgmt()       { loadView("user_manager.fxml"); }
    @FXML public void openRoleMgmt()       { loadView("role_manager.fxml"); }
    @FXML public void openConfig()         { loadView("config_manager.fxml"); }
    @FXML public void openPromotion()      { loadView("promotion_manager.fxml"); }
    @FXML public void openReports()        { loadView("report_manager.fxml"); }

    /** FIX TÊN FILE AUDIT LOG — phải trùng EXACT FILE NAME */
    @FXML public void openAudit()          { loadView("auditLog_manager.fxml"); }

    @FXML public void openChangePassword() { loadView("change_password.fxml"); }

    // ---------------- LOGOUT ----------------
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
