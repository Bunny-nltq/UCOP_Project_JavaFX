package com.ucop.controller.admin;

import com.ucop.entity.User;
import com.ucop.service.UserService;
import javafx.collections.FXCollections;
import javafx.fxml.*;
import javafx.scene.control.*;

public class UserController {

    @FXML private TableView<User> tblUsers;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colEmail;

    private final UserService service = new UserService();

    @FXML
    public void initialize(){
        loadData();
    }

    private void loadData(){
        tblUsers.setItems(FXCollections.observableArrayList(service.findAll()));

        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getId()));
        colUsername.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getUsername()));
        colEmail.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));
    }
}
