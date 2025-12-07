package com.ucop.controller;

import com.ucop.dto.PromotionDTO;
import com.ucop.entity.Promotion;
import com.ucop.service.PromotionService;
import com.ucop.repository.PromotionRepository;
import com.ucop.repository.PromotionUsageRepository;
import com.ucop.util.HibernateUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class PromotionManagementController {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cboDiscountType;
    @FXML private ComboBox<String> cboStatus;
    @FXML private TableView<Promotion> tablePromotions;
    @FXML private TableColumn<Promotion, String> colCode;
    @FXML private TableColumn<Promotion, String> colName;
    @FXML private TableColumn<Promotion, String> colType;
    @FXML private TableColumn<Promotion, String> colValue;
    @FXML private TableColumn<Promotion, String> colMinAmount;
    @FXML private TableColumn<Promotion, String> colMaxDiscount;
    @FXML private TableColumn<Promotion, String> colStartDate;
    @FXML private TableColumn<Promotion, String> colEndDate;
    @FXML private TableColumn<Promotion, String> colUsageCount;
    @FXML private TableColumn<Promotion, String> colMaxUsage;
    @FXML private TableColumn<Promotion, String> colActive;
    @FXML private TableColumn<Promotion, Void> colActions;
    @FXML private ComboBox<String> cboPageSize;
    @FXML private Label lblPage;
    @FXML private Label lblTotal;
    @FXML private Label lblStatus;
    @FXML private Button btnFirst;
    @FXML private Button btnPrev;
    @FXML private Button btnNext;
    @FXML private Button btnLast;

    private PromotionService promotionService;
    private ObservableList<Promotion> promotionList = FXCollections.observableArrayList();
    private int currentPage = 1;
    private int pageSize = 10;
    private int totalPages = 1;
    private DashboardController dashboardController;

    @FXML
    public void initialize() {
        try {
            // Initialize service
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            PromotionRepository promotionRepository = new PromotionRepository(sessionFactory);
            PromotionUsageRepository usageRepository = new PromotionUsageRepository(sessionFactory);
            promotionService = new PromotionService(promotionRepository, usageRepository);

            // Setup ComboBoxes
            cboDiscountType.setItems(FXCollections.observableArrayList(
                "Tất cả", "PERCENTAGE", "FIXED_AMOUNT", "ITEM", "CART"
            ));
            cboDiscountType.setValue("Tất cả");
            
            cboStatus.setItems(FXCollections.observableArrayList(
                "Tất cả", "Đang hoạt động", "Không hoạt động"
            ));
            cboStatus.setValue("Tất cả");

            // Setup table columns
            setupTableColumns();

            // Setup page size combo
            cboPageSize.setItems(FXCollections.observableArrayList("10", "20", "50", "100"));
            cboPageSize.setValue("10");

            // Load data
            loadPromotions();

            lblStatus.setText("Sẵn sàng");
            lblStatus.setStyle("-fx-text-fill: green;");
        } catch (Exception e) {
            showError("Lỗi khởi tạo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        colCode.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCode()));
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDiscountType()));
        colValue.setCellValueFactory(data -> {
            Promotion p = data.getValue();
            BigDecimal discountValue = p.getDiscountValue();
            String value;
            if ("PERCENTAGE".equals(p.getDiscountType())) {
                // Format percentage: remove unnecessary decimals
                value = formatNumber(discountValue) + "%";
            } else {
                // Format money: remove unnecessary decimals
                value = formatNumber(discountValue) + " đ";
            }
            return new SimpleStringProperty(value);
        });
        colMinAmount.setCellValueFactory(data -> 
            new SimpleStringProperty(formatMoney(data.getValue().getMinOrderAmount())));
        colMaxDiscount.setCellValueFactory(data -> 
            new SimpleStringProperty(formatMoney(data.getValue().getMaxDiscountAmount())));
        colStartDate.setCellValueFactory(data -> 
            new SimpleStringProperty(formatDateTime(data.getValue().getStartDate())));
        colEndDate.setCellValueFactory(data -> 
            new SimpleStringProperty(formatDateTime(data.getValue().getEndDate())));
        colUsageCount.setCellValueFactory(data -> 
            new SimpleStringProperty(String.valueOf(data.getValue().getUsageCount())));
        colMaxUsage.setCellValueFactory(data -> {
            Integer max = data.getValue().getMaxUsageTotal();
            return new SimpleStringProperty(max == null ? "∞" : String.valueOf(max));
        });
        colActive.setCellValueFactory(data -> 
            new SimpleStringProperty(Boolean.TRUE.equals(data.getValue().getActive()) ? "✓ Hoạt động" : "✗ Ngưng"));

        // Actions column
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("✏️ Sửa");
            private final Button btnDelete = new Button("🗑️ Xóa");
            private final HBox hbox = new HBox(5, btnEdit, btnDelete);

            {
                btnEdit.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                btnDelete.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                btnEdit.setOnAction(e -> {
                    Promotion promotion = getTableView().getItems().get(getIndex());
                    handleEdit(promotion);
                });

                btnDelete.setOnAction(e -> {
                    Promotion promotion = getTableView().getItems().get(getIndex());
                    handleDelete(promotion);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }

    private void loadPromotions() {
        try {
            lblStatus.setText("Đang tải...");
            List<Promotion> all = promotionService.getAllPromotions();
            
            // Apply filters
            String search = txtSearch.getText();
            if (search != null && !search.trim().isEmpty()) {
                all = all.stream()
                    .filter(p -> p.getCode().toLowerCase().contains(search.toLowerCase()) ||
                                p.getName().toLowerCase().contains(search.toLowerCase()))
                    .toList();
            }

            String typeFilter = cboDiscountType.getValue();
            if (typeFilter != null && !"Tất cả".equals(typeFilter)) {
                all = all.stream()
                    .filter(p -> p.getDiscountType().equals(typeFilter))
                    .toList();
            }

            // Calculate pagination
            int total = all.size();
            totalPages = (int) Math.ceil((double) total / pageSize);
            if (totalPages == 0) totalPages = 1;
            if (currentPage > totalPages) currentPage = totalPages;

            // Get page data
            int start = (currentPage - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            List<Promotion> pageData = all.subList(start, end);

            promotionList.setAll(pageData);
            tablePromotions.setItems(promotionList);

            // Update pagination UI
            lblPage.setText("Trang " + currentPage + " / " + totalPages);
            lblTotal.setText("Tổng: " + total + " khuyến mãi");
            
            btnFirst.setDisable(currentPage == 1);
            btnPrev.setDisable(currentPage == 1);
            btnNext.setDisable(currentPage == totalPages);
            btnLast.setDisable(currentPage == totalPages);

            lblStatus.setText("Sẵn sàng");
            lblStatus.setStyle("-fx-text-fill: green;");
        } catch (Exception e) {
            showError("Lỗi tải dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        currentPage = 1;
        loadPromotions();
    }

    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        cboDiscountType.setValue("Tất cả");
        cboStatus.setValue("Tất cả");
        currentPage = 1;
        loadPromotions();
    }
    
    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }
    
    @FXML
    private void handleGoBack() {
        try {
            if (dashboardController != null) {
                dashboardController.showDashboard();
            } else {
                // Fallback: close current window if not in dashboard
                Stage stage = (Stage) tablePromotions.getScene().getWindow();
                stage.close();
            }
        } catch (Exception e) {
            showError("Lỗi khi quay lại: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreate() {
        try {
            if (dashboardController != null) {
                dashboardController.showPromotionForm(null);
            } else {
                // Fallback: open in new window if not in dashboard
                openPromotionFormDialog(null);
            }
        } catch (Exception e) {
            showError("Lỗi mở form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEdit(Promotion promotion) {
        try {
            if (dashboardController != null) {
                dashboardController.showPromotionForm(promotion);
            } else {
                // Fallback: open in new window if not in dashboard
                openPromotionFormDialog(promotion);
            }
        } catch (Exception e) {
            showError("Lỗi mở form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openPromotionFormDialog(Promotion promotion) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/promotion-form.fxml"));
        Parent root = loader.load();
        
        PromotionFormController controller = loader.getController();
        controller.setPromotionService(promotionService);
        controller.setManagementController(this);
        
        if (promotion != null) {
            controller.setPromotion(promotion);
        }
        
        Stage stage = new Stage();
        stage.setTitle(promotion == null ? "Thêm Khuyến Mãi" : "Sửa Khuyến Mãi");
        stage.setScene(new javafx.scene.Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        
        // Refresh after dialog closes
        loadPromotions();
    }
    
    public void returnToManagementView() {
        try {
            if (dashboardController != null) {
                dashboardController.showDashboard();
                // Reload promotion management after returning
                Platform.runLater(() -> dashboardController.showPromotionManagement());
            }
        } catch (Exception e) {
            showError("Lỗi quay lại danh sách: " + e.getMessage());
        }
    }

    private void handleDelete(Promotion promotion) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Bạn có chắc muốn xóa khuyến mãi này?");
        alert.setContentText("Mã: " + promotion.getCode() + "\nTên: " + promotion.getName());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                promotionService.deletePromotion(promotion.getId());
                showSuccess("Đã xóa khuyến mãi thành công!");
                loadPromotions();
            } catch (Exception e) {
                showError("Lỗi xóa: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleFirst() {
        currentPage = 1;
        loadPromotions();
    }

    @FXML
    private void handlePrevious() {
        if (currentPage > 1) {
            currentPage--;
            loadPromotions();
        }
    }

    @FXML
    private void handleNext() {
        if (currentPage < totalPages) {
            currentPage++;
            loadPromotions();
        }
    }

    @FXML
    private void handleLast() {
        currentPage = totalPages;
        loadPromotions();
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "0 đ";
        return formatNumber(amount) + " đ";
    }

    private String formatNumber(BigDecimal number) {
        if (number == null) return "0";
        // Remove trailing zeros and decimal point if not needed
        String formatted = number.stripTrailingZeros().toPlainString();
        // Format with thousand separators if it's a whole number
        if (number.scale() <= 0 || number.stripTrailingZeros().scale() <= 0) {
            return String.format("%,d", number.longValue());
        }
        return formatted;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private String formatDate(LocalDate date) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
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
        
        lblStatus.setText("Lỗi");
        lblStatus.setStyle("-fx-text-fill: red;");
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
