package com.ucop.controller.customer;

import java.math.BigDecimal;

import com.ucop.dao.CartItemDAO;
import com.ucop.entity.Cart;
import com.ucop.entity.Item;
import com.ucop.entity.Product;
import com.ucop.service.CartServiceV2;
import com.ucop.service.OrderService;
import com.ucop.service.ProductService;
import com.ucop.repository.CartRepository;
import com.ucop.repository.impl.CartRepositoryImpl;
import com.ucop.util.HibernateUtil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Controller for Product Detail View
 * Uses Product entity from products table
 */
public class ProductDetailController {

    @FXML private Label lblProductName;
    @FXML private Label lblCategory;
    @FXML private Label lblPrice;
    @FXML private Label lblStock;
    @FXML private TextArea txtDescription;
    @FXML private Spinner<Integer> spnQuantity;
    @FXML private Button btnAddToCart;
    @FXML private Button btnBack;
    @FXML private StackPane imageContainer;

    private Product product;
    private Item item; // For backward compatibility with old controllers
    private BorderPane mainContainer;
    private CartServiceV2 cartService;
    private ProductService productService;
    private OrderService orderService;
    private Long currentAccountId = 1L; // TODO: Get from session
    private Cart currentCart;
    private CustomerProductController parentController;
    private CustomerItemsController itemsController;

    @FXML
    public void initialize() {
        // Setup quantity spinner
        if (spnQuantity != null) {
            SpinnerValueFactory<Integer> valueFactory = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1);
            spnQuantity.setValueFactory(valueFactory);
        }
        
        // Khởi tạo CartServiceV2
        CartRepository cartRepository = new CartRepositoryImpl(HibernateUtil.getSessionFactory());
        cartService = new CartServiceV2(cartRepository);
        currentCart = cartService.getOrCreateCart(currentAccountId);
    }

    /**
     * Set the product to display (Product entity)
     */
    public void setProduct(Product product) {
        this.product = product;
        this.item = null;
        displayProductDetails();
    }

    /**
     * Set the product to display (Item entity - for backward compatibility)
     */
    public void setProduct(Item item) {
        this.item = item;
        this.product = null;
        displayProductDetails();
    }

    /**
     * Set main container for navigation
     */
    public void setMainContainer(BorderPane mainContainer) {
        this.mainContainer = mainContainer;
    }

    /**
     * Set services (for old controllers)
     */
    public void setServices(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    /**
     * Set current account ID
     */
    public void setCurrentAccountId(Long accountId) {
        this.currentAccountId = accountId;
        if (cartService != null && accountId != null) {
            currentCart = cartService.getOrCreateCart(accountId);
        }
        if (orderService != null && accountId != null) {
            currentCart = orderService.getOrCreateCart(accountId);
        }
    }

    /**
     * Set parent controller for navigation back
     */
    public void setParentController(CustomerProductController parentController) {
        this.parentController = parentController;
    }
    
    /**
     * Set items controller for navigation back
     */
    public void setItemsController(CustomerItemsController itemsController) {
        this.itemsController = itemsController;
    }

    /**
     * Display product details
     */
    private void displayProductDetails() {
        if (product == null && item == null) return;

        // Product Name
        if (lblProductName != null) {
            String name = product != null ? product.getName() : item.getName();
            lblProductName.setText(name);
        }

        // Category
        if (lblCategory != null) {
            String category;
            if (product != null) {
                category = product.getCategory() != null ? product.getCategory() : "Chưa phân loại";
            } else {
                category = item.getCategory() != null ? item.getCategory().getName() : "Chưa phân loại";
            }
            lblCategory.setText(category);
        }

        // Price
        if (lblPrice != null) {
            BigDecimal price = product != null ? product.getPrice() : item.getPrice();
            lblPrice.setText(formatPrice(price));
        }

        // Stock
        if (lblStock != null) {
            Long stock;
            boolean isAvailable;
            if (product != null) {
                stock = product.getStockQuantity();
                isAvailable = stock != null && stock > 0;
            } else {
                stock = item.getStock() != null ? item.getStock().longValue() : 0;
                isAvailable = item.getStatus() == 1 && stock > 0;
            }
            
            if (isAvailable) {
                lblStock.setText("Còn hàng (" + stock + " sản phẩm)");
                lblStock.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 16px; -fx-font-weight: bold;");
            } else {
                lblStock.setText("Hết hàng");
                lblStock.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 16px; -fx-font-weight: bold;");
            }
        }

        // Description
        if (txtDescription != null) {
            String desc;
            if (product != null) {
                desc = product.getDescription() != null ? product.getDescription() : "Không có mô tả chi tiết.";
            } else {
                desc = item.getDescription() != null ? item.getDescription() : "Không có mô tả chi tiết.";
            }
            txtDescription.setText(desc);
        }

        // Product Image
        if (imageContainer != null) {
            imageContainer.getChildren().clear();
            
            String imageUrl = product != null ? product.getImageUrl() : item.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                try {
                    Image image = new Image(imageUrl, 380, 380, true, true, true);
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(380);
                    imageView.setFitHeight(380);
                    imageView.setPreserveRatio(true);
                    imageView.setSmooth(true);
                    
                    if (!image.isError()) {
                        imageContainer.getChildren().add(imageView);
                        String name = product != null ? product.getName() : item.getName();
                        System.out.println("✓ Product detail image loaded: " + name);
                    } else {
                        addPlaceholderImage();
                    }
                } catch (Exception e) {
                    System.err.println("✗ Error loading product detail image: " + e.getMessage());
                    addPlaceholderImage();
                }
            } else {
                addPlaceholderImage();
            }
        }

        // Disable add to cart if out of stock
        if (btnAddToCart != null) {
            boolean isAvailable;
            if (product != null) {
                isAvailable = product.getStockQuantity() != null && product.getStockQuantity() > 0;
            } else {
                isAvailable = item.getStatus() == 1 && item.getStock() != null && item.getStock() > 0;
            }
            btnAddToCart.setDisable(!isAvailable);
        }

        // Update spinner max value based on stock
        if (spnQuantity != null) {
            Long stockQty;
            if (product != null) {
                stockQty = product.getStockQuantity();
            } else {
                stockQty = item.getStock() != null ? item.getStock().longValue() : 0L;
            }
            
            if (stockQty != null && stockQty > 0) {
                int maxQuantity = Math.min(stockQty.intValue(), 99);
                SpinnerValueFactory<Integer> valueFactory = 
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxQuantity, 1);
                spnQuantity.setValueFactory(valueFactory);
            }
        }
    }

    private void addPlaceholderImage() {
        Label placeholder = new Label("📦");
        placeholder.setFont(Font.font("System", FontWeight.BOLD, 120));
        placeholder.setStyle("-fx-text-fill: #bdc3c7;");
        imageContainer.getChildren().add(placeholder);
    }

    @FXML
    private void handleAddToCart() {
        if (product == null && item == null) return;

        int quantity = spnQuantity != null ? spnQuantity.getValue() : 1;
        
        try {
            if (product != null) {
                // Using Product entity (new flow)
                if (product.getStockQuantity() == null || product.getStockQuantity() < quantity) {
                    showError("Không đủ số lượng trong kho!");
                    return;
                }

                if (cartService == null || currentCart == null) {
                    showError("Không thể thêm vào giỏ hàng. Vui lòng thử lại!");
                    return;
                }

                cartService.addProductToCart(currentCart.getId(), product.getId(), quantity);
                showInfo("Đã thêm " + quantity + " sản phẩm \"" + product.getName() + "\" vào giỏ hàng!");
            } else {
                // Using Item entity (old flow for backward compatibility)
                if (productService == null || !productService.isProductAvailable(item.getId(), quantity)) {
                    showError("Số lượng yêu cầu vượt quá số lượng còn trong kho.");
                    return;
                }

                if (orderService == null || currentCart == null) {
                    showError("Không thể thêm vào giỏ hàng. Vui lòng đăng nhập lại.");
                    return;
                }

                CartItemDAO itemDTO = new CartItemDAO();
                itemDTO.setItemId(item.getId());
                itemDTO.setQuantity(quantity);
                itemDTO.setUnitPrice(item.getPrice());

                orderService.addToCart(currentCart.getId(), itemDTO);
                showInfo("Đã thêm " + quantity + " sản phẩm \"" + item.getName() + "\" vào giỏ hàng!");
            }
            
            // Quay lại trang danh sách
            handleClose();
        } catch (Exception e) {
            showError("Lỗi khi thêm vào giỏ hàng: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose() {
        try {
            // If using Item entity (old flow), load customer_items.fxml
            // If using Product entity (new flow), load customer-products.fxml
            String fxmlPath = item != null 
                ? "/UI/customer/customer_items.fxml" 
                : "/UI/customer/customer-products.fxml";
                
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent page = loader.load();
            
            // Try to get parent container from scene if mainContainer not set
            javafx.scene.Parent root = null;
            if (btnBack != null && btnBack.getScene() != null) {
                root = btnBack.getScene().getRoot();
            }
            
            // Try different container types
            if (root instanceof BorderPane) {
                BorderPane borderPane = (BorderPane) root;
                javafx.scene.Node center = borderPane.getCenter();
                
                // Check if center is StackPane (CustomerDashboard)
                if (center instanceof StackPane) {
                    StackPane stackPane = (StackPane) center;
                    stackPane.getChildren().clear();
                    stackPane.getChildren().add(page);
                    System.out.println("✓ Navigated back to products list (CustomerDashboard/StackPane)");
                    return;
                } else {
                    // CustomerMain case - use mainContainer or borderPane
                    BorderPane container = mainContainer != null ? mainContainer : borderPane;
                    
                    // Set main container for the loaded controller if needed
                    if (item != null) {
                        CustomerItemsController controller = loader.getController();
                        controller.setMainContainer(container);
                    } else {
                        CustomerProductControllerV2 controller = loader.getController();
                        if (controller != null) {
                            controller.setMainContainer(container);
                        }
                    }
                    
                    container.setCenter(page);
                    System.out.println("✓ Navigated back to products list (CustomerMain/BorderPane)");
                    return;
                }
            } else if (mainContainer != null) {
                // Fallback to mainContainer if available
                if (item != null) {
                    CustomerItemsController controller = loader.getController();
                    controller.setMainContainer(mainContainer);
                } else {
                    CustomerProductControllerV2 controller = loader.getController();
                    if (controller != null) {
                        controller.setMainContainer(mainContainer);
                    }
                }
                
                mainContainer.setCenter(page);
                System.out.println("✓ Navigated back to products list (using mainContainer)");
            } else {
                showError("Không thể quay lại. Vui lòng đóng cửa sổ.");
            }
        } catch (Exception e) {
            System.err.println("✗ Error navigating back: " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi khi quay lại: " + e.getMessage());
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
