package com.ucop.service;

import com.ucop.dao.CategoryDAO;
import com.ucop.entity.Category;

import java.util.List;

public class CategoryService {

    private final CategoryDAO dao = new CategoryDAO();
    private final AuditLogService audit = new AuditLogService();

    public void update(Category newData) {

        Category oldData = dao.findById(newData.getId());
        if (oldData == null) {
            System.err.println("Category update failed: ID not found!");
            return;
        }

        // === Clone oldData để audit ===
        Category oldCopy = new Category();
        oldCopy.setId(oldData.getId());
        oldCopy.setName(oldData.getName());
        oldCopy.setParent(oldData.getParent());  // Copy parent cấp 1

        // === Update vào DB ===
        dao.update(newData);

        // === Audit Log ===
        audit.log(
            "Category",
            newData.getId().longValue(),
            "UPDATE",
            oldCopy,
            newData
        );
    }

    public void save(Category cat){
        dao.save(cat);

        audit.log(
            "Category",
            cat.getId().longValue(),
            "CREATE",
            null,
            cat
        );
    }

    public List<Category> findAll(){
        return dao.findAll();
    }

    public Category findById(int id){
        return dao.findById(id);
    }
}
