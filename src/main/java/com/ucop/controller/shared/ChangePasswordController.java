package com.ucop.controller.shared;

import com.ucop.entity.User;
import com.ucop.service.UserService;
import com.ucop.util.HashUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;

public class ChangePasswordController {

    @FXML private PasswordField txtOldPass;
    @FXML private PasswordField txtNewPass;
    @FXML private PasswordField txtConfirmPass;
    @FXML private Label lblMsg;

    private final UserService userService = new UserService();

    // Giả sử hệ thống lưu user đang đăng nhập tại đây:
    public static User loggedUser;

    @FXML
    public void handleChange(ActionEvent e){
        try {
            if(loggedUser == null){
                lblMsg.setText("Lỗi: Không xác định được tài khoản!");
                return;
            }

            String oldHash = HashUtil.sha256(txtOldPass.getText());
            String newPass = txtNewPass.getText();
            String confirm = txtConfirmPass.getText();

            // 1. Kiểm tra mật khẩu cũ
            if(!loggedUser.getPassword().equals(oldHash)){
                lblMsg.setText("Mật khẩu cũ không đúng!");
                return;
            }

            // 2. Kiểm tra confirm password
            if(!newPass.equals(confirm)){
                lblMsg.setText("Xác nhận mật khẩu không khớp!");
                return;
            }

            // 3. Lưu mật khẩu mới
            loggedUser.setPassword(HashUtil.sha256(newPass));
            userService.update(loggedUser);

            lblMsg.setText("Đổi mật khẩu thành công!");

            txtOldPass.clear();
            txtNewPass.clear();
            txtConfirmPass.clear();

        } catch(Exception ex){
            lblMsg.setText("Lỗi! Không thể đổi mật khẩu.");
            ex.printStackTrace();
        }
    }

    @FXML
    public void goBack(ActionEvent e){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("/ui/admin_dashboard.fxml"));
            Stage st = (Stage)((Node)e.getSource()).getScene().getWindow();
            st.setScene(new Scene(root));
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
