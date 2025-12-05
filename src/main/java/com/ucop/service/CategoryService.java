package com.ucop.service;

import com.ucop.dao.CategoryDAO;
import com.ucop.entity.Category;

import java.util.List;

public class CategoryService {

    private final CategoryDAO dao = new CategoryDAO();

    public void save(Category cat){
        dao.save(cat);
    }

    public List<Category> findAll(){
        return dao.findAll();
    }

    public Category findById(int id){
        return dao.findById(id);
    }
}
