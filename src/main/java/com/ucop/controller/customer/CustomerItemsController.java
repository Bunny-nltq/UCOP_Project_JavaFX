package com.ucop.controller.customer;

import com.ucop.entity.Category;
import com.ucop.entity.Item;
import com.ucop.repository.impl.CartRepositoryImpl;
import com.ucop.service.CartService;
import com.ucop.service.CategoryService;
import com.ucop.service.ItemService;
import com.ucop.util.HibernateUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.File;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;

public class CustomerItemsController implements Initializable {

    private CustomerMainController customerMainController;

    @FXML private GridPane gridItems;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<Category> cboCategory;
    @FXML private Label lblTotal;

    private ItemService itemService;
    private CartService cartService;
    private final CategoryService categoryService = new CategoryService();
    private Long currentAccountId;


    private List<Item> items = new ArrayList<>();

    // ================= INIT =================
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("[DEBUG] CustomerItemsController init: " + getClass().getName());

        setupCategoryCombo();

        // ✅ fallback itemService nếu chưa inject
        if (itemService == null) {
            System.out.println("[DEBUG] ItemService not injected -> fallback create");
            itemService = new ItemService();
        }

        // ✅ QUAN TRỌNG: luôn đảm bảo cartService có (không phụ thuộc itemService null)
        ensureCartService();

        loadItems();
        renderItems();
    }

    // ================= INJECT =================
    public void setCustomerMainController(CustomerMainController controller) {
        this.customerMainController = controller;
        System.out.println("[DEBUG] setCustomerMainController called: " + (controller != null));
    }

    public void setItemService(ItemService service) {
        this.itemService = service;
        System.out.println("[DEBUG] setItemService called: " + (service != null));
        loadItems();
        renderItems();
    }

    public void setCartService(CartService service) {
        this.cartService = service;
        System.out.println("[DEBUG] setCartService called: " + (service != null));
        // ✅ nếu inject null thì fallback luôn
        ensureCartService();
    }
    
    public void setCurrentAccountId(Long currentAccountId) {
        this.currentAccountId = currentAccountId;
        System.out.println("[DEBUG] setCurrentAccountId called: " + currentAccountId);
    }


    // ================= CATEGORY =================
    private void setupCategoryCombo() {
        List<Category> categories = new ArrayList<>();

        Category all = new Category();
        all.setId(null);
        all.setName("Tất cả danh mục");
        categories.add(all);

        categories.addAll(categoryService.findAll());
        cboCategory.getItems().setAll(categories);
        cboCategory.getSelectionModel().selectFirst();

        cboCategory.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        cboCategory.setButtonCell(cboCategory.getCellFactory().call(null));
    }

    // ================= LOAD =================
    private void loadItems() {
        if (itemService == null) return;

        items = itemService.findForCustomer();
        System.out.println("[DEBUG] items loaded = " + items.size());

        if (lblTotal != null) {
            lblTotal.setText("Tổng: " + items.size() + " sản phẩm");
        }
    }

    // ================= RENDER =================
    private void renderItems() {
        if (gridItems == null) return;

        Platform.runLater(() -> {
            gridItems.getChildren().clear();
            gridItems.setHgap(16);
            gridItems.setVgap(16);

            int col = 0, row = 0;
            for (Item item : items) {
                gridItems.add(createItemCard(item), col++, row);
                if (col == 3) {
                    col = 0;
                    row++;
                }
            }
        });
    }

    // ================= SEARCH =================
    @FXML
    private void handleSearch() {
        if (itemService == null) return;

        Category selected = cboCategory.getValue();
        Long categoryId = (selected != null && selected.getId() != null)
                ? ((Number) selected.getId()).longValue()
                : null;

        items = itemService.searchForCustomer(
                txtSearch != null ? txtSearch.getText() : null,
                categoryId
        );

        if (lblTotal != null) {
            lblTotal.setText("Tổng: " + items.size() + " sản phẩm");
        }
        renderItems();
    }

    // ================= CARD =================
    private VBox createItemCard(Item item) {
        ImageView iv = new ImageView(loadItemImage(item));
        iv.setFitWidth(180);
        iv.setFitHeight(140);
        iv.setPreserveRatio(true);

        Label name = new Label(safe(item.getName()));
        name.setStyle("-fx-font-weight: bold;");
        name.setMaxWidth(180);

        NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        Label price = new Label(item.getPrice() == null ? "0 ₫" : fmt.format(item.getPrice()));
        price.setStyle("-fx-text-fill: #E53935; -fx-font-weight: bold;");

        Button btnAdd = new Button("🛒 Thêm vào giỏ");
        Button btnDetail = new Button("👁 Xem chi tiết");

        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnDetail.setMaxWidth(Double.MAX_VALUE);

        btnAdd.setOnAction(e -> addToCart(item));
        btnDetail.setOnAction(e -> showDetail(item));

        VBox box = new VBox(8, iv, name, price, btnAdd, btnDetail);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.setPrefWidth(220);
        box.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #DDDDDD;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
        """);

        return box;
    }

    // ================= CART =================
    private void addToCart(Item item) {
        // ✅ đảm bảo cartService luôn có trước khi dùng
        ensureCartService();

        try {
            // ✅ lấy accountId theo ưu tiên:
            // 1) từ customerMainController (nếu inject được)
            // 2) fallback từ currentAccountId
            Long accountId = null;
            if (customerMainController != null) {
                accountId = customerMainController.getCurrentAccountId();
            }
            if (accountId == null) {
                accountId = currentAccountId;
            }

            if (accountId == null) {
                alert("Thiếu accountId (chưa inject).", Alert.AlertType.WARNING);
                return;
            }

            if (item == null || item.getId() == null) {
                alert("Sản phẩm không hợp lệ.", Alert.AlertType.ERROR);
                return;
            }

            var cart = cartService.getOrCreateCart(accountId);
            if (cart == null || cart.getId() == null) {
                alert("Không thể tạo/ lấy giỏ hàng.", Alert.AlertType.ERROR);
                return;
            }

            Long itemId = item.getId().longValue();
            cartService.addToCart(cart.getId(), itemId, 1, item.getPrice());

            // ✅ update badge nếu có mainController
            if (customerMainController != null) {
                customerMainController.updateCartCount();
            }

            alert("Đã thêm vào giỏ hàng!", Alert.AlertType.INFORMATION);

        } catch (Exception ex) {
            ex.printStackTrace();
            alert("Không thể thêm vào giỏ: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private void ensureCartService() {
        if (cartService != null) return;

        var sf = HibernateUtil.getSessionFactory();
        cartService = new CartService(new CartRepositoryImpl(sf));
        System.out.println("[DEBUG] cartService fallback created");
    }

    // ================= DETAIL =================
    private void showDetail(Item item) {
        NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        StringBuilder sb = new StringBuilder()
                .append("SKU: ").append(safe(item.getSku())).append("\n")
                .append("Tên: ").append(safe(item.getName())).append("\n")
                .append("Giá: ").append(item.getPrice() == null ? "0 ₫" : fmt.format(item.getPrice()));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết sản phẩm");
        alert.setHeaderText(item.getName());

        ImageView iv = new ImageView(loadItemImage(item));
        iv.setFitWidth(260);
        iv.setPreserveRatio(true);

        VBox content = new VBox(10, iv, new Label(sb.toString()));
        content.setPadding(new Insets(10));
        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }

    // ================= IMAGE =================
    private Image loadItemImage(Item item) {
        try {
            String path = item.getImagePath();
            if (path != null && !path.isBlank()) {
                File f = new File(path);
                if (f.exists()) return new Image(f.toURI().toString(), true);
                if (path.startsWith("http")) return new Image(path, true);
            }
        } catch (Exception ignored) {}

        return new Image(
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO7WQZcAAAAASUVORK5CYII="
        );
    }

    private void alert(String msg, Alert.AlertType type) {
        new Alert(type, msg, ButtonType.OK).show();
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
