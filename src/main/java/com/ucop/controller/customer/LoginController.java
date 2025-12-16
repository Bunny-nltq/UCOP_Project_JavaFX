package com.ucop.controller.customer;

import com.ucop.controller.admin.AdminDashboardController;
import com.ucop.controller.staff.StaffDashboardController;
import com.ucop.entity.Role;
import com.ucop.entity.User;
import com.ucop.service.UserService;
import com.ucop.util.HashUtil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class LoginController {

    @FXML private TextField txtUser;
    @FXML private PasswordField txtPass;
    @FXML private Label lblMsg;

    private final UserService userService = new UserService();

    @FXML
    public void handleLogin(ActionEvent e) {

        String input = txtUser.getText().trim();
        String pass = txtPass.getText().trim();

        User user = userService.findByUsernameOrEmail(input);

        if (user == null) {
            lblMsg.setText("User not found!");
            return;
        }

        String hashed = HashUtil.sha256(pass);

        if (!hashed.equals(user.getPassword())) {
            lblMsg.setText("Wrong password!");
            return;
        }

        // Lấy role đầu tiên của user
        String roleName = user.getRoles()
                .stream()
                .map(Role::getName)
                .map(String::toUpperCase)
                .findFirst()
                .orElse("CUSTOMER");

        try {
            FXMLLoader loader;
            Parent root;

            switch (roleName) {

                case "ADMIN":
                    loader = new FXMLLoader(getClass().getResource("/UI/admin/admin_dashboard.fxml"));
                    root = loader.load();
                    AdminDashboardController adminCtrl = loader.getController();
                    adminCtrl.setAdmin(user);
                    break;

                case "STAFF":
                    loader = new FXMLLoader(getClass().getResource("/UI/staff/staff_dashboard.fxml"));
                    root = loader.load();
                    StaffDashboardController staffCtrl = loader.getController();
                    staffCtrl.setStaff(user);
                    break;

                default:
                    loader = new FXMLLoader(getClass().getResource("/UI/customer/customer_dashboard.fxml"));
                    root = loader.load();
                    CustomerDashboardController cusCtrl = loader.getController();
                    cusCtrl.setCustomer(user);
                    break;
            }

            Stage stage = (Stage) txtUser.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception ex) {
            ex.printStackTrace();
            lblMsg.setText("Cannot load dashboard!");
        }
    }

    @FXML
    public void goRegister(ActionEvent e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/UI/customer/customer_register.fxml"));
            Stage stage = (Stage) txtUser.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) {
            ex.printStackTrace();
            lblMsg.setText("Cannot open register screen!");
        }
    }
}