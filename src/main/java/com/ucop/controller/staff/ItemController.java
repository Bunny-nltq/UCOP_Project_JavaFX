package com.ucop.controller.staff;

import java.math.BigDecimal;

import com.ucop.entity.Category;
import com.ucop.entity.Item;
import com.ucop.service.CategoryService;
import com.ucop.service.ItemService;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ItemController {

    @FXML private TextField txtSku, txtName, txtPrice, txtStock, txtUnit, txtWeight;
    @FXML private ComboBox<Category> cbCategory;

    @FXML private TableView<Item> tbl;
    @FXML private TableColumn<Item, Long> colId;
    @FXML private TableColumn<Item, String> colSku;
    @FXML private TableColumn<Item, String> colName;
    @FXML private TableColumn<Item, BigDecimal> colPrice;
    @FXML private TableColumn<Item, Integer> colStock;
    @FXML private TableColumn<Item, String> colCategory;

    private final ItemService itemService = new ItemService();
    private final CategoryService categoryService = new CategoryService();

    private Item selected;

    @FXML
    public void initialize() {
        setupTable();
        refresh();
        setupRowSelect();
    }

    // ------------------ TABLE SETUP ------------------
    private void setupTable() {
        colId.setCellValueFactory(v -> new SimpleObjectProperty<>(v.getValue().getId()));
        colSku.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getSku()));
        colName.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getName()));
        colPrice.setCellValueFactory(v -> new SimpleObjectProperty<>(v.getValue().getPrice()));
        colStock.setCellValueFactory(v -> new SimpleObjectProperty<>(v.getValue().getStock()));

        // FIX LazyInitializationException
        colCategory.setCellValueFactory(v ->
            new SimpleStringProperty(
                    (v.getValue().getCategory() != null)
                            ? v.getValue().getCategory().getName()
                            : "No Category"
            )
        );
    }

    // ------------------ LOAD DATA ------------------
    private void refresh() {
        tbl.setItems(FXCollections.observableArrayList(itemService.findAll()));
        cbCategory.setItems(FXCollections.observableArrayList(categoryService.findAll()));
    }

    // ------------------ TABLE CLICK ------------------
    private void setupRowSelect() {
        tbl.setOnMouseClicked(event -> {
            Item it = tbl.getSelectionModel().getSelectedItem();
            if (it != null) {
                selected = it;

                txtSku.setText(it.getSku());
                txtName.setText(it.getName());
                txtUnit.setText(it.getUnit());

                txtWeight.setText(it.getWeight() != null ? it.getWeight().toString() : "");
                txtPrice.setText(it.getPrice() != null ? it.getPrice().toString() : "");
                txtStock.setText(it.getStock() != null ? it.getStock().toString() : "");

                cbCategory.setValue(it.getCategory());
            }
        });
    }

    // ------------------ ADD ------------------
    @FXML
    public void add() {
        try {
            if (txtSku.getText().isBlank() || txtName.getText().isBlank()) {
                showError("SKU và Name không được để trống!");
                return;
            }

            if (cbCategory.getValue() == null) {
                showError("Bạn phải chọn Category!");
                return;
            }

            Item it = new Item();
            it.setSku(txtSku.getText());
            it.setName(txtName.getText());
            it.setUnit(txtUnit.getText());

            it.setWeight(parseDouble(txtWeight.getText()));
            it.setPrice(parseBigDecimal(txtPrice.getText()));
            it.setStock(parseInt(txtStock.getText()));

            it.setCategory(cbCategory.getValue());

            itemService.save(it);
            clearForm();
            refresh();

        } catch (Exception ex) {
            showError(ex);
        }
    }

    // ------------------ UPDATE ------------------
    @FXML
    public void update() {
        try {
            if (selected == null) {
                showError("Hãy chọn item để cập nhật!");
                return;
            }

            selected.setSku(txtSku.getText());
            selected.setName(txtName.getText());
            selected.setUnit(txtUnit.getText());

            selected.setWeight(parseDouble(txtWeight.getText()));
            selected.setPrice(parseBigDecimal(txtPrice.getText()));
            selected.setStock(parseInt(txtStock.getText()));

            selected.setCategory(cbCategory.getValue());

            itemService.update(selected);
            clearForm();
            refresh();

        } catch (Exception ex) {
            showError(ex);
        }
    }

    // ------------------ DELETE ------------------
    @FXML
    public void delete() {
        try {
            if (selected == null) {
                showError("Hãy chọn item để xóa!");
                return;
            }

            itemService.delete(selected);  // ✔ Sửa đúng: delete(Item)
            clearForm();
            refresh();

        } catch (Exception ex) {
            showError(ex);
        }
    }

    // ------------------ HELPERS ------------------
    private Double parseDouble(String s) {
        return (s == null || s.isBlank()) ? null : Double.valueOf(s);
    }

    private BigDecimal parseBigDecimal(String s) {
        return (s == null || s.isBlank()) ? BigDecimal.ZERO : new BigDecimal(s);
    }

    private Integer parseInt(String s) {
        return (s == null || s.isBlank()) ? 0 : Integer.parseInt(s);
    }

    private void clearForm() {
        selected = null;
        txtSku.clear();
        txtName.clear();
        txtUnit.clear();
        txtWeight.clear();
        txtPrice.clear();
        txtStock.clear();
        cbCategory.getSelectionModel().clearSelection();
    }

    private void showError(Object msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg.toString(), ButtonType.OK);
        a.show();
    }
}
