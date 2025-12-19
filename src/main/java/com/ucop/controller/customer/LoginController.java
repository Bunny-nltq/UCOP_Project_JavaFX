package com.ucop.controller.customer;

import com.ucop.controller.admin.AdminDashboardController;
import com.ucop.controller.staff.StaffDashboardController;
import com.ucop.entity.Role;
import com.ucop.entity.User;

import com.ucop.repository.CartRepository;
import com.ucop.repository.OrderRepository;
import com.ucop.repository.ShipmentRepository;
import com.ucop.repository.StockItemRepository;
import com.ucop.repository.impl.CartRepositoryImpl;
import com.ucop.repository.impl.OrderRepositoryImpl;
import com.ucop.repository.impl.ShipmentRepositoryImpl;
import com.ucop.repository.impl.StockItemRepositoryImpl;

import com.ucop.service.CartService;
import com.ucop.service.ItemService;
import com.ucop.service.OrderService;
import com.ucop.service.UserService;

import com.ucop.util.HashUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class LoginController {

    @FXML private TextField txtUser;
    @FXML private PasswordField txtPass;
    @FXML private Label lblMsg;

    private final UserService userService = new UserService();

    // ✅ Không dùng HibernateUtil: tự tạo SessionFactory 1 lần cho toàn app
    private static SessionFactory SF;

    private static SessionFactory getSessionFactory() {
        if (SF == null) {
            // cần có src/main/resources/hibernate.cfg.xml
            SF = new Configuration().configure().buildSessionFactory();
        }
        return SF;
    }

    @FXML
    public void handleLogin(ActionEvent e) {

        String input = (txtUser.getText() == null) ? "" : txtUser.getText().trim();
        String pass  = (txtPass.getText() == null) ? "" : txtPass.getText().trim();

        if (input.isEmpty() || pass.isEmpty()) {
            lblMsg.setText("Vui lòng nhập tài khoản và mật khẩu!");
            return;
        }

        User user = userService.findByUsernameOrEmail(input);
        if (user == null) {
            lblMsg.setText("User not found!");
            return;
        }

        String hashed = HashUtil.sha256(pass);
        if (user.getPassword() == null || !hashed.equals(user.getPassword())) {
            lblMsg.setText("Wrong password!");
            return;
        }

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

                    // ✅ tạo và inject services để Cart/Orders không còn báo thiếu
                    SessionFactory sf = getSessionFactory();

                    ItemService itemService = new ItemService();

                    CartRepository cartRepository = new CartRepositoryImpl(sf);
                    OrderRepository orderRepository = new OrderRepositoryImpl(sf);
                    StockItemRepository stockItemRepository = new StockItemRepositoryImpl(sf);
                    ShipmentRepository shipmentRepository = new ShipmentRepositoryImpl(sf);

                    CartService cartService = new CartService(cartRepository);
                    OrderService orderService = new OrderService(
                            orderRepository,
                            cartRepository,
                            stockItemRepository,
                            shipmentRepository
                    );

                    cusCtrl.setItemService(itemService);
                    cusCtrl.setCartService(cartService);
                    cusCtrl.setOrderService(orderService);

                    // set user + accountId (giữ logic của bạn)
                    cusCtrl.setCurrentUser(user);

                    Long accountId = null;
                    try {
                        if (user.getId() != null) accountId = Long.valueOf(user.getId().toString());
                    } catch (Exception ignore) {}

                    cusCtrl.setCurrentAccountId(accountId);
                    break;
            }

            Stage stage = (Stage) txtUser.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

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
            stage.centerOnScreen();
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            lblMsg.setText("Cannot open register screen!");
        }
    }
}
