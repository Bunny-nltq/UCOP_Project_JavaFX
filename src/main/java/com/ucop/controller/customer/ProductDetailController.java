package com.ucop.controller.customer;

import com.ucop.entity.Item;
import com.ucop.dao.CartItemDTO;
import com.ucop.entity.Cart;
import com.ucop.service.ProductService;
import com.ucop.service.OrderService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Controller for Product Detail View
 */
public class ProductDetailController {

    @FXML private Label lblProductName;
    @FXML private Label lblCategory;
    @FXML private Label lblPrice;
    @FXML private Label lblStock;
    @FXML private TextArea txtDescription;
    @FXML private Spinner<Integer> spnQuantity;
    @FXML private Button btnAddToCart;
    @FXML private StackPane imageContainer;

    private Item product;
    private ProductService productService;
    private OrderService orderService;
    private Long currentAccountId;
    private Cart currentCart;
    private CustomerProductController parentController;

    @FXML
    public void initialize() {
        // Setup quantity spinner
        if (spnQuantity != null) {
            SpinnerValueFactory<Integer> valueFactory = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1);
            spnQuantity.setValueFactory(valueFactory);
        }
    }

    /**
     * Set the product to display
     */
    public void setProduct(Item product) {
        this.product = product;
        displayProductDetails();
    }

    /**
     * Set services
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
     * Display product details
     */
    private void displayProductDetails() {
        if (product == null) return;

        // Display product image
        if (imageContainer != null) {
            imageContainer.getChildren().clear();
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                try {
                    ImageView imageView = new ImageView(new Image(product.getImageUrl(), true));
                    imageView.setFitWidth(320);
                    imageView.setFitHeight(320);
                    imageView.setPreserveRatio(true);
                    imageView.setSmooth(true);
                    imageContainer.getChildren().add(imageView);
                } catch (Exception e) {
                    // Fallback to emoji
                    Label imgPlaceholder = new Label("📦");
                    imgPlaceholder.setFont(Font.font("System", FontWeight.BOLD, 120));
                    imageContainer.getChildren().add(imgPlaceholder);
                }
            } else {
                // Default emoji
                Label imgPlaceholder = new Label("📦");
                imgPlaceholder.setFont(Font.font("System", FontWeight.BOLD, 120));
                imageContainer.getChildren().add(imgPlaceholder);
            }
        }

        if (lblProductName != null) {
            lblProductName.setText(product.getName());
        }

        if (lblCategory != null) {
            lblCategory.setText(product.getCategory() != null ? product.getCategory().getName() : "");
        }

        if (lblPrice != null) {
            lblPrice.setText(formatPrice(product.getPrice()));
        }

        if (lblStock != null) {
            boolean inStock = product.getStatus() == 1 && product.getStock() > 0;
            String stockText = inStock 
                ? "Còn hàng (" + product.getStock() + " sản phẩm)"
                : "Hết hàng";
            lblStock.setText(stockText);
            lblStock.setStyle(inStock
                ? "-fx-text-fill: green;" 
                : "-fx-text-fill: red;");
        }

        if (txtDescription != null) {
            txtDescription.setText(product.getDescription() != null ? product.getDescription() : "");
        }

        if (btnAddToCart != null) {
            btnAddToCart.setDisable(!(product.getStatus() == 1 && product.getStock() > 0));
        }

        if (spnQuantity != null && product.getStock() != null) {
            int maxQuantity = Math.min(product.getStock(), 99);
            SpinnerValueFactory<Integer> valueFactory = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxQuantity, 1);
            spnQuantity.setValueFactory(valueFactory);
            spnQuantity.setDisable(!(product.getStatus() == 1 && product.getStock() > 0));
        }
    }

    @FXML
    private void handleAddToCart() {
        if (product == null || orderService == null || currentCart == null) {
            showError("Không thể thêm vào giỏ hàng. Vui lòng đăng nhập lại.");
            return;
        }

        try {
            int quantity = spnQuantity != null ? spnQuantity.getValue() : 1;

            // Check if product is available
            if (!productService.isProductAvailable(product.getId(), quantity)) {
                showError("Số lượng yêu cầu vượt quá số lượng còn trong kho.");
                return;
            }

            // Add to cart
            CartItemDTO itemDTO = new CartItemDTO();
            itemDTO.setItemId(product.getId());
            itemDTO.setQuantity(quantity);
            itemDTO.setUnitPrice(product.getPrice());

            orderService.addToCart(currentCart.getId(), itemDTO);

            showInfo("Đã thêm " + quantity + " sản phẩm \"" + product.getName() + "\" vào giỏ hàng!");
            
            // Close dialog
            handleClose();
        } catch (Exception e) {
            showError("Lỗi khi thêm vào giỏ hàng: " + e.getMessage());
        }
    }

    @FXML
    private void handleClose() {
        if (parentController != null) {
            try {
                // Quay lại trang sản phẩm
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/customer/customer-products.fxml"));
                Parent root = loader.load();
                
                CustomerProductController controller = loader.getController();
                controller.setServices(productService, orderService);
                controller.setCurrentAccountId(currentAccountId);
                
                // Lấy BorderPane cha từ CustomerMainController
                javafx.scene.layout.BorderPane mainContainer = (javafx.scene.layout.BorderPane) lblProductName.getScene().getRoot();
                if (mainContainer != null) {
                    mainContainer.setCenter(root);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Fallback: đóng stage nếu mở dạng modal
            Stage stage = (Stage) lblProductName.getScene().getWindow();
            stage.close();
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
