package com.ucop.controller.customer;

import com.ucop.entity.Role;
import com.ucop.entity.User;
import com.ucop.service.RoleService;
import com.ucop.service.UserService;
import com.ucop.util.HashUtil;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
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

            if (userService.findByUsernameOrEmail(txtUsername.getText()) != null) {
                lblMsg.setText("Username đã tồn tại!");
                return;
            }

            User u = new User();
            u.setUsername(txtUsername.getText());
            u.setPassword(HashUtil.sha256(txtPassword.getText()));
            u.setEmail(txtEmail.getText());
            u.setActive(true);

            // GÁN ROLE CUSTOMER MẶC ĐỊNH
            Role customer = roleService.findByName("CUSTOMER");
            if (customer != null) {
                u.getRoles().add(customer);
            }

            userService.save(u);

            lblMsg.setStyle("-fx-text-fill: green;");
            lblMsg.setText("Đăng ký thành công!");

        } catch(Exception ex){
            lblMsg.setText("Lỗi đăng ký!");
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
