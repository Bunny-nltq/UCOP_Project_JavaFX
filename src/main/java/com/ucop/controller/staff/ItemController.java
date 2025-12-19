package com.ucop.controller.staff;

import com.ucop.entity.Category;
import com.ucop.entity.Item;
import com.ucop.service.CategoryService;
import com.ucop.service.ItemService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.hibernate.LazyInitializationException;

import java.io.File;
import java.math.BigDecimal;

public class ItemController {

    // ================= FIELDS =================
    @FXML private TextField txtSku, txtName, txtPrice, txtStock, txtUnit, txtWeight;
    @FXML private ComboBox<Category> cbCategory;

    @FXML private Label lblImagePath;
    @FXML private ImageView imgPreview;

    @FXML private TableView<Item> tbl;

    // ✅ FIX: Item.id là Long
    @FXML private TableColumn<Item, Long> colId;

    @FXML private TableColumn<Item, String> colSku;
    @FXML private TableColumn<Item, String> colName;
    @FXML private TableColumn<Item, String> colUnit;
    @FXML private TableColumn<Item, BigDecimal> colPrice;
    @FXML private TableColumn<Item, Integer> colStock;
    @FXML private TableColumn<Item, String> colCategory;
    @FXML private TableColumn<Item, String> colImage;

    private final ItemService itemService = new ItemService();
    private final CategoryService categoryService = new CategoryService();

    private Item selected;
    private File selectedImageFile;

    @FXML
    public void initialize() {
        try {
            setupTable();
            setupCategoryComboBox();
            refresh();
            setupRowSelect();
        } catch (Exception e) {
            showError("Lỗi khởi tạo dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupTable() {
        colId.setCellValueFactory(v -> new SimpleObjectProperty<>(v.getValue().getId()));
        colSku.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getSku()));
        colName.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getName()));
        colUnit.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getUnit()));
        colPrice.setCellValueFactory(v -> new SimpleObjectProperty<>(v.getValue().getPrice()));
        colStock.setCellValueFactory(v -> new SimpleObjectProperty<>(v.getValue().getStock()));

        colCategory.setCellValueFactory(v ->
                new SimpleStringProperty(
                        v.getValue().getCategory() != null
                                ? v.getValue().getCategory().getName()
                                : "No Category"
                )
        );

        colImage.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getImagePath()));
        colImage.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String path, boolean empty) {
                super.updateItem(path, empty);
                if (empty || path == null || path.isBlank()) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                try {
                    File f = new File(path);
                    if (f.exists()) {
                        Image img = new Image(f.toURI().toString(), 40, 40, true, true, true);
                        imageView.setImage(img);
                        imageView.setFitWidth(40);
                        imageView.setFitHeight(40);
                        setGraphic(imageView);
                        setText(null);
                    } else {
                        setGraphic(null);
                        setText("❌");
                    }
                } catch (Exception e) {
                    setGraphic(null);
                    setText("❌");
                }
            }
        });
    }

    private void setupCategoryComboBox() {
        cbCategory.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        cbCategory.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
    }

    @FXML
    private void chooseImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn hình ảnh");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        Stage stage = (Stage) lblImagePath.getScene().getWindow();
        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            selectedImageFile = file;
            lblImagePath.setText(file.getName());
            imgPreview.setImage(new Image(file.toURI().toString()));
        }
    }

    private void refresh() {
        try {
            tbl.setItems(FXCollections.observableArrayList(itemService.findAll()));
            cbCategory.setItems(FXCollections.observableArrayList(categoryService.findAll()));
        } catch (Exception e) {
            showError("Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    private void setupRowSelect() {
        tbl.setOnMouseClicked(e -> {
            Item it = tbl.getSelectionModel().getSelectedItem();
            if (it == null) return;

            selected = it;
            txtSku.setText(it.getSku());
            txtName.setText(it.getName());
            txtUnit.setText(it.getUnit());

            // ✅ FIX: weight là BigDecimal (khớp Item.java mới)
            txtWeight.setText(it.getWeight() != null ? it.getWeight().toString() : "");

            txtPrice.setText(it.getPrice() != null ? it.getPrice().toString() : "");
            txtStock.setText(it.getStock() != null ? it.getStock().toString() : "");

            try {
                cbCategory.setValue(it.getCategory());
            } catch (LazyInitializationException ex) {
                cbCategory.getSelectionModel().clearSelection();
                System.err.println("Lazy initialization error on Category during selection.");
            }

            if (it.getImagePath() != null) {
                File f = new File(it.getImagePath());
                if (f.exists()) {
                    imgPreview.setImage(new Image(f.toURI().toString()));
                    lblImagePath.setText(f.getName());
                } else {
                    imgPreview.setImage(null);
                    lblImagePath.setText("Ảnh không tồn tại");
                }
            } else {
                imgPreview.setImage(null);
                lblImagePath.setText("Chưa chọn ảnh");
            }
        });
    }

    @FXML
    public void add() {
        try {
            Item it = new Item();
            it.setSku(txtSku.getText());
            it.setName(txtName.getText());
            it.setUnit(txtUnit.getText());

            // ✅ FIX: weight BigDecimal
            it.setWeight(parseBigDecimalNullable(txtWeight.getText()));

            it.setPrice(parseBigDecimal(txtPrice.getText()));
            it.setStock(parseInt(txtStock.getText()));
            it.setCategory(cbCategory.getValue());

            if (selectedImageFile != null) {
                it.setImagePath(selectedImageFile.getAbsolutePath());
            }

            itemService.save(it);
            clearForm();
            refresh();
        } catch (NumberFormatException ex) {
            showError("Giá, Tồn kho hoặc Khối lượng phải là số hợp lệ.");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    public void update() {
        if (selected == null) {
            showError("Vui lòng chọn sản phẩm cần cập nhật.");
            return;
        }
        try {
            selected.setSku(txtSku.getText());
            selected.setName(txtName.getText());
            selected.setUnit(txtUnit.getText());

            // ✅ FIX: weight BigDecimal
            selected.setWeight(parseBigDecimalNullable(txtWeight.getText()));

            selected.setPrice(parseBigDecimal(txtPrice.getText()));
            selected.setStock(parseInt(txtStock.getText()));
            selected.setCategory(cbCategory.getValue());

            if (selectedImageFile != null) {
                selected.setImagePath(selectedImageFile.getAbsolutePath());
            }

            itemService.update(selected);
            clearForm();
            refresh();
        } catch (NumberFormatException ex) {
            showError("Giá, Tồn kho hoặc Khối lượng phải là số hợp lệ.");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    public void delete() {
        if (selected == null) {
            showError("Vui lòng chọn sản phẩm cần xóa.");
            return;
        }
        try {
            itemService.delete(selected);
            clearForm();
            refresh();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private BigDecimal parseBigDecimal(String s) {
        if (s == null || s.isBlank()) return BigDecimal.ZERO;
        return new BigDecimal(s.trim());
    }

    private BigDecimal parseBigDecimalNullable(String s) {
        if (s == null || s.isBlank()) return null;
        return new BigDecimal(s.trim());
    }

    private Integer parseInt(String s) {
        if (s == null || s.isBlank()) return 0;
        return Integer.parseInt(s.trim());
    }

    private void clearForm() {
        selected = null;
        selectedImageFile = null;
        txtSku.clear();
        txtName.clear();
        txtUnit.clear();
        txtWeight.clear();
        txtPrice.clear();
        txtStock.clear();
        lblImagePath.setText("Chưa chọn ảnh");
        imgPreview.setImage(null);
        cbCategory.getSelectionModel().clearSelection();
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).show();
    }
}
