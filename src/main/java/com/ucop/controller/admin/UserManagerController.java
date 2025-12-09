package com.ucop.controller.admin;

import com.ucop.entity.AccountProfile;
import com.ucop.entity.Role;
import com.ucop.entity.User;
import com.ucop.service.RoleService;
import com.ucop.service.UserService;
import com.ucop.util.HashUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UserManagerController {

    @FXML private TableView<User> tblUsers;

    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, Boolean> colActive;
    @FXML private TableColumn<User, String> colRole;

    @FXML private TableColumn<User, String> colFullName;
    @FXML private TableColumn<User, String> colPhone;
    @FXML private TableColumn<User, String> colAddress;

    // ===== FORM FIELD =====
    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;

    @FXML private TextField txtFullName;
    @FXML private TextField txtPhone;
    @FXML private TextField txtAddress;

    @FXML private ComboBox<Role> cbRole;
    @FXML private CheckBox chkActive;
    @FXML private Label lblMsg;

    private final UserService userService = new UserService();
    private final RoleService roleService = new RoleService();

    private ObservableList<User> users;
    private User selectedUser = null;

    @FXML
    public void initialize() {
        loadRoles();
        loadUsers();

        tblUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                selectedUser = newV;

                txtUsername.setText(newV.getUsername());
                txtEmail.setText(newV.getEmail());
                chkActive.setSelected(newV.isActive());

                // ROLE
                if (!newV.getRoles().isEmpty()) {
                    cbRole.setValue(newV.getRoles().iterator().next());
                } else {
                    cbRole.setValue(null);
                }

                // PROFILE
                AccountProfile p = newV.getProfile();
                if (p != null) {
                    txtFullName.setText(p.getFullName());
                    txtPhone.setText(p.getPhone());
                    txtAddress.setText(p.getAddress());
                } else {
                    txtFullName.clear();
                    txtPhone.clear();
                    txtAddress.clear();
                }
            }
        });
    }

    private void loadRoles() {
        cbRole.setItems(FXCollections.observableArrayList(roleService.findAll()));
    }

    private void loadUsers() {
        users = FXCollections.observableArrayList(userService.findAll());
        tblUsers.setItems(users);

        colId.setCellValueFactory(param ->
                new javafx.beans.property.SimpleIntegerProperty(param.getValue().getId()).asObject()
        );

        colUsername.setCellValueFactory(param ->
                new javafx.beans.property.SimpleStringProperty(param.getValue().getUsername())
        );

        colEmail.setCellValueFactory(param ->
                new javafx.beans.property.SimpleStringProperty(param.getValue().getEmail())
        );

        colActive.setCellValueFactory(param ->
                new javafx.beans.property.SimpleBooleanProperty(param.getValue().isActive()).asObject()
        );

        colRole.setCellValueFactory(param -> {
            if (!param.getValue().getRoles().isEmpty()) {
                Role r = param.getValue().getRoles().iterator().next();
                return new javafx.beans.property.SimpleStringProperty(r.getName());
            }
            return new javafx.beans.property.SimpleStringProperty("CUSTOMER");
        });

        colFullName.setCellValueFactory(param ->
                new javafx.beans.property.SimpleStringProperty(
                        param.getValue().getProfile() != null ?
                                param.getValue().getProfile().getFullName() : ""
                )
        );

        colPhone.setCellValueFactory(param ->
                new javafx.beans.property.SimpleStringProperty(
                        param.getValue().getProfile() != null ?
                                param.getValue().getProfile().getPhone() : ""
                )
        );

        colAddress.setCellValueFactory(param ->
                new javafx.beans.property.SimpleStringProperty(
                        param.getValue().getProfile() != null ?
                                param.getValue().getProfile().getAddress() : ""
                )
        );
    }

    // ================= ADD USER =================
    @FXML
    public void handleAdd() {
        try {
            User u = new User();
            u.setUsername(txtUsername.getText());
            u.setEmail(txtEmail.getText());
            u.setActive(chkActive.isSelected());

            // default password = 123456
            u.setPassword(HashUtil.sha256("123456"));

            // DEFAULT ROLE = CUSTOMER if user does not choose
            Role role = cbRole.getValue();
            if (role == null) {
                role = roleService.findByName("CUSTOMER");
            }

            u.getRoles().add(role);

            // Create profile
            AccountProfile profile = new AccountProfile();
            profile.setUser(u);
            profile.setFullName(txtFullName.getText());
            profile.setPhone(txtPhone.getText());
            profile.setAddress(txtAddress.getText());

            u.setProfile(profile);

            userService.save(u);

            lblMsg.setText("User added successfully!");
            loadUsers();

        } catch (Exception ex) {
            lblMsg.setText("Error adding user!");
            ex.printStackTrace();
        }
    }

    // ================= UPDATE USER =================
    @FXML
    public void handleUpdate() {
        if (selectedUser == null) {
            lblMsg.setText("Select user first!");
            return;
        }

        try {
            selectedUser.setUsername(txtUsername.getText());
            selectedUser.setEmail(txtEmail.getText());
            selectedUser.setActive(chkActive.isSelected());

            // ROLE UPDATE
            selectedUser.getRoles().clear();
            if (cbRole.getValue() != null) {
                selectedUser.getRoles().add(cbRole.getValue());
            }

            // PROFILE FIX — create profile if missing
            AccountProfile p = selectedUser.getProfile();
            if (p == null) {  
                p = new AccountProfile();
                p.setUser(selectedUser);
                selectedUser.setProfile(p);
            }

            p.setFullName(txtFullName.getText());
            p.setPhone(txtPhone.getText());
            p.setAddress(txtAddress.getText());

            userService.update(selectedUser);

            lblMsg.setText("User updated!");

            loadUsers();

        } catch (Exception ex) {
            lblMsg.setText("Error updating user!");
            ex.printStackTrace();
        }
    }


    // ================= DELETE USER =================
    @FXML
    public void handleDelete() {
        if (selectedUser == null) {
            lblMsg.setText("Select user first!");
            return;
        }

        try {
            userService.delete(selectedUser.getId());
            lblMsg.setText("User deleted!");
            loadUsers();

        } catch (Exception ex) {
            lblMsg.setText("Error deleting user!");
            ex.printStackTrace();
        }
    }
}
