package com.ucop.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ucop.dao.ProductDAO;
import com.ucop.entity.Product;

/**
 * Service cho quản lý products từ bảng products
 * Sử dụng entity Product và ProductDAO
 */
public class ProductServiceV2 {
    
    private final ProductDAO productDAO;

    public ProductServiceV2() {
        this.productDAO = new ProductDAO();
    }
    
    public ProductServiceV2(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    /**
     * Lưu hoặc cập nhật product
     */
    public Product saveProduct(Product product) {
        return productDAO.save(product);
    }

    /**
     * Lấy product theo ID
     */
    public Optional<Product> getProductById(Long id) {
        return productDAO.findById(id);
    }

    /**
     * Lấy tất cả products
     */
    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    /**
     * Lấy các products đang active
     */
    public List<Product> getActiveProducts() {
        return productDAO.findActiveProducts();
    }

    /**
     * Lấy products có trong kho
     */
    public List<Product> getInStockProducts() {
        return productDAO.findInStockProducts();
    }

    /**
     * Tìm kiếm products theo keyword
     */
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return productDAO.findByNameContaining(keyword.trim());
    }

    /**
     * Lấy products theo category
     */
    public List<Product> getProductsByCategory(String category) {
        if (category == null || category.trim().isEmpty() || "Tất cả".equals(category)) {
            return getActiveProducts();
        }
        return productDAO.findByCategory(category);
    }

    /**
     * Lọc products với nhiều tiêu chí
     */
    public List<Product> filterProducts(String keyword, String category, 
                                        BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> products = getActiveProducts();

        // Lọc theo keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            products = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(lowerKeyword) ||
                               (p.getDescription() != null && 
                                p.getDescription().toLowerCase().contains(lowerKeyword)))
                    .collect(Collectors.toList());
        }

        // Lọc theo category
        if (category != null && !category.trim().isEmpty() && !"Tất cả".equals(category)) {
            products = products.stream()
                    .filter(p -> p.getCategory() != null && category.equals(p.getCategory()))
                    .collect(Collectors.toList());
        }

        // Lọc theo khoảng giá
        if (minPrice != null) {
            products = products.stream()
                    .filter(p -> p.getPrice() != null && p.getPrice().compareTo(minPrice) >= 0)
                    .collect(Collectors.toList());
        }
        if (maxPrice != null) {
            products = products.stream()
                    .filter(p -> p.getPrice() != null && p.getPrice().compareTo(maxPrice) <= 0)
                    .collect(Collectors.toList());
        }

        return products;
    }

    /**
     * Sắp xếp products theo tiêu chí
     */
    public List<Product> sortProducts(List<Product> products, String sortBy) {
        if (products == null || products.isEmpty()) {
            return products;
        }

        return switch (sortBy) {
            case "Giá tăng dần" -> products.stream()
                    .sorted(Comparator.comparing(Product::getPrice, Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.toList());
            case "Giá giảm dần" -> products.stream()
                    .sorted(Comparator.comparing(Product::getPrice, Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
            case "Tên A-Z" -> products.stream()
                    .sorted(Comparator.comparing(Product::getName, Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.toList());
            case "Mới nhất" -> products.stream()
                    .sorted(Comparator.comparing(Product::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
            default -> products;
        };
    }

    /**
     * Lấy danh sách categories
     */
    public List<String> getCategories() {
        return productDAO.findAllCategories();
    }

    /**
     * Cập nhật stock
     */
    public void updateStock(Long productId, Long quantity) {
        Optional<Product> productOpt = productDAO.findById(productId);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Product not found with id: " + productId);
        }
        productDAO.updateStock(productId, quantity);
    }

    /**
     * Xóa product
     */
    public void deleteProduct(Long id) {
        productDAO.deleteById(id);
    }

    /**
     * Đếm tổng số products
     */
    public long countProducts() {
        return productDAO.count();
    }

    /**
     * Đếm số products active
     */
    public long countActiveProducts() {
        return productDAO.countActive();
    }

    /**
     * Kiểm tra product có sẵn để mua không
     */
    public boolean isProductAvailable(Long productId, Long requestedQuantity) {
        Optional<Product> productOpt = productDAO.findById(productId);
        if (productOpt.isEmpty()) {
            return false;
        }

        Product product = productOpt.get();
        return product.getIsActive() && 
               product.getStockQuantity() != null &&
               product.getStockQuantity() >= requestedQuantity;
    }
}
