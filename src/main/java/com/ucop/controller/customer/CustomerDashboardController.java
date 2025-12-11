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

    /** --- Nhận user sau khi login --- */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /** --- Hàm load FXML chung --- */
    private void loadView(String fileName) {
        String path = "/UI/customer/" + fileName;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent ui = loader.load();

            // Nếu mở trang profile → truyền User
            if (fileName.equals("customer_profile.fxml")) {
                Object controller = loader.getController();
                if (controller instanceof CustomerProfileController profileCtrl) {
                    profileCtrl.setUser(currentUser);
                }
            }

            // Set UI vào content area
            contentArea.getChildren().setAll(ui);

            System.out.println("✓ Loaded: " + path);

        } catch (Exception e) {
            System.out.println("❌ Không thể load FXML: " + path);
            e.printStackTrace();
        }
    }

    // ================== MENU ==================
    @FXML public void openItems()   { loadView("customer_items.fxml"); }
    @FXML public void openCart()    { loadView("customer_cart.fxml"); }
    @FXML public void openOrders()  { loadView("customer_orders.fxml"); }
    @FXML public void openProfile() { loadView("customer_profile.fxml"); }

    // ================== LOGOUT ==================
    @FXML
    public void logout() {
        try {
            Parent loginUI = FXMLLoader.load(getClass().getResource("/UI/customer/customer_login.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(loginUI));
            stage.show();

            System.out.println("✓ Customer logged out.");

        } catch (Exception e) {
            System.out.println("❌ Logout failed!");
            e.printStackTrace();
        }
    }
}
