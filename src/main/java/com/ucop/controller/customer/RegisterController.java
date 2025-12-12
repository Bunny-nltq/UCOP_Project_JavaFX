package com.ucop.controller.customer;

import com.ucop.entity.Role;
import com.ucop.entity.User;
import com.ucop.service.RoleService;
import com.ucop.service.UserService;
import com.ucop.util.HashUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirm;
    @FXML private TextField txtEmail;
    @FXML private Label lblMsg;

    private final UserService userService = new UserService();
    private final RoleService roleService = new RoleService();

    @FXML
    public void handleRegister(ActionEvent e){
        try {

            if (txtUsername.getText().isEmpty() ||
                txtPassword.getText().isEmpty() ||
                txtConfirm.getText().isEmpty() ||
                txtEmail.getText().isEmpty()) {

                lblMsg.setText("Vui lòng nhập đầy đủ!");
                return;
            }

            if (!txtPassword.getText().equals(txtConfirm.getText())) {
                lblMsg.setText("Password không khớp!");
                return;
            }

            // Validate email format
            if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                lblMsg.setText("Email không hợp lệ!");
                return;
            }

            if (userService.findByUsernameOrEmail(txtUsername.getText()) != null || userService.findByUsernameOrEmail(txtEmail.getText()) != null) {
                lblMsg.setText("Username hoặc email đã tồn tại!");
                return;
            }

            User u = new User();
            u.setUsername(txtUsername.getText());
            u.setPassword(HashUtil.sha256(txtPassword.getText()));
            u.setEmail(txtEmail.getText());
            u.setActive(true);
            u.setLocked(false);

            // GÁN ROLE CUSTOMER MẶC ĐỊNH
            Role customer = roleService.findByName("CUSTOMER");
            if (customer != null) {
                u.getRoles().add(customer);
            }

            userService.save(u);

            lblMsg.setStyle("-fx-text-fill: green;");
            lblMsg.setText("Đăng ký thành công!");

        } catch(Exception ex){
            lblMsg.setText("Lỗi đăng ký: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    public void goLogin(ActionEvent e){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("/UI/customer/customer_login.fxml"));
            Stage st = (Stage)((Node)e.getSource()).getScene().getWindow();
            st.setScene(new Scene(root));
        } catch(Exception ex){
            lblMsg.setText("Không mở được màn Login!");
            ex.printStackTrace();
        }
    }
}
