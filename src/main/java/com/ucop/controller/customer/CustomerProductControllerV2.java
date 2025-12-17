package com.ucop.controller.customer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ucop.entity.Cart;
import com.ucop.entity.Product;
import com.ucop.service.ProductServiceV2;
import com.ucop.service.CartServiceV2;
import com.ucop.repository.CartRepository;
import com.ucop.repository.impl.CartRepositoryImpl;
import com.ucop.util.HibernateUtil;

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
 * Controller V2 cho Customer Product Listing
 * Sử dụng entity Product từ bảng products trong database
 */
public class CustomerProductControllerV2 {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cboCategory;
    @FXML private ComboBox<String> cboSortBy;
    @FXML private Label lblCartCount;
    @FXML private Button btnViewCart;
    @FXML private GridPane gridProducts;
    @FXML private ComboBox<String> cboPageSize;
    @FXML private Label lblPage;
    @FXML private Label lblTotal;
    @FXML private Button btnFirst;
    @FXML private Button btnPrev;
    @FXML private Button btnNext;
    @FXML private Button btnLast;

    private ProductServiceV2 productService;
    private CartServiceV2 cartService;
    private List<Product> allProducts = new ArrayList<>();
    private List<Product> filteredProducts = new ArrayList<>();
    private int currentPage = 1;
    private int pageSize = 12;
    private int totalPages = 1;
    private Long currentAccountId = 1L; // TODO: Get from session
    private Cart currentCart;
    private BorderPane mainContainer; // Main container from CustomerMainController
    private CustomerMainController customerMainController; // Reference to main controller

    /**
     * Set main container from parent controller
     */
    public void setMainContainer(BorderPane mainContainer) {
        this.mainContainer = mainContainer;
        System.out.println("✓ CustomerProductControllerV2: mainContainer set = " + (mainContainer != null));
    }

    /**
     * Set customer main controller for navigation
     */
    public void setCustomerMainController(CustomerMainController controller) {
        this.customerMainController = controller;
        System.out.println("✓ CustomerProductControllerV2: customerMainController set = " + (controller != null));
    }

    @FXML
    public void initialize() {
        // Khởi tạo services
        productService = new ProductServiceV2();
        CartRepository cartRepository = new CartRepositoryImpl(HibernateUtil.getSessionFactory());
        cartService = new CartServiceV2(cartRepository);
        
        // Tạo hoặc lấy cart hiện tại
        currentCart = cartService.getOrCreateCart(currentAccountId);
        updateCartCount();
        
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

        // Load products
        loadProducts();
        displayProducts();
    }

    /**
     * Cập nhật số lượng giỏ hàng
     */
    private void updateCartCount() {
        if (cartService != null && currentCart != null && lblCartCount != null) {
            int count = cartService.getCartItemCount(currentCart.getId());
            lblCartCount.setText(String.valueOf(count));
        }
    }

    /**
     * Load products từ database
     */
    private void loadProducts() {
        try {
            allProducts = productService.getActiveProducts();
            filteredProducts = new ArrayList<>(allProducts);
            
            System.out.println("✓ Loaded " + allProducts.size() + " products from database");
            
            // In thông tin một số sản phẩm để kiểm tra
            for (int i = 0; i < Math.min(3, allProducts.size()); i++) {
                Product p = allProducts.get(i);
                System.out.println("Product: " + p.getName() + 
                                 ", Price: " + p.getPrice() + 
                                 ", Image: " + p.getImageUrl() +
                                 ", Category: " + p.getCategory());
            }
        } catch (Exception e) {
            System.err.println("✗ Error loading products: " + e.getMessage());
            e.printStackTrace();
            showError("Không thể tải danh sách sản phẩm: " + e.getMessage());
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

        // Lọc sử dụng service
        filteredProducts = productService.filterProducts(keyword, category, null, null);
        filteredProducts = productService.sortProducts(filteredProducts, sortBy);

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
        List<Product> pageProducts = filteredProducts.subList(start, end);

        int row = 0, col = 0;
        for (Product product : pageProducts) {
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

    private VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefHeight(420);
        GridPane.setHgrow(card, Priority.ALWAYS);

        // Product Image
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(200, 200);
        imageContainer.setMaxSize(200, 200);
        imageContainer.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8;");
        
        String imageUrl = product.getImageUrl();
        System.out.println("📦 Product: " + product.getName() + ", Image URL: " + imageUrl);
        
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
                Label imgPlaceholder = new Label("📦");
                imgPlaceholder.setFont(Font.font("System", FontWeight.BOLD, 60));
                imageContainer.getChildren().add(imgPlaceholder);
            }
        } else {
            System.out.println("✗ No image URL for: " + product.getName());
            Label imgPlaceholder = new Label("📦");
            imgPlaceholder.setFont(Font.font("System", FontWeight.BOLD, 60));
            imageContainer.getChildren().add(imgPlaceholder);
        }

        // Product Name
        Label lblName = new Label(product.getName());
        lblName.setFont(Font.font("System", FontWeight.BOLD, 15));
        lblName.setWrapText(true);
        lblName.setMaxWidth(Double.MAX_VALUE);
        lblName.setAlignment(Pos.CENTER);
        lblName.setMaxHeight(45);
        lblName.setMinHeight(45);

        // Product Price
        Label lblPrice = new Label(formatPrice(product.getPrice()));
        lblPrice.setFont(Font.font("System", FontWeight.BOLD, 18));
        lblPrice.setStyle("-fx-text-fill: #f44336;");

        // Product Category
        String categoryText = product.getCategory() != null ? product.getCategory() : "";
        Label lblCategory = new Label(categoryText);
        lblCategory.setStyle("-fx-background-color: #E3F2FD; -fx-padding: 5 12; -fx-font-size: 13px; -fx-background-radius: 15;");

        // Stock status
        String stockText = product.getStockQuantity() != null && product.getStockQuantity() > 0 
            ? "Còn hàng (" + product.getStockQuantity() + ")" 
            : "Hết hàng";
        Label lblStock = new Label(stockText);
        lblStock.setStyle("-fx-font-size: 12px; -fx-text-fill: " + 
            (product.getStockQuantity() != null && product.getStockQuantity() > 0 ? "#4CAF50" : "#f44336") + ";");

        // Button container
        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setMaxWidth(Double.MAX_VALUE);
        
        Button btnViewDetail = new Button("👁️ Xem chi tiết");
        btnViewDetail.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8 0; -fx-background-radius: 5;");
        btnViewDetail.setMaxWidth(Double.MAX_VALUE);
        btnViewDetail.setOnAction(e -> viewProductDetails(product));

        Button btnAddCart = new Button("🛒 Thêm vào giỏ");
        btnAddCart.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8 0; -fx-background-radius: 5;");
        btnAddCart.setMaxWidth(Double.MAX_VALUE);
        btnAddCart.setOnAction(e -> handleAddToCart(product));
        btnAddCart.setDisable(product.getStockQuantity() == null || product.getStockQuantity() <= 0);
        
        buttonBox.getChildren().addAll(btnViewDetail, btnAddCart);

        card.getChildren().addAll(imageContainer, lblName, lblCategory, lblPrice, lblStock, buttonBox);
        return card;
    }

    private void handleAddToCart(Product product) {
        if (cartService == null || currentCart == null) {
            showError("Không thể thêm vào giỏ hàng. Vui lòng thử lại!");
            return;
        }

        try {
            // Thêm vào giỏ hàng với số lượng = 1
            cartService.addProductToCart(currentCart.getId(), product.getId(), 1);
            
            // Cập nhật số lượng giỏ hàng
            currentCart = cartService.getOrCreateCart(currentAccountId);
            updateCartCount();
            
            showInfo("Đã thêm \"" + product.getName() + "\" vào giỏ hàng!");
        } catch (Exception e) {
            showError("Lỗi khi thêm vào giỏ hàng: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewCart() {
        try {
            // Load cart page - Sử dụng customer_cart.fxml (đã gộp V1 và V2)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/customer/customer_cart.fxml"));
            Parent cartPage = loader.load();
            
            // Get controller and set account ID
            CustomerCartController controller = loader.getController();
            controller.setCurrentAccountId(currentAccountId);
            // Set main controller for navigation to orders page
            if (customerMainController != null) {
                controller.setMainController(customerMainController);
            }
            // Force reload cart after setting account ID
            controller.refreshCart();
            
            // If mainContainer not set, try to find it from scene
            if (mainContainer == null && gridProducts != null && gridProducts.getScene() != null) {
                javafx.scene.Parent root = gridProducts.getScene().getRoot();
                
                if (root instanceof BorderPane) {
                    BorderPane borderPane = (BorderPane) root;
                    javafx.scene.Node center = borderPane.getCenter();
                    
                    // Check if center is StackPane (CustomerDashboard)
                    if (center instanceof StackPane) {
                        StackPane stackPane = (StackPane) center;
                        stackPane.getChildren().clear();
                        stackPane.getChildren().add(cartPage);
                        System.out.println("✓ Loaded cart page into StackPane");
                        return;
                    } else {
                        mainContainer = borderPane;
                    }
                }
            }
            
            if (mainContainer == null) {
                showError("Không thể mở giỏ hàng. Vui lòng thử lại.");
                return;
            }
            
            // Load cart page into center
            mainContainer.setCenter(cartPage);
            
            System.out.println("✓ Loaded cart page into BorderPane");
        } catch (Exception e) {
            System.err.println("✗ Error loading cart page: " + e.getMessage());
            e.printStackTrace();
            showError("Không thể mở giỏ hàng: " + e.getMessage());
        }
    }

    private void viewProductDetails(Product product) {
        try {
            System.out.println("→ viewProductDetails called for: " + product.getName());
            System.out.println("→ mainContainer is null? " + (mainContainer == null));
            
            // Load product detail page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/customer/product-detail.fxml"));
            Parent detailPage = loader.load();
            
            // Get controller and set product
            ProductDetailController controller = loader.getController();
            controller.setProduct(product);
            
            // If mainContainer not set, try to get parent container from scene
            if (mainContainer == null && gridProducts != null && gridProducts.getScene() != null) {
                javafx.scene.Parent root = gridProducts.getScene().getRoot();
                System.out.println("→ Trying to get container from scene root: " + root.getClass().getName());
                
                // Check if root is BorderPane (CustomerDashboard)
                if (root instanceof BorderPane) {
                    BorderPane borderPane = (BorderPane) root;
                    javafx.scene.Node center = borderPane.getCenter();
                    
                    // Check if center is StackPane (contentArea in CustomerDashboard)
                    if (center instanceof StackPane) {
                        StackPane stackPane = (StackPane) center;
                        stackPane.getChildren().clear();
                        stackPane.getChildren().add(detailPage);
                        System.out.println("✓ Loaded product detail into StackPane (CustomerDashboard)");
                        return;
                    } else {
                        // It's CustomerMain with BorderPane
                        mainContainer = borderPane;
                        System.out.println("✓ Found BorderPane from scene root (CustomerMain)");
                    }
                }
            }
            
            if (mainContainer == null) {
                System.err.println("✗ ERROR: Cannot find suitable container!");
                showError("Không thể mở chi tiết sản phẩm. Vui lòng thử lại.");
                return;
            }
            
            controller.setMainContainer(mainContainer);
            
            // Load detail page into center
            mainContainer.setCenter(detailPage);
            
            System.out.println("✓ Loaded product detail for: " + product.getName());
        } catch (Exception e) {
            System.err.println("✗ Error loading product detail: " + e.getMessage());
            e.printStackTrace();
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
        if (price == null) return "0 đ";
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
