package com.ucop.repository;

import java.util.List;
import java.util.Optional;

import com.ucop.entity.Category;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findById(Integer id);
    List<Category> findAll();
    List<Category> findActiveCategories();
    void delete(Category category);
    void deleteById(Integer id);
    long count();
}
