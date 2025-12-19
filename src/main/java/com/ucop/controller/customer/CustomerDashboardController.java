package com.ucop.controller.customer;

import com.ucop.entity.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class CustomerDashboardController {

    @FXML
    private StackPane contentArea;

    private User currentUser;
    private Long currentAccountId;

    /* ================= INIT ================= */
    @FXML
    public void initialize() {
        // Chỉ load mặc định nếu chưa có user (tránh load trước khi Login inject)
        // Sau login, LoginController gọi setCurrentUser + setCurrentAccountId rồi openItems()
        // => UI sẽ có accountId
    }

    /* ================= RECEIVE USER ================= */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    // ✅ FIX: nhận accountId từ LoginController
    public void setCurrentAccountId(Long id) {
        this.currentAccountId = id;
    }

    /* ================= LOAD VIEW (FIX) ================= */
    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/UI/customer/" + fxml)
            );
            Parent ui = loader.load();

            // ✅ Inject accountId cho các controller con nếu có setter
            Object controller = loader.getController();
            injectAccountId(controller);

            contentArea.getChildren().setAll(ui);
            StackPane.setAlignment(ui, Pos.TOP_LEFT);

        } catch (Exception e) {
            e.printStackTrace();
            contentArea.getChildren().setAll(new Label("Không thể tải: " + fxml));
        }
    }

    // ✅ Tự inject accountId cho các controller con
    private void injectAccountId(Object controller) {
        if (controller == null) return;

        // Inject vào CustomerItemsController
        if (controller instanceof CustomerItemsController itemsCtrl) {
            itemsCtrl.setCurrentAccountId(currentAccountId);
        }

        // Inject vào CustomerCartController (nếu bạn có setter này)
        if (controller instanceof CustomerCartController cartCtrl) {
            cartCtrl.setCurrentAccountId(currentAccountId);
        }

        // Inject vào CustomerOrderController (nếu có)
        if (controller instanceof CustomerOrderController orderCtrl) {
            orderCtrl.setCurrentAccountId(currentAccountId);
        }

        // Profile controller (nếu có)
        if (controller instanceof CustomerProfileController profileCtrl) {
            profileCtrl.setCurrentAccountId(currentAccountId);
        }
    }

    /* ================= MENU ACTION ================= */
    @FXML
    public void openItems() {
        loadView("customer_items.fxml");
    }

    @FXML
    public void openCart() {
        loadView("customer_cart.fxml");
    }

    @FXML
    public void openOrders() {
        loadView("customer_orders.fxml");
    }

    @FXML
    public void openProfile() {
        loadView("customer_profile.fxml");
    }

    /* ================= LOGOUT ================= */
    @FXML
    public void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/UI/customer/customer_login.fxml")
            );
            Parent loginUI = loader.load();
            contentArea.getScene().setRoot(loginUI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
