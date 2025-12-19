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

    private Long currentAccountId;

    // ✅ NEW: Dashboard inject accountId vào đây
    public void setCurrentAccountId(Long id) {
        this.currentAccountId = id;
        loadProfileSafe();
    }

    // ✅ giữ lại cho tương thích nếu bạn vẫn truyền User trực tiếp ở đâu đó
    public void setUser(User user) {
        this.user = user;
        this.currentAccountId = (user != null && user.getId() != null)
                ? Long.valueOf(user.getId().toString())
                : null;
        loadProfileSafe();
    }

    private void loadProfileSafe() {
        try {
            if (lblMsg != null) lblMsg.setText("");

            // Nếu chưa có user thì tự load theo accountId
            if (user == null) {
                if (currentAccountId == null) {
                    showError("Thiếu accountId, không thể tải hồ sơ.");
                    return;
                }

                // ⚠️ UserService của bạn phải có hàm findById(...)
                // Nếu tên hàm khác, bạn đổi ở đây cho đúng.
                user = userService.findById(currentAccountId);
                if (user == null) {
                    showError("Không tìm thấy user theo accountId=" + currentAccountId);
                    return;
                }
            }

            // UI: Email
            if (txtEmail != null) {
                txtEmail.setText(user.getEmail() != null ? user.getEmail() : "");
                txtEmail.setDisable(true);
            }

            // Load profile
            profile = profileService.findByUserId(user.getId());

            if (profile != null) {
                if (txtFullname != null) txtFullname.setText(nvl(profile.getFullName()));
                if (txtPhone != null) txtPhone.setText(nvl(profile.getPhone()));
                if (txtAddress != null) txtAddress.setText(nvl(profile.getAddress()));
            } else {
                profile = new AccountProfile();
                profile.setUser(user);

                if (txtFullname != null) txtFullname.setText("");
                if (txtPhone != null) txtPhone.setText("");
                if (txtAddress != null) txtAddress.setText("");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Lỗi khi tải hồ sơ: " + ex.getMessage());
        }
    }

    @FXML
    public void updateProfile() {
        try {
            if (user == null) {
                showError("Bạn chưa đăng nhập / chưa tải được user.");
                return;
            }
            if (profile == null) {
                profile = new AccountProfile();
                profile.setUser(user);
            }

            profile.setFullName(txtFullname != null ? txtFullname.getText() : null);
            profile.setPhone(txtPhone != null ? txtPhone.getText() : null);
            profile.setAddress(txtAddress != null ? txtAddress.getText() : null);

            profileService.saveOrUpdate(profile);

            if (lblMsg != null) {
                lblMsg.setStyle("-fx-text-fill: green;");
                lblMsg.setText("Bạn đã cập nhật thành công!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Lỗi khi cập nhật: " + ex.getMessage());
        }
    }

    private void showError(String msg) {
        if (lblMsg != null) {
            lblMsg.setStyle("-fx-text-fill: red;");
            lblMsg.setText(msg);
        }
    }

    private String nvl(String s) {
        return s == null ? "" : s;
    }
}
