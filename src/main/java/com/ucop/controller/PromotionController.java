package com.ucop.controller;

import com.ucop.dto.PromotionDTO;
import com.ucop.entity.Promotion;
import com.ucop.service.PromotionService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for Promotion Management
 */
public class PromotionController {

    @FXML
    private TableView<Promotion> promotionTable;

    @FXML
    private TableColumn<Promotion, String> codeColumn;

    @FXML
    private TableColumn<Promotion, String> nameColumn;

    @FXML
    private TableColumn<Promotion, String> discountTypeColumn;

    @FXML
    private TableColumn<Promotion, BigDecimal> discountValueColumn;

    @FXML
    private TableColumn<Promotion, LocalDateTime> startDateColumn;

    @FXML
    private TableColumn<Promotion, LocalDateTime> endDateColumn;

    @FXML
    private TableColumn<Promotion, Integer> usageCountColumn;

    @FXML
    private TableColumn<Promotion, Boolean> activeColumn;

    @FXML
    private TextField codeField;

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private ComboBox<String> discountTypeCombo;

    @FXML
    private TextField discountValueField;

    @FXML
    private TextField minOrderField;

    @FXML
    private TextField maxDiscountField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField maxUsageTotalField;

    @FXML
    private TextField maxUsagePerUserField;

    @FXML
    private CheckBox activeCheckBox;

    @FXML
    private CheckBox stackableCheckBox;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterTypeCombo;

    private PromotionService promotionService;
    private ObservableList<Promotion> promotions;
    private Promotion selectedPromotion;

    @FXML
    public void initialize() {
        promotions = FXCollections.observableArrayList();
        
        setupTableColumns();
        setupFormFields();
        setupFilters();
        setupEventHandlers();
    }

    public void setPromotionService(PromotionService promotionService) {
        this.promotionService = promotionService;
        loadPromotions();
    }

    private void setupTableColumns() {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        discountTypeColumn.setCellValueFactory(new PropertyValueFactory<>("discountType"));
        discountValueColumn.setCellValueFactory(new PropertyValueFactory<>("discountValue"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        usageCountColumn.setCellValueFactory(new PropertyValueFactory<>("usageCount"));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));
        
        // Custom cell factory for active column
        activeColumn.setCellFactory(column -> new TableCell<Promotion, Boolean>() {
            @Override
            protected void updateItem(Boolean active, boolean empty) {
                super.updateItem(active, empty);
                if (empty || active == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(active ? "Hoạt động" : "Không hoạt động");
                    setStyle(active ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
                }
            }
        });
    }

    private void setupFormFields() {
        discountTypeCombo.setItems(FXCollections.observableArrayList(
            "PERCENTAGE", "FIXED_AMOUNT", "ITEM", "CART"
        ));
        discountTypeCombo.setValue("PERCENTAGE");
        
        activeCheckBox.setSelected(true);
        maxUsagePerUserField.setText("1");
    }

    private void setupFilters() {
        filterTypeCombo.setItems(FXCollections.observableArrayList(
            "Tất cả", "Hoạt động", "Không hoạt động", "Hết hạn"
        ));
        filterTypeCombo.setValue("Ất cả");
        filterTypeCombo.setOnAction(e -> applyFilters());
        
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void setupEventHandlers() {
        promotionTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedPromotion = newSelection;
                    populateForm(newSelection);
                }
            }
        );
    }

    private void loadPromotions() {
        if (promotionService == null) return;
        
        try {
            List<Promotion> list = promotionService.getAllPromotions();
            promotions.setAll(list);
            promotionTable.setItems(promotions);
            applyFilters();
        } catch (Exception e) {
            showError("Ỗi! Không thể tải danh sách khuyến mãi: " + e.getMessage());
        }
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String filterType = filterTypeCombo.getValue();
        LocalDateTime now = LocalDateTime.now();
        
        ObservableList<Promotion> filtered = promotions.filtered(promo -> {
            boolean matches = true;
            
            // Search filter
            if (!searchText.isEmpty()) {
                matches = promo.getCode().toLowerCase().contains(searchText) ||
                         promo.getName().toLowerCase().contains(searchText);
            }
            
            // Type filter
            switch (filterType) {
                case "Hoạt động":
                    matches = matches && promo.getActive() && 
                             promo.getStartDate().isBefore(now) &&
                             promo.getEndDate().isAfter(now);
                    break;
                case "Không hoạt động":
                    matches = matches && !promo.getActive();
                    break;
                case "Hết hạn":
                    matches = matches && promo.getEndDate().isBefore(now);
                    break;
            }
            
            return matches;
        });
        
        promotionTable.setItems(filtered);
    }

    private void populateForm(Promotion promotion) {
        codeField.setText(promotion.getCode());
        nameField.setText(promotion.getName());
        descriptionArea.setText(promotion.getDescription());
        discountTypeCombo.setValue(promotion.getDiscountType());
        discountValueField.setText(promotion.getDiscountValue().toString());
        
        if (promotion.getMinOrderAmount() != null) {
            minOrderField.setText(promotion.getMinOrderAmount().toString());
        }
        if (promotion.getMaxDiscountAmount() != null) {
            maxDiscountField.setText(promotion.getMaxDiscountAmount().toString());
        }
        
        startDatePicker.setValue(promotion.getStartDate().toLocalDate());
        endDatePicker.setValue(promotion.getEndDate().toLocalDate());
        
        if (promotion.getMaxUsageTotal() != null) {
            maxUsageTotalField.setText(promotion.getMaxUsageTotal().toString());
        }
        maxUsagePerUserField.setText(promotion.getMaxUsagePerUser().toString());
        
        activeCheckBox.setSelected(promotion.getActive());
        stackableCheckBox.setSelected(promotion.getStackable());
    }

    @FXML
    private void handleCreate() {
        try {
            PromotionDTO dto = createDTOFromForm();
            promotionService.createPromotion(dto);
            showSuccess("Đã tạo khuyến mãi thành công!");
            loadPromotions();
            handleClear();
        } catch (Exception e) {
            showError("Ỗi! " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedPromotion == null) {
            showError("Vui lòng chọn khuyến mãi cần cập nhật!");
            return;
        }
        
        try {
            PromotionDTO dto = createDTOFromForm();
            promotionService.updatePromotion(selectedPromotion.getId(), dto);
            showSuccess("Đã cập nhật khuyến mãi thành công!");
            loadPromotions();
        } catch (Exception e) {
            showError("Ỗi! " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedPromotion == null) {
            showError("Vui lòng chọn khuyến mãi cần xóa!");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Xóa khuyến mãi");
        confirm.setContentText("Bạn có chắc muốn xóa khuyến mãi này?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    promotionService.deletePromotion(selectedPromotion.getId());
                    showSuccess("Đã xóa khuyến mãi thành công!");
                    loadPromotions();
                    handleClear();
                } catch (Exception e) {
                    showError("Ỗi! " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleClear() {
        selectedPromotion = null;
        codeField.clear();
        nameField.clear();
        descriptionArea.clear();
        discountTypeCombo.setValue("PERCENTAGE");
        discountValueField.clear();
        minOrderField.clear();
        maxDiscountField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        maxUsageTotalField.clear();
        maxUsagePerUserField.setText("1");
        activeCheckBox.setSelected(true);
        stackableCheckBox.setSelected(false);
        promotionTable.getSelectionModel().clearSelection();
    }

    private PromotionDTO createDTOFromForm() {
        PromotionDTO dto = new PromotionDTO();
        dto.setCode(codeField.getText());
        dto.setName(nameField.getText());
        dto.setDescription(descriptionArea.getText());
        dto.setDiscountType(discountTypeCombo.getValue());
        dto.setDiscountValue(new BigDecimal(discountValueField.getText()));
        
        if (!minOrderField.getText().isEmpty()) {
            dto.setMinOrderAmount(new BigDecimal(minOrderField.getText()));
        }
        if (!maxDiscountField.getText().isEmpty()) {
            dto.setMaxDiscountAmount(new BigDecimal(maxDiscountField.getText()));
        }
        if (!maxUsageTotalField.getText().isEmpty()) {
            dto.setMaxUsageTotal(Integer.parseInt(maxUsageTotalField.getText()));
        }
        
        dto.setStartDate(startDatePicker.getValue().atStartOfDay());
        dto.setEndDate(endDatePicker.getValue().atTime(23, 59, 59));
        dto.setMaxUsagePerUser(Integer.parseInt(maxUsagePerUserField.getText()));
        dto.setActive(activeCheckBox.isSelected());
        dto.setStackable(stackableCheckBox.isSelected());
        
        return dto;
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ỗi!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
