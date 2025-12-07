package com.ucop.repository;

import com.ucop.entity.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    List<Product> findByCategory(String category);
    List<Product> findByNameContaining(String keyword);
    List<Product> findActiveProducts();
    List<Product> findInStockProducts();
    void delete(Product product);
    void deleteById(Long id);
    long count();
}
