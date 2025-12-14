package com.ucop.controller.staff;

import com.ucop.entity.Category;
import com.ucop.service.CategoryService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CategoryController {

    @FXML private TextField txtName, txtDescription;
    @FXML private ComboBox<Category> cbParent;

    @FXML private TableView<Category> tbl;
    @FXML private TableColumn<Category, Integer> colId;
    @FXML private TableColumn<Category, String> colName;
    @FXML private TableColumn<Category, String> colDescription;
    @FXML private TableColumn<Category, String> colParent;
    @FXML private TableColumn<Category, Integer> colStatus;

    private final CategoryService service = new CategoryService();
    private Category selected;

    @FXML
    public void initialize() {
        setupComboBox();
        setupTable();
        refresh();
        setRowSelectEvent();
    }

    // ===================== TABLE SETUP ===========================
    private void setupTable() {

        colId.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getId()));

        colName.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getName()));

        colDescription.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getDescription() != null ? c.getValue().getDescription() : ""
                ));

        // Hiển thị tên category cha
        colParent.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getParent() != null
                                ? c.getValue().getParent().getName()
                                : ""
                ));

        colStatus.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getStatus()));
    }

    // ===================== COMBOBOX NAME DISPLAY ===========================
    private void setupComboBox() {
        cbParent.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });

        cbParent.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
    }

    // ===================== LOAD DATA ===========================
    private void refresh() {
        var all = service.findAll();

        tbl.setItems(FXCollections.observableArrayList(all));
        cbParent.setItems(FXCollections.observableArrayList(all));
    }

    // ===================== TABLE ROW SELECT ===========================
    private void setRowSelectEvent() {
        tbl.setOnMouseClicked(e -> {
            Category c = tbl.getSelectionModel().getSelectedItem();
            if (c != null) {
                selected = c;
                txtName.setText(c.getName());
                txtDescription.setText(c.getDescription());
                cbParent.setValue(c.getParent());
            }
        });
    }

    // ===================== CREATE ===========================
    @FXML
    public void add() {
        try {
            if (txtName.getText().isBlank()) {
                error("Name cannot be empty!");
                return;
            }

            Category c = new Category();
            c.setName(txtName.getText());
            c.setDescription(txtDescription.getText());
            c.setParent(cbParent.getValue());
            c.setStatus(1);

            service.save(c);
            clear();
            refresh();

        } catch (Exception ex) {
            error(ex);
        }
    }

    // ===================== UPDATE ===========================
    @FXML
    public void update() {
        try {
            if (selected == null) {
                error("Select a category to update");
                return;
            }

            // Không cho tự làm parent của chính mình
            if (cbParent.getValue() != null &&
                cbParent.getValue().getId().equals(selected.getId())) {
                error("A category cannot be its own parent!");
                return;
            }

            selected.setName(txtName.getText());
            selected.setDescription(txtDescription.getText());
            selected.setParent(cbParent.getValue());

            service.update(selected);
            clear();
            refresh();

        } catch (Exception ex) {
            error(ex);
        }
    }

    // ===================== DELETE ===========================
    @FXML
    public void delete() {
        try {
            if (selected == null) {
                error("Select a category to delete");
                return;
            }

            service.delete(selected.getId());
            clear();
            refresh();

        } catch (Exception ex) {
            error(ex);
        }
    }

    // ===================== UTIL ===========================
    private void clear() {
        selected = null;
        txtName.clear();
        txtDescription.clear();
        cbParent.getSelectionModel().clearSelection();
    }

    private void error(Object msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg.toString(), ButtonType.OK);
        a.show();
    }
}
