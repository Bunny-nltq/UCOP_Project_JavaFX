package com.ucop.controller.customer;

import com.ucop.entity.Category;
import com.ucop.entity.Item;
import com.ucop.repository.CategoryRepository;
import com.ucop.repository.ProductRepository;
import com.ucop.util.HibernateUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for Customer Items Page - Display products from database
 */
public class CustomerItemsController {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cboCategory;
    @FXML private ComboBox<String> cboSort;
    @FXML private ComboBox<String> cboPageSize;
    @FXML private GridPane gridProducts;
    @FXML private VBox emptyState;
    @FXML private Label lblTotalItems;
    @FXML private Label lblStatus;
    @FXML private Label lblPage;
    @FXML private Button btnFirst;
    @FXML private Button btnPrev;
    @FXML private Button btnNext;
    @FXML private Button btnLast;

    private SessionFactory sessionFactory;
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private BorderPane mainContainer; // Reference to parent container
    
    private List<Item> allItems = new ArrayList<>();
    private List<Item> filteredItems = new ArrayList<>();
    private int currentPage = 1;
    private int pageSize = 12; // 12 products per page (4 columns x 3 rows)
    private int totalPages = 1;
    
    public void setMainContainer(BorderPane mainContainer) {
        this.mainContainer = mainContainer;
    }

    @FXML
    public void initialize() {
        try {
            // Initialize Hibernate and repositories
            sessionFactory = HibernateUtil.getSessionFactory();
            productRepository = new com.ucop.repository.impl.ProductRepositoryImpl(sessionFactory);
            categoryRepository = new com.ucop.repository.impl.CategoryRepositoryImpl(sessionFactory);

            // Setup ComboBoxes
            setupComboBoxes();
            
            // Load initial data
            loadCategories();
            loadItems();
            
            lblStatus.setText("✓ Dữ liệu đã tải");
            lblStatus.setStyle("-fx-text-fill: green;");

        } catch (Exception e) {
            System.err.println("Lỗi khởi tạo CustomerItemsController: " + e.getMessage());
            e.printStackTrace();
            if (lblStatus != null) {
                lblStatus.setText("❌ Lỗi: " + e.getMessage());
                lblStatus.setStyle("-fx-text-fill: red;");
            }
        }
    }

    private void setupComboBoxes() {
        // Sort options
        cboSort.getItems().addAll(
            "Mới nhất",
            "Giá: Thấp đến cao",
            "Giá: Cao đến thấp",
            "Tên: A-Z",
            "Tên: Z-A"
        );
        cboSort.setValue("Mới nhất");
        cboSort.setOnAction(e -> applySortAndFilter());

        // Page size
        cboPageSize.getItems().addAll("12", "24", "36", "48");
        cboPageSize.setValue("12");
        cboPageSize.setOnAction(e -> {
            pageSize = Integer.parseInt(cboPageSize.getValue());
            currentPage = 1;
            displayCurrentPage();
        });
    }

    private void loadCategories() {
        try {
            List<Category> categories = categoryRepository.findAll();
            
            cboCategory.getItems().clear();
            cboCategory.getItems().add("Tất cả danh mục");
            
            for (Category category : categories) {
                if (category.getStatus() == 1) {
                    cboCategory.getItems().add(category.getName());
                }
            }
            
            cboCategory.setValue("Tất cả danh mục");
            cboCategory.setOnAction(e -> applySortAndFilter());

        } catch (Exception e) {
            System.err.println("Lỗi khi tải danh mục: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadItems() {
        try {
            // Get all active items
            allItems = productRepository.findAll().stream()
                .filter(item -> item.getStatus() == 1)
                .collect(Collectors.toList());
            
            // Initial filter
            applySortAndFilter();
            
        } catch (Exception e) {
            System.err.println("Lỗi khi tải sản phẩm: " + e.getMessage());
            e.printStackTrace();
            lblStatus.setText("❌ Lỗi tải dữ liệu: " + e.getMessage());
            lblStatus.setStyle("-fx-text-fill: red;");
        }
    }

    private void applySortAndFilter() {
        String searchText = txtSearch.getText().trim().toLowerCase();
        String selectedCategory = cboCategory.getValue();
        String sortOption = cboSort.getValue();

        // Filter
        filteredItems = allItems.stream()
            .filter(item -> {
                // Search filter
                if (!searchText.isEmpty()) {
                    String itemName = item.getName().toLowerCase();
                    String itemDesc = item.getDescription() != null ? item.getDescription().toLowerCase() : "";
                    if (!itemName.contains(searchText) && !itemDesc.contains(searchText)) {
                        return false;
                    }
                }
                
                // Category filter
                if (selectedCategory != null && !selectedCategory.equals("Tất cả danh mục")) {
                    Category category = item.getCategory();
                    if (category == null || !category.getName().equals(selectedCategory)) {
                        return false;
                    }
                }
                
                return true;
            })
            .collect(Collectors.toList());

        // Sort
        switch (sortOption) {
            case "Giá: Thấp đến cao":
                filteredItems.sort((a, b) -> a.getPrice().compareTo(b.getPrice()));
                break;
            case "Giá: Cao đến thấp":
                filteredItems.sort((a, b) -> b.getPrice().compareTo(a.getPrice()));
                break;
            case "Tên: A-Z":
                filteredItems.sort((a, b) -> a.getName().compareTo(b.getName()));
                break;
            case "Tên: Z-A":
                filteredItems.sort((a, b) -> b.getName().compareTo(a.getName()));
                break;
            default: // Mới nhất
                filteredItems.sort((a, b) -> b.getId().compareTo(a.getId()));
                break;
        }

        // Reset to page 1
        currentPage = 1;
        displayCurrentPage();
    }

    private void displayCurrentPage() {
        gridProducts.getChildren().clear();
        
        int totalItems = filteredItems.size();
        totalPages = (int) Math.ceil((double) totalItems / pageSize);
        
        if (totalPages == 0) totalPages = 1;
        if (currentPage > totalPages) currentPage = totalPages;
        
        // Calculate start and end indices
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalItems);
        
        // Show/hide empty state
        if (filteredItems.isEmpty()) {
            gridProducts.setVisible(false);
            gridProducts.setManaged(false);
            emptyState.setVisible(true);
            emptyState.setManaged(true);
        } else {
            gridProducts.setVisible(true);
            gridProducts.setManaged(true);
            emptyState.setVisible(false);
            emptyState.setManaged(false);
            
            // Display products in grid (4 columns)
            int col = 0;
            int row = 0;
            final int COLUMNS = 4;
            
            for (int i = startIndex; i < endIndex; i++) {
                Item item = filteredItems.get(i);
                VBox productCard = createProductCard(item);
                
                // Set column constraints for equal width
                GridPane.setHgrow(productCard, javafx.scene.layout.Priority.ALWAYS);
                GridPane.setFillWidth(productCard, true);
                
                gridProducts.add(productCard, col, row);
                
                col++;
                if (col >= COLUMNS) {
                    col = 0;
                    row++;
                }
            }
            
            // Set column constraints to distribute evenly
            gridProducts.getColumnConstraints().clear();
            for (int i = 0; i < COLUMNS; i++) {
                ColumnConstraints colConstraints = new ColumnConstraints();
                colConstraints.setPercentWidth(100.0 / COLUMNS);
                colConstraints.setHgrow(javafx.scene.layout.Priority.ALWAYS);
                gridProducts.getColumnConstraints().add(colConstraints);
            }
        }
        
        // Update UI labels
        lblTotalItems.setText("Tổng: " + totalItems + " sản phẩm");
        lblPage.setText("Trang " + currentPage + " / " + totalPages);
        
        // Update pagination buttons
        updatePaginationButtons();
    }

    private VBox createProductCard(Item item) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle("-fx-background-color: white; " +
                     "-fx-border-color: #ddd; " +
                     "-fx-border-width: 1; " +
                     "-fx-padding: 15; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);");
        card.setPrefWidth(240);
        card.setPrefHeight(420);
        card.setCursor(javafx.scene.Cursor.DEFAULT);
        
        // Make card clickable with hover effect
        String originalStyle = card.getStyle();
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: #f9f9f9; " +
                         "-fx-border-color: #4CAF50; " +
                         "-fx-border-width: 2; " +
                         "-fx-padding: 14; " +
                         "-fx-effect: dropshadow(three-pass-box, rgba(76,175,80,0.2), 8, 0, 0, 2);");
        });
        card.setOnMouseExited(e -> card.setStyle(originalStyle));
        
        // Image Container with border
        StackPane imageContainer = new StackPane();
        imageContainer.setStyle("-fx-background-color: #f5f5f5; " +
                               "-fx-border-color: #ddd; " +
                               "-fx-border-width: 1;");
        imageContainer.setPrefSize(200, 200);
        
        // Tạo hình ảnh placeholder với emoji đẹp
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            ImageView imageView = new ImageView();
            imageView.setFitWidth(190);
            imageView.setFitHeight(190);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            
            try {
                Image image = new Image(item.getImageUrl(), 190, 190, true, true, true);
                image.errorProperty().addListener((obs, oldError, newError) -> {
                    if (newError) {
                        // Nếu load ảnh lỗi, thay bằng placeholder
                        Platform.runLater(() -> {
                            imageContainer.getChildren().clear();
                            imageContainer.getChildren().add(createEmojiPlaceholder(item));
                        });
                    }
                });
                imageView.setImage(image);
                imageContainer.getChildren().add(imageView);
            } catch (Exception e) {
                // Lỗi khi load ảnh, dùng placeholder
                imageContainer.getChildren().add(createEmojiPlaceholder(item));
            }
        } else {
            // Không có URL, dùng placeholder
            imageContainer.getChildren().add(createEmojiPlaceholder(item));
        }
        
        // Product name with better font
        Label nameLabel = new Label(item.getName());
        nameLabel.setFont(Font.font(14));
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333; -fx-text-alignment: center;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(210);
        nameLabel.setMaxHeight(40);
        nameLabel.setAlignment(Pos.CENTER);
        
        // Category badge
        String categoryName = item.getCategory() != null ? item.getCategory().getName() : "Khác";
        Label categoryLabel = new Label("📂 " + categoryName);
        categoryLabel.setFont(Font.font(11));
        categoryLabel.setStyle("-fx-text-fill: #666; " +
                              "-fx-background-color: #e8e8e8; " +
                              "-fx-padding: 4 10;");
        
        // Price with better styling
        Label priceLabel = new Label(formatCurrency(item.getPrice()));
        priceLabel.setFont(Font.font(20));
        priceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        
        // Stock info
        Label stockLabel = new Label(item.getStock() > 0 ? "✓ Còn hàng" : "✗ Hết hàng");
        stockLabel.setFont(Font.font(10));
        stockLabel.setStyle(item.getStock() > 0 ? 
            "-fx-text-fill: #27ae60; -fx-font-weight: bold;" : 
            "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        
        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        // View details button (Green)
        Button btnViewDetail = new Button("🔍 Xem chi tiết");
        btnViewDetail.setFont(Font.font(12));
        btnViewDetail.setStyle("-fx-background-color: #4CAF50; " +
                              "-fx-text-fill: white; " +
                              "-fx-font-weight: bold; " +
                              "-fx-padding: 10 20; " +
                              "-fx-cursor: hand;");
        btnViewDetail.setMaxWidth(Double.MAX_VALUE);
        btnViewDetail.setOnMouseEntered(e -> btnViewDetail.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;"));
        btnViewDetail.setOnMouseExited(e -> btnViewDetail.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;"));
        btnViewDetail.setOnAction(e -> {
            e.consume(); // Prevent card click event
            handleProductClick(item);
        });
        
        // Add to cart button (Blue)
        Button btnAddToCart = new Button("🛒 Thêm vào giỏ");
        btnAddToCart.setFont(Font.font(12));
        btnAddToCart.setStyle("-fx-background-color: #2196F3; " +
                             "-fx-text-fill: white; " +
                             "-fx-font-weight: bold; " +
                             "-fx-padding: 10 20; " +
                             "-fx-cursor: hand;");
        btnAddToCart.setMaxWidth(Double.MAX_VALUE);
        btnAddToCart.setOnMouseEntered(e -> btnAddToCart.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;"));
        btnAddToCart.setOnMouseExited(e -> btnAddToCart.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;"));
        btnAddToCart.setOnAction(e -> {
            e.consume(); // Prevent card click event
            handleAddToCart(item);
        });
        
        card.getChildren().addAll(imageContainer, nameLabel, categoryLabel, priceLabel, stockLabel, spacer, btnViewDetail, btnAddToCart);
        
        return card;
    }

    private VBox createEmojiPlaceholder(Item item) {
        VBox placeholder = new VBox(5);
        placeholder.setAlignment(Pos.CENTER);
        
        // Chọn emoji dựa trên tên sản phẩm
        String emoji = "📦";
        String name = item.getName().toLowerCase();
        
        if (name.contains("bút")) emoji = "✏️";
        else if (name.contains("sổ") || name.contains("vở")) emoji = "📓";
        else if (name.contains("kéo")) emoji = "✂️";
        else if (name.contains("băng keo")) emoji = "📌";
        else if (name.contains("hồ")) emoji = "🖍️";
        else if (name.contains("thước")) emoji = "📏";
        else if (name.contains("máy tính")) emoji = "💻";
        else if (name.contains("máy in")) emoji = "🖨️";
        else if (name.contains("bàn phím")) emoji = "⌨️";
        else if (name.contains("chuột")) emoji = "🖱️";
        else if (name.contains("tai nghe")) emoji = "🎧";
        else if (name.contains("bàn") || name.contains("ghế")) emoji = "🪑";
        
        Label emojiLabel = new Label(emoji);
        emojiLabel.setFont(Font.font("System", FontWeight.BOLD, 80));
        
        placeholder.getChildren().add(emojiLabel);
        return placeholder;
    }

    private void handleProductClick(Item item) {
        try {
            // Tìm main container
            BorderPane container = mainContainer;
            if (container == null && gridProducts != null && gridProducts.getScene() != null) {
                javafx.scene.Parent root = gridProducts.getScene().getRoot();
                if (root instanceof BorderPane) {
                    container = (BorderPane) root;
                }
            }
            
            // Load product detail view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/customer/product-detail.fxml"));
            Parent detailRoot = loader.load();
            
            // Get controller and set product
            ProductDetailController controller = loader.getController();
            controller.setProduct(item);
            controller.setItemsController(this);
            controller.setMainContainer(container);
            
            // Replace main container content with product detail
            if (container != null) {
                container.setCenter(detailRoot);
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi khi mở chi tiết sản phẩm: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback to simple alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Chi tiết sản phẩm");
            alert.setHeaderText(item.getName());
            String content = String.format(
                "Mã: %d\nDanh mục: %s\nGiá: %s\nTồn kho: %d\nMô tả: %s",
                item.getId(),
                item.getCategory() != null ? item.getCategory().getName() : "Khác",
                formatCurrency(item.getPrice()),
                item.getStock(),
                item.getDescription() != null ? item.getDescription() : "Chưa có mô tả"
            );
            alert.setContentText(content);
            alert.show();
        }
    }
    
    public void showProductList() {
        // Reload current view to go back to product list
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/customer/customer_items.fxml"));
            Parent root = loader.load();
            
            CustomerItemsController controller = loader.getController();
            controller.setMainContainer(mainContainer);
            
            if (mainContainer != null) {
                mainContainer.setCenter(root);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi quay lại danh sách: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleAddToCart(Item item) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thêm vào giỏ hàng");
        alert.setHeaderText("Đã thêm vào giỏ hàng!");
        alert.setContentText("Sản phẩm \"" + item.getName() + "\" đã được thêm vào giỏ hàng của bạn.");
        alert.show();
        
        // TODO: Implement actual add to cart logic
    }

    private void updatePaginationButtons() {
        btnFirst.setDisable(currentPage == 1);
        btnPrev.setDisable(currentPage == 1);
        btnNext.setDisable(currentPage == totalPages);
        btnLast.setDisable(currentPage == totalPages);
    }

    @FXML
    private void handleSearch() {
        applySortAndFilter();
    }

    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        cboCategory.setValue("Tất cả danh mục");
        cboSort.setValue("Mới nhất");
        applySortAndFilter();
    }

    @FXML
    private void handleFirst() {
        currentPage = 1;
        displayCurrentPage();
    }

    @FXML
    private void handlePrevious() {
        if (currentPage > 1) {
            currentPage--;
            displayCurrentPage();
        }
    }

    @FXML
    private void handleNext() {
        if (currentPage < totalPages) {
            currentPage++;
            displayCurrentPage();
        }
    }

    @FXML
    private void handleLast() {
        currentPage = totalPages;
        displayCurrentPage();
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 ₫";
        return String.format("%,d ₫", amount.longValue());
    }
}
