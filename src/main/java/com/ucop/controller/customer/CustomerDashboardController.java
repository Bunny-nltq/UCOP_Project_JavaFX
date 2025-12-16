package com.ucop.controller.customer;

import com.ucop.entity.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class CustomerDashboardController {

    @FXML private Button btnLogout; // Hoặc bất kỳ UI element nào
    private User currentUser;

    public void setCustomer(User user) {
        this.currentUser = user;
    }

    @FXML
    public void logout() {
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource("/UI/customer/customer_login.fxml")
            );

            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}