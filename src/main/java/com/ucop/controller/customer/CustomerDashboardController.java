package com.ucop.controller.customer;

import com.ucop.entity.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class CustomerDashboardController {

    @FXML private StackPane contentArea;

    private User currentUser;

    /**
     * Initialize - tự động load trang sản phẩm
     */
    @FXML
    public void initialize() {
        // Load trang sản phẩm mặc định khi dashboard mở
        loadView("customer-products.fxml");
    }

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
            contentArea.getChildren().clear();
            contentArea.getChildren().add(ui);
            
            // Đảm bảo UI fill toàn bộ content area
            StackPane.setAlignment(ui, Pos.TOP_LEFT);

            System.out.println("✓ Loaded: " + path);

        } catch (Exception e) {
            System.out.println("❌ Không thể load FXML: " + path);
            e.printStackTrace();
            
            // Hiển thị thông báo lỗi
            Label errorLabel = new Label(
                "Không thể tải trang: " + fileName + "\n" + e.getMessage()
            );
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px; -fx-padding: 20px;");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(errorLabel);
        }
    }

    // ================== MENU ==================
    @FXML public void openItems()   { loadView("customer-products.fxml"); }  // Sử dụng file mới với Product entity
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
