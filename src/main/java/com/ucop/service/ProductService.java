package com.ucop.service;

import com.ucop.entity.Product;
import com.ucop.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing products
 */
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Create or update product
     */
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Get product by ID
     */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Get all products
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Get active products only
     */
    public List<Product> getActiveProducts() {
        return productRepository.findActiveProducts();
    }

    /**
     * Get products in stock
     */
    public List<Product> getInStockProducts() {
        return productRepository.findInStockProducts();
    }

    /**
     * Search products by keyword
     */
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.findByNameContaining(keyword.trim());
    }

    /**
     * Get products by category
     */
    public List<Product> getProductsByCategory(String category) {
        if (category == null || category.trim().isEmpty() || "Tất cả".equals(category)) {
            return getAllProducts();
        }
        return productRepository.findByCategory(category);
    }

    /**
     * Filter and search products with multiple criteria
     */
    public List<Product> filterProducts(String keyword, String category, 
                                        BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> products = getActiveProducts();

        // Filter by keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            products = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(lowerKeyword) ||
                               (p.getDescription() != null && 
                                p.getDescription().toLowerCase().contains(lowerKeyword)))
                    .collect(Collectors.toList());
        }

        // Filter by category
        if (category != null && !category.trim().isEmpty() && !"Tất cả".equals(category)) {
            products = products.stream()
                    .filter(p -> category.equals(p.getCategory()))
                    .collect(Collectors.toList());
        }

        // Filter by price range
        if (minPrice != null) {
            products = products.stream()
                    .filter(p -> p.getPrice().compareTo(minPrice) >= 0)
                    .collect(Collectors.toList());
        }
        if (maxPrice != null) {
            products = products.stream()
                    .filter(p -> p.getPrice().compareTo(maxPrice) <= 0)
                    .collect(Collectors.toList());
        }

        return products;
    }

    /**
     * Sort products by different criteria
     */
    public List<Product> sortProducts(List<Product> products, String sortBy) {
        if (products == null || products.isEmpty()) {
            return products;
        }

        return switch (sortBy) {
            case "Giá tăng dần" -> products.stream()
                    .sorted(Comparator.comparing(Product::getPrice))
                    .collect(Collectors.toList());
            case "Giá giảm dần" -> products.stream()
                    .sorted(Comparator.comparing(Product::getPrice).reversed())
                    .collect(Collectors.toList());
            case "Tên A-Z" -> products.stream()
                    .sorted(Comparator.comparing(Product::getName))
                    .collect(Collectors.toList());
            case "Mới nhất" -> products.stream()
                    .sorted(Comparator.comparing(Product::getCreatedAt).reversed())
                    .collect(Collectors.toList());
            default -> products;
        };
    }

    /**
     * Get product categories
     */
    public List<String> getCategories() {
        return productRepository.findAll().stream()
                .map(Product::getCategory)
                .filter(cat -> cat != null && !cat.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Update product stock
     */
    public void updateStock(Long productId, Long quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Product not found with id: " + productId);
        }

        Product product = productOpt.get();
        product.setStockQuantity(quantity);
        productRepository.save(product);
    }

    /**
     * Delete product
     */
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * Count total products
     */
    public long countProducts() {
        return productRepository.count();
    }

    /**
     * Check if product is available for purchase
     */
    public boolean isProductAvailable(Long productId, Long requestedQuantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return false;
        }

        Product product = productOpt.get();
        return product.getIsActive() && 
               product.getStockQuantity() >= requestedQuantity;
    }
}
