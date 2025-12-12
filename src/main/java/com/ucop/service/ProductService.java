package com.ucop.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ucop.entity.Item;
import com.ucop.repository.ProductRepository;

/**
 * Service for managing items (formerly products)
 */
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Create or update item
     */
    public Item saveProduct(Item product) {
        return productRepository.save(product);
    }

    /**
     * Get item by ID
     */
    public Optional<Item> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Get all items
     */
    public List<Item> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Get active items only
     */
    public List<Item> getActiveProducts() {
        return productRepository.findActiveProducts();
    }

    /**
     * Get items in stock
     */
    public List<Item> getInStockProducts() {
        return productRepository.findInStockProducts();
    }

    /**
     * Search items by keyword
     */
    public List<Item> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.findByNameContaining(keyword.trim());
    }

    /**
     * Get items by category
     */
    public List<Item> getProductsByCategory(String category) {
        if (category == null || category.trim().isEmpty() || "Tất cả".equals(category)) {
            return getAllProducts();
        }
        return productRepository.findByCategory(category);
    }

    /**
     * Filter and search items with multiple criteria
     */
    public List<Item> filterProducts(String keyword, String category, 
                                        BigDecimal minPrice, BigDecimal maxPrice) {
        List<Item> products = getActiveProducts();

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
                    .filter(p -> p.getCategory() != null && category.equals(p.getCategory().getName()))
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
     * Sort items by different criteria
     */
    public List<Item> sortProducts(List<Item> products, String sortBy) {
        if (products == null || products.isEmpty()) {
            return products;
        }

        return switch (sortBy) {
            case "Giá tăng dần" -> products.stream()
                    .sorted(Comparator.comparing(Item::getPrice))
                    .collect(Collectors.toList());
            case "Giá giảm dần" -> products.stream()
                    .sorted(Comparator.comparing(Item::getPrice).reversed())
                    .collect(Collectors.toList());
            case "Tên A-Z" -> products.stream()
                    .sorted(Comparator.comparing(Item::getName))
                    .collect(Collectors.toList());
            case "Mới nhất" -> products;
            default -> products;
        };
    }

    /**
     * Get item categories
     */
    public List<String> getCategories() {
        return productRepository.findAll().stream()
                .map(item -> item.getCategory() != null ? item.getCategory().getName() : null)
                .filter(cat -> cat != null && !cat.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Update item stock
     */
    public void updateStock(Long productId, Integer quantity) {
        Optional<Item> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Item not found with id: " + productId);
        }

        Item product = productOpt.get();
        product.setStock(quantity);
        productRepository.save(product);
    }

    /**
     * Delete item
     */
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * Count total items
     */
    public long countProducts() {
        return productRepository.count();
    }

    /**
     * Check if item is available for purchase
     */
    public boolean isProductAvailable(Long productId, Integer requestedQuantity) {
        Optional<Item> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return false;
        }

        Item product = productOpt.get();
        return product.getStatus() == 1 && 
               product.getStock() >= requestedQuantity;
    }
}
