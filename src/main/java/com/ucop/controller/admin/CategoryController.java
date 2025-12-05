package com.ucop.controller.admin;

import com.ucop.entity.Category;
import com.ucop.service.CategoryService;
import javafx.collections.FXCollections;
import javafx.fxml.*;
import javafx.scene.control.*;

public class CategoryController {

    @FXML private TextField txtName;
    @FXML private TableView<Category> tbl;
    @FXML private TableColumn<Category, Integer> colId;
    @FXML private TableColumn<Category, String> colName;

    private final CategoryService service = new CategoryService();

    @FXML
    public void initialize(){
        refresh();
    }

    private void refresh(){
        tbl.setItems(FXCollections.observableArrayList(service.findAll()));

        colId.setCellValueFactory(c ->
                new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getId()));

        colName.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
    }

    @FXML
    public void add(){
        Category c = new Category();
        c.setName(txtName.getText());
        service.save(c);
        txtName.clear();
        refresh();
    }
}
