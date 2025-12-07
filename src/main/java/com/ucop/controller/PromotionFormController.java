package com.ucop.controller;

import com.ucop.dto.PromotionDTO;
import com.ucop.entity.Promotion;
import com.ucop.service.PromotionService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Controller for Promotion Form (Add/Edit)
 */
public class PromotionFormController {

    @FXML private TextField txtCode;
    @FXML private TextField txtName;
    @FXML private TextArea txtDescription;
    @FXML private ComboBox<String> cboType;
    @FXML private TextField txtValue;
    @FXML private Label lblValueUnit;
    @FXML private TextField txtMinAmount;
    @FXML private TextField txtMaxDiscount;
    @FXML private DatePicker dateStart;
    @FXML private DatePicker dateEnd;
    @FXML private TextField txtMaxUsage;
    @FXML private TextField txtMaxUsagePerUser;
    @FXML private CheckBox chkActive;
    @FXML private Label lblApplicableItems;
    @FXML private TextField txtFieldApplicableItems;

    private PromotionService promotionService;
    private Promotion editingPromotion;
    private PromotionManagementController managementController;
    private DashboardController dashboardController;

    @FXML
    public void initialize() {
        // Setup ComboBox items
        cboType.setItems(FXCollections.observableArrayList(
            "PERCENTAGE", "FIXED_AMOUNT", "ITEM", "CART"
        ));
        
        // Setup combo box listener to update unit label
        cboType.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("PERCENTAGE".equals(newVal)) {
                lblValueUnit.setText("(%)");
            } else {
                lblValueUnit.setText("(VNĐ)");
            }
            
            // Show/hide applicable items field
            boolean showItems = "ITEM".equals(newVal);
            if (lblApplicableItems != null) {
                lblApplicableItems.setVisible(showItems);
                lblApplicableItems.setManaged(showItems);
            }
            if (txtFieldApplicableItems != null) {
                txtFieldApplicableItems.setVisible(showItems);
                txtFieldApplicableItems.setManaged(showItems);
            }
        });

        // Set default values
        txtMaxUsagePerUser.setText("1");
        chkActive.setSelected(true);
        
        // Initially hide applicable items field
        if (lblApplicableItems != null) {
            lblApplicableItems.setVisible(false);
            lblApplicableItems.setManaged(false);
        }
        if (txtFieldApplicableItems != null) {
            txtFieldApplicableItems.setVisible(false);
            txtFieldApplicableItems.setManaged(false);
        }
    }

    public void setPromotionService(PromotionService promotionService) {
        this.promotionService = promotionService;
    }
    
    public void setManagementController(PromotionManagementController managementController) {
        this.managementController = managementController;
    }
    
    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    public void setPromotion(Promotion promotion) {
        this.editingPromotion = promotion;
        populateForm(promotion);
    }

    private void populateForm(Promotion promotion) {
        txtCode.setText(promotion.getCode());
        txtCode.setDisable(true); // Cannot change code when editing
        
        txtName.setText(promotion.getName());
        txtDescription.setText(promotion.getDescription());
        cboType.setValue(promotion.getDiscountType());
        txtValue.setText(promotion.getDiscountValue().toString());
        
        if (promotion.getMinOrderAmount() != null) {
            txtMinAmount.setText(promotion.getMinOrderAmount().toString());
        }
        
        if (promotion.getMaxDiscountAmount() != null) {
            txtMaxDiscount.setText(promotion.getMaxDiscountAmount().toString());
        }
        
        dateStart.setValue(promotion.getStartDate().toLocalDate());
        dateEnd.setValue(promotion.getEndDate().toLocalDate());
        
        if (promotion.getMaxUsageTotal() != null) {
            txtMaxUsage.setText(promotion.getMaxUsageTotal().toString());
        }
        
        if (promotion.getMaxUsagePerUser() != null) {
            txtMaxUsagePerUser.setText(promotion.getMaxUsagePerUser().toString());
        }
        
        chkActive.setSelected(Boolean.TRUE.equals(promotion.getActive()));
        
        if (promotion.getApplicableItemIds() != null && txtFieldApplicableItems != null) {
            txtFieldApplicableItems.setText(promotion.getApplicableItemIds());
        }
    }

    @FXML
    private void handleSave() {
        try {
            // Validate inputs
            if (!validateInputs()) {
                return;
            }

            // Create DTO from form
            PromotionDTO dto = createDTOFromForm();

            // Save or update
            if (editingPromotion == null) {
                promotionService.createPromotion(dto);
                showSuccess("Đã tạo khuyến mãi thành công!");
            } else {
                promotionService.updatePromotion(editingPromotion.getId(), dto);
                showSuccess("Đã cập nhật khuyến mãi thành công!");
            }

            // Close dialog
            closeDialog();

        } catch (Exception e) {
            showError("Lỗi lưu khuyến mãi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private boolean validateInputs() {
        // Required fields validation
        if (isEmpty(txtCode.getText())) {
            showError("Vui lòng nhập mã khuyến mãi!");
            txtCode.requestFocus();
            return false;
        }

        if (isEmpty(txtName.getText())) {
            showError("Vui lòng nhập tên khuyến mãi!");
            txtName.requestFocus();
            return false;
        }

        if (cboType.getValue() == null) {
            showError("Vui lòng chọn loại giảm giá!");
            cboType.requestFocus();
            return false;
        }

        if (isEmpty(txtValue.getText())) {
            showError("Vui lòng nhập giá trị giảm!");
            txtValue.requestFocus();
            return false;
        }

        // Validate numeric fields
        try {
            BigDecimal value = new BigDecimal(txtValue.getText());
            if (value.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Giá trị giảm phải lớn hơn 0!");
                txtValue.requestFocus();
                return false;
            }

            // For percentage, value should be <= 100
            if ("PERCENTAGE".equals(cboType.getValue()) && value.compareTo(BigDecimal.valueOf(100)) > 0) {
                showError("Giá trị phần trăm không được vượt quá 100%!");
                txtValue.requestFocus();
                return false;
            }

        } catch (NumberFormatException e) {
            showError("Giá trị giảm không hợp lệ!");
            txtValue.requestFocus();
            return false;
        }

        // Validate optional numeric fields
        if (!isEmpty(txtMinAmount.getText())) {
            try {
                new BigDecimal(txtMinAmount.getText());
            } catch (NumberFormatException e) {
                showError("Đơn tối thiểu không hợp lệ!");
                txtMinAmount.requestFocus();
                return false;
            }
        }

        if (!isEmpty(txtMaxDiscount.getText())) {
            try {
                new BigDecimal(txtMaxDiscount.getText());
            } catch (NumberFormatException e) {
                showError("Giảm tối đa không hợp lệ!");
                txtMaxDiscount.requestFocus();
                return false;
            }
        }

        if (!isEmpty(txtMaxUsage.getText())) {
            try {
                Integer.parseInt(txtMaxUsage.getText());
            } catch (NumberFormatException e) {
                showError("Giới hạn sử dụng không hợp lệ!");
                txtMaxUsage.requestFocus();
                return false;
            }
        }

        if (!isEmpty(txtMaxUsagePerUser.getText())) {
            try {
                Integer.parseInt(txtMaxUsagePerUser.getText());
            } catch (NumberFormatException e) {
                showError("Giới hạn sử dụng/người không hợp lệ!");
                txtMaxUsagePerUser.requestFocus();
                return false;
            }
        }

        // Date validation
        if (dateStart.getValue() == null) {
            showError("Vui lòng chọn ngày bắt đầu!");
            dateStart.requestFocus();
            return false;
        }

        if (dateEnd.getValue() == null) {
            showError("Vui lòng chọn ngày kết thúc!");
            dateEnd.requestFocus();
            return false;
        }

        if (dateEnd.getValue().isBefore(dateStart.getValue())) {
            showError("Ngày kết thúc phải sau ngày bắt đầu!");
            dateEnd.requestFocus();
            return false;
        }

        return true;
    }

    private PromotionDTO createDTOFromForm() {
        PromotionDTO dto = new PromotionDTO();
        
        dto.setCode(txtCode.getText().trim().toUpperCase());
        dto.setName(txtName.getText().trim());
        dto.setDescription(txtDescription.getText() != null ? txtDescription.getText().trim() : "");
        dto.setDiscountType(cboType.getValue());
        dto.setDiscountValue(new BigDecimal(txtValue.getText()));
        
        // Optional fields
        if (!isEmpty(txtMinAmount.getText())) {
            dto.setMinOrderAmount(new BigDecimal(txtMinAmount.getText()));
        }
        
        if (!isEmpty(txtMaxDiscount.getText())) {
            dto.setMaxDiscountAmount(new BigDecimal(txtMaxDiscount.getText()));
        }
        
        if (!isEmpty(txtMaxUsage.getText())) {
            dto.setMaxUsageTotal(Integer.parseInt(txtMaxUsage.getText()));
        }
        
        if (!isEmpty(txtMaxUsagePerUser.getText())) {
            dto.setMaxUsagePerUser(Integer.parseInt(txtMaxUsagePerUser.getText()));
        } else {
            dto.setMaxUsagePerUser(1);
        }
        
        // Dates
        dto.setStartDate(LocalDateTime.of(dateStart.getValue(), LocalTime.MIN));
        dto.setEndDate(LocalDateTime.of(dateEnd.getValue(), LocalTime.of(23, 59, 59)));
        
        // Status
        dto.setActive(chkActive.isSelected());
        dto.setStackable(false); // Default
        
        // Applicable items
        if ("ITEM".equals(cboType.getValue())) {
            dto.setApplicableTo("SPECIFIC_ITEMS");
            if (txtFieldApplicableItems != null && !isEmpty(txtFieldApplicableItems.getText())) {
                dto.setApplicableItemIds(txtFieldApplicableItems.getText().trim());
            }
        } else {
            dto.setApplicableTo("ALL");
        }
        
        return dto;
    }

    private boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    private void closeDialog() {
        if (dashboardController != null) {
            // Return to promotion management in dashboard
            dashboardController.showDashboard();
            Platform.runLater(() -> dashboardController.showPromotionManagement());
        } else if (managementController != null) {
            managementController.returnToManagementView();
        } else {
            // Fallback: close window if opened as dialog
            Stage stage = (Stage) txtCode.getScene().getWindow();
            stage.close();
        }
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
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
