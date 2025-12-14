package com.ucop.repository;

import java.util.List;
import java.util.Optional;

import com.ucop.entity.Item;

public interface ProductRepository {
    Item save(Item item);
    Optional<Item> findById(Long id);
    List<Item> findAll();
    List<Item> findByCategory(String category);
    List<Item> findByNameContaining(String keyword);
    List<Item> findActiveProducts();
    List<Item> findInStockProducts();
    void delete(Item item);
    void deleteById(Long id);
    long count();
}
