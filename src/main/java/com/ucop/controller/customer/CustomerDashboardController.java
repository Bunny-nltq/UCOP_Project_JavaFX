package com.ucop.controller.customer;

import com.ucop.entity.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class CustomerDashboardController {

    @FXML private StackPane contentArea;

    private User currentUser;

    // ← nhận user từ LoginController
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private void loadView(String fileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/customer/" + fileName));
            Parent ui = loader.load();

            // Nếu form là profile → truyền user
            if (fileName.contains("profile")) {
                CustomerProfileController profileCtrl = loader.getController();
                profileCtrl.setUser(currentUser);
            }

            contentArea.getChildren().setAll(ui);
        } catch (Exception e) {
            System.out.println("Cannot load: " + fileName);
            e.printStackTrace();
        }
    }

    @FXML public void openItems()  { loadView("customer_items.fxml"); }
    @FXML public void openCart()   { loadView("customer_cart.fxml"); }
    @FXML public void openOrders() { loadView("customer_orders.fxml"); }
    @FXML public void openProfile(){ loadView("customer_profile.fxml"); }

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
