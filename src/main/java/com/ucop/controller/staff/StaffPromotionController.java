package com.ucop.controller.staff;

import com.ucop.entity.Promotion;
import com.ucop.service.PromotionService;
import com.ucop.repository.PromotionRepository;
import com.ucop.repository.PromotionUsageRepository;
import com.ucop.util.HibernateUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Staff Promotion Controller - READ ONLY
 * Staff can only view promotions, cannot add/edit/delete
 */
public class StaffPromotionController {

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

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

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
            
            cboPageSize.setItems(FXCollections.observableArrayList(
                "10", "20", "50", "100"
            ));
            cboPageSize.setValue("10");
            cboPageSize.setOnAction(e -> {
                pageSize = Integer.parseInt(cboPageSize.getValue());
                currentPage = 1;
                loadPromotions();
            });

            // Setup table columns
            setupTableColumns();
            
            // Load initial data
            loadPromotions();
            
            lblStatus.setText("Dữ liệu đã được tải");
            lblStatus.setStyle("-fx-text-fill: green;");

        } catch (Exception e) {
            System.err.println("Lỗi khởi tạo Staff Promotion Controller: " + e.getMessage());
            e.printStackTrace();
            if (lblStatus != null) {
                lblStatus.setText("Lỗi: " + e.getMessage());
                lblStatus.setStyle("-fx-text-fill: red;");
            }
        }
    }

    private void setupTableColumns() {
        colCode.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getCode()));
        
        colName.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getName()));
        
        colType.setCellValueFactory(data -> 
            new SimpleStringProperty(getDiscountTypeText(data.getValue().getDiscountType())));
        
        colValue.setCellValueFactory(data -> {
            Promotion p = data.getValue();
            String value = p.getDiscountType().equals("PERCENTAGE") 
                ? p.getDiscountValue() + "%" 
                : formatCurrency(p.getDiscountValue());
            return new SimpleStringProperty(value);
        });
        
        colMinAmount.setCellValueFactory(data -> 
            new SimpleStringProperty(formatCurrency(data.getValue().getMinOrderAmount())));
        
        colMaxDiscount.setCellValueFactory(data -> {
            BigDecimal max = data.getValue().getMaxDiscountAmount();
            return new SimpleStringProperty(max != null ? formatCurrency(max) : "Không giới hạn");
        });
        
        colStartDate.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getStartDate().format(DATE_FORMATTER)));
        
        colEndDate.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getEndDate().format(DATE_FORMATTER)));
        
        colUsageCount.setCellValueFactory(data -> 
            new SimpleStringProperty(String.valueOf(data.getValue().getUsageCount())));
        
        colMaxUsage.setCellValueFactory(data -> {
            Integer max = data.getValue().getMaxUsageTotal();
            return new SimpleStringProperty(max != null ? String.valueOf(max) : "Không giới hạn");
        });
        
        colActive.setCellValueFactory(data -> {
            boolean active = data.getValue().getActive();
            return new SimpleStringProperty(active ? "✅ Hoạt động" : "❌ Tắt");
        });

        tablePromotions.setItems(promotionList);
    }

    private void loadPromotions() {
        try {
            String searchText = txtSearch.getText().trim();
            String typeFilter = cboDiscountType.getValue();
            String statusFilter = cboStatus.getValue();

            // Get filtered data
            List<Promotion> allPromotions = promotionService.getAllPromotions();
            
            // Apply filters
            List<Promotion> filteredList = allPromotions.stream()
                .filter(p -> {
                    if (!searchText.isEmpty() && 
                        !p.getCode().toLowerCase().contains(searchText.toLowerCase()) &&
                        !p.getName().toLowerCase().contains(searchText.toLowerCase())) {
                        return false;
                    }
                    if (!"Tất cả".equals(typeFilter) && !p.getDiscountType().equals(typeFilter)) {
                        return false;
                    }
                    if ("Đang hoạt động".equals(statusFilter) && !p.getActive()) {
                        return false;
                    }
                    if ("Không hoạt động".equals(statusFilter) && p.getActive()) {
                        return false;
                    }
                    return true;
                })
                .toList();

            // Calculate pagination
            int totalRecords = filteredList.size();
            totalPages = (int) Math.ceil((double) totalRecords / pageSize);
            
            if (totalPages == 0) totalPages = 1;
            if (currentPage > totalPages) currentPage = totalPages;
            
            int start = (currentPage - 1) * pageSize;
            int end = Math.min(start + pageSize, totalRecords);
            
            List<Promotion> pageData = filteredList.subList(start, end);
            
            promotionList.clear();
            promotionList.addAll(pageData);
            
            // Update UI
            lblPage.setText("Trang " + currentPage + " / " + totalPages);
            lblTotal.setText("Tổng: " + totalRecords + " khuyến mãi");
            
            updatePaginationButtons();
            
        } catch (Exception e) {
            System.err.println("Lỗi khi tải danh sách khuyến mãi: " + e.getMessage());
            e.printStackTrace();
            lblStatus.setText("Lỗi: " + e.getMessage());
            lblStatus.setStyle("-fx-text-fill: red;");
        }
    }

    private void updatePaginationButtons() {
        btnFirst.setDisable(currentPage == 1);
        btnPrev.setDisable(currentPage == 1);
        btnNext.setDisable(currentPage == totalPages);
        btnLast.setDisable(currentPage == totalPages);
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

    private String getDiscountTypeText(String type) {
        return switch (type) {
            case "PERCENTAGE" -> "Phần trăm";
            case "FIXED_AMOUNT" -> "Số tiền cố định";
            case "ITEM" -> "Theo sản phẩm";
            case "CART" -> "Theo giỏ hàng";
            default -> type;
        };
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 ₫";
        return String.format("%,d ₫", amount.longValue());
    }
}
