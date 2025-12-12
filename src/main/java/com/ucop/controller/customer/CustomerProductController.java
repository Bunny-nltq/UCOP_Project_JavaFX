package com.ucop.controller.customer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ucop.dao.CartItemDTO;
import com.ucop.entity.Cart;
import com.ucop.entity.Item;
import com.ucop.service.OrderService;
import com.ucop.service.ProductService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Controller cho Customer Product Listing
 * Hiển thị danh sách sản phẩm với tìm kiếm, lọc, phân trang
 */
public class CustomerProductController {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cboCategory;
    @FXML private ComboBox<String> cboSortBy;
    @FXML private Label lblCartCount;
    @FXML private GridPane gridProducts;
    @FXML private ComboBox<String> cboPageSize;
    @FXML private Label lblPage;
    @FXML private Label lblTotal;
    @FXML private Button btnFirst;
    @FXML private Button btnPrev;
    @FXML private Button btnNext;
    @FXML private Button btnLast;

    private ProductService productService;
    private OrderService orderService;
    private List<Item> allProducts = new ArrayList<>();
    private List<Item> filteredProducts = new ArrayList<>();
    private int currentPage = 1;
    private int pageSize = 12;
    private int totalPages = 1;
    private Long currentAccountId = 1L; // TODO: Get from session/login
    private Cart currentCart;

    @FXML
    public void initialize() {
        // Services will be injected by CustomerMainController
        
        // Setup ComboBoxes
        if (cboCategory != null) {
            cboCategory.setItems(javafx.collections.FXCollections.observableArrayList(
                "Tất cả", "Bút viết", "Vở và Sổ", "Dụng cụ học tập", "Văn phòng phẩm"
            ));
            cboCategory.setValue("Tất cả");
            cboCategory.setOnAction(e -> applyFilters());
        }
        if (cboSortBy != null) {
            cboSortBy.setItems(javafx.collections.FXCollections.observableArrayList(
                "Mới nhất", "Giá tăng dần", "Giá giảm dần", "Tên A-Z"
            ));
            cboSortBy.setValue("Mới nhất");
            cboSortBy.setOnAction(e -> applyFilters());
        }
        if (cboPageSize != null) {
            cboPageSize.setItems(javafx.collections.FXCollections.observableArrayList(
                "12", "24", "48"
            ));
            cboPageSize.setValue("12");
            cboPageSize.setOnAction(e -> handlePageSizeChange());
        }

        // Note: loadProducts() and displayProducts() will be called after services are set
    }

    /**
     * Set services (dependency injection)
     */
    public void setServices(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;

        // Load data after services are set
        loadProducts();
        loadCart();
        displayProducts();
    }

    /**
     * Set current account ID
     */
    public void setCurrentAccountId(Long accountId) {
        this.currentAccountId = accountId;
        loadCart();
    }

    /**
     * Load products from database
     */
    private void loadProducts() {
        if (productService != null) {
            allProducts = productService.getActiveProducts();
            filteredProducts = new ArrayList<>(allProducts);
        }
    }

    /**
     * Load or create cart for current user
     */
    private void loadCart() {
        if (orderService != null && currentAccountId != null) {
            currentCart = orderService.getOrCreateCart(currentAccountId);
            updateCartCount();
        }
    }

    /**
     * Update cart count display
     */
    private void updateCartCount() {
        if (currentCart != null && lblCartCount != null) {
            int count = currentCart.getItems().size();
            lblCartCount.setText(String.valueOf(count));
        }
    }

    @FXML
    private void handleSearch() {
        applyFilters();
    }

    private void applyFilters() {
        String keyword = txtSearch != null ? txtSearch.getText() : "";
        String category = cboCategory != null ? cboCategory.getValue() : "Tất cả";
        String sortBy = cboSortBy != null ? cboSortBy.getValue() : "Mới nhất";

        // Filter using service
        if (productService != null) {
            filteredProducts = productService.filterProducts(keyword, category, null, null);
            filteredProducts = productService.sortProducts(filteredProducts, sortBy);
        } else {
            // Fallback to local filtering
            filteredProducts = allProducts.stream()
                .filter(p -> keyword.isEmpty() || p.getName().toLowerCase().contains(keyword.toLowerCase()))
                .filter(p -> "Tất cả".equals(category) || p.getCategory().equals(category))
                .toList();
        }

        currentPage = 1;
        displayProducts();
    }

    private void displayProducts() {
        if (gridProducts == null) return;
        
        gridProducts.getChildren().clear();

        int total = filteredProducts.size();
        totalPages = (int) Math.ceil((double) total / pageSize);
        if (totalPages == 0) totalPages = 1;
        if (currentPage > totalPages) currentPage = totalPages;

        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<Item> pageProducts = filteredProducts.subList(start, end);

        int row = 0, col = 0;
        for (Item product : pageProducts) {
            VBox productCard = createProductCard(product);
            gridProducts.add(productCard, col, row);
            
            col++;
            if (col >= 4) {
                col = 0;
                row++;
            }
        }

        if (lblPage != null) lblPage.setText("Trang " + currentPage + " / " + totalPages);
        if (lblTotal != null) lblTotal.setText("Tổng: " + total + " sản phẩm");
        
        if (btnFirst != null) btnFirst.setDisable(currentPage == 1);
        if (btnPrev != null) btnPrev.setDisable(currentPage == 1);
        if (btnNext != null) btnNext.setDisable(currentPage == totalPages);
        if (btnLast != null) btnLast.setDisable(currentPage == totalPages);
    }

    private VBox createProductCard(Item product) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1;");
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefHeight(420);
        GridPane.setHgrow(card, Priority.ALWAYS);

        // Product Image
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(200, 200);
        imageContainer.setMaxSize(200, 200);
        imageContainer.setStyle("-fx-background-color: #f5f5f5;");
        
        String imageUrl = product.getImageUrl();
        System.out.println("Product: " + product.getName() + ", Image URL: " + imageUrl);
        
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                Image image = new Image(imageUrl, 180, 180, true, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(180);
                imageView.setFitHeight(180);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                
                // Check if image loaded successfully
                if (!image.isError()) {
                    imageContainer.getChildren().add(imageView);
                    System.out.println("✓ Image loaded successfully for: " + product.getName());
                } else {
                    System.err.println("✗ Image load error for: " + product.getName());
                    Label imgPlaceholder = new Label("📦");
                    imgPlaceholder.setFont(Font.font("System", FontWeight.BOLD, 60));
                    imageContainer.getChildren().add(imgPlaceholder);
                }
            } catch (Exception e) {
                System.err.println("✗ Exception loading image for " + product.getName() + ": " + e.getMessage());
                // Fallback to emoji if image loading fails
                Label imgPlaceholder = new Label("📦");
                imgPlaceholder.setFont(Font.font("System", FontWeight.BOLD, 60));
                imageContainer.getChildren().add(imgPlaceholder);
            }
        } else {
            System.out.println("✗ No image URL for: " + product.getName());
            // Default emoji placeholder
            Label imgPlaceholder = new Label("📦");
            imgPlaceholder.setFont(Font.font("System", FontWeight.BOLD, 60));
            imageContainer.getChildren().add(imgPlaceholder);
        }

        Label lblName = new Label(product.getName());
        lblName.setFont(Font.font("System", FontWeight.BOLD, 15));
        lblName.setWrapText(true);
        lblName.setMaxWidth(Double.MAX_VALUE);
        lblName.setAlignment(Pos.CENTER);
        lblName.setMaxHeight(45);
        lblName.setMinHeight(45);

        Label lblPrice = new Label(formatPrice(product.getPrice()));
        lblPrice.setFont(Font.font("System", FontWeight.BOLD, 18));
        lblPrice.setStyle("-fx-text-fill: #f44336;");

        Label lblCategory = new Label(product.getCategory() != null ? product.getCategory().getName() : "");
        lblCategory.setStyle("-fx-background-color: #E3F2FD; -fx-padding: 5 12; -fx-font-size: 13px;");

        // Button container
        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setMaxWidth(Double.MAX_VALUE);
        
        Button btnViewDetail = new Button("👁️ Xem chi tiết");
        btnViewDetail.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8 0;");
        btnViewDetail.setMaxWidth(Double.MAX_VALUE);
        btnViewDetail.setOnAction(e -> viewProductDetails(product));

        Button btnAddCart = new Button("🛒 Thêm vào giỏ");
        btnAddCart.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8 0;");
        btnAddCart.setMaxWidth(Double.MAX_VALUE);
        btnAddCart.setOnAction(e -> handleAddToCart(product));
        
        buttonBox.getChildren().addAll(btnViewDetail, btnAddCart);

        card.getChildren().addAll(imageContainer, lblName, lblCategory, lblPrice, buttonBox);
        return card;
    }

    private void handleAddToCart(Item product) {
        if (orderService == null || currentCart == null) {
            showError("Không thể thêm vào giỏ hàng. Vui lòng đăng nhập lại.");
            return;
        }

        try {
            // Check if product is available
            if (!productService.isProductAvailable(product.getId(), 1)) {
                showError("Sản phẩm không còn hàng hoặc không khả dụng.");
                return;
            }

            // Add to cart
            CartItemDTO itemDTO = new CartItemDTO();
            itemDTO.setItemId(product.getId());
            itemDTO.setQuantity(1);
            itemDTO.setUnitPrice(product.getPrice());

            orderService.addToCart(currentCart.getId(), itemDTO);
            
            // Reload cart
            loadCart();
            
            showInfo("Đã thêm \"" + product.getName() + "\" vào giỏ hàng!");
        } catch (Exception e) {
            showError("Lỗi khi thêm vào giỏ hàng: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewCart() {
        if (currentCart != null) {
            int count = currentCart.getItems().size();
            showInfo("Bạn có " + count + " sản phẩm trong giỏ hàng.");
            // TODO: Navigate to cart view
        }
    }

    /**
     * View product details
     */
    private void viewProductDetails(Item product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/customer/product-detail.fxml"));
            Parent root = loader.load();
            
            ProductDetailController controller = loader.getController();
            controller.setProduct(product);
            controller.setServices(productService, orderService);
            controller.setCurrentAccountId(currentAccountId);
            controller.setParentController(this);
            
            // Lấy BorderPane cha từ CustomerMainController
            BorderPane mainContainer = (BorderPane) gridProducts.getScene().getRoot();
            if (mainContainer != null) {
                mainContainer.setCenter(root);
            }
        } catch (IOException e) {
            showError("Không thể mở chi tiết sản phẩm: " + e.getMessage());
        }
    }

    @FXML
    private void handleFirst() {
        currentPage = 1;
        displayProducts();
    }

    @FXML
    private void handlePrevious() {
        if (currentPage > 1) {
            currentPage--;
            displayProducts();
        }
    }

    @FXML
    private void handleNext() {
        if (currentPage < totalPages) {
            currentPage++;
            displayProducts();
        }
    }

    @FXML
    private void handleLast() {
        currentPage = totalPages;
        displayProducts();
    }

    private void handlePageSizeChange() {
        if (cboPageSize != null && cboPageSize.getValue() != null) {
            pageSize = Integer.parseInt(cboPageSize.getValue());
            currentPage = 1;
            displayProducts();
        }
    }

    private String formatPrice(BigDecimal price) {
        return String.format("%,.0f đ", price);
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
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
