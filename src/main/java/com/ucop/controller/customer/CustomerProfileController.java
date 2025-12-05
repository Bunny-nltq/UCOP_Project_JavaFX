package com.ucop.controller.customer;

import com.ucop.entity.AccountProfile;
import com.ucop.entity.User;
import com.ucop.service.ProfileService;
import com.ucop.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CustomerProfileController {

    @FXML private TextField txtFullname;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private TextField txtAddress;
    @FXML private Label lblMsg;

    private final ProfileService profileService = new ProfileService();
    private final UserService userService = new UserService();

    private User user;
    private AccountProfile profile;

    // Dashboard truyền user vào đây
    public void setUser(User user) {
        this.user = user;
        loadProfile();
    }

    private void loadProfile() {
        txtEmail.setText(user.getEmail());
        txtEmail.setDisable(true);  // khóa email

        profile = profileService.findByUserId(user.getId());

        if (profile != null) {
            txtFullname.setText(profile.getFullName());
            txtPhone.setText(profile.getPhone());
            txtAddress.setText(profile.getAddress());
        } else {
            profile = new AccountProfile();
            profile.setUser(user);
        }
    }

    @FXML
    public void updateProfile() {
        try {
            profile.setFullName(txtFullname.getText());
            profile.setPhone(txtPhone.getText());
            profile.setAddress(txtAddress.getText());

            profileService.saveOrUpdate(profile);

            lblMsg.setStyle("-fx-text-fill: green;");
            lblMsg.setText("Bạn đã cập nhật thành công!");

        } catch (Exception ex) {
            lblMsg.setStyle("-fx-text-fill: red;");
            lblMsg.setText("Lỗi khi cập nhật!");
            ex.printStackTrace();
        }
    }
}
