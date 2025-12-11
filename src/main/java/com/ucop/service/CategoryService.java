package com.ucop.service;

import com.ucop.dao.CategoryDAO;
import com.ucop.entity.Category;

import java.util.List;

public class CategoryService {

    private final CategoryDAO dao = new CategoryDAO();
    private final AuditLogService audit = new AuditLogService();

    // ========================= CREATE =========================
    public void save(Category cat) {

        // Validate parent
        if (cat.getParent() != null && cat.getParent().getId().equals(cat.getId())) {
            throw new IllegalArgumentException("Category cannot be parent of itself!");
        }

        dao.save(cat); // ID generated

        audit.log(
                "Category",
                cat.getId().longValue(),
                "CREATE",
                null,
                cat,
                "Category created"
        );
    }

    // ========================= UPDATE =========================
    public void update(Category newData) {

        Category oldData = dao.findById(newData.getId());
        if (oldData == null) {
            throw new IllegalArgumentException("Category update failed: ID not found!");
        }

        // Check parent validity
        if (newData.getParent() != null && newData.getParent().getId().equals(newData.getId())) {
            throw new IllegalArgumentException("Category cannot be parent of itself!");
        }

        // Copy FULL DATA for audit log
        Category oldCopy = new Category();
        oldCopy.setId(oldData.getId());
        oldCopy.setName(oldData.getName());
        oldCopy.setDescription(oldData.getDescription());
        oldCopy.setStatus(oldData.getStatus());
        oldCopy.setParent(oldData.getParent());

        // Update DB
        dao.update(newData);

        // Audit
        audit.log(
                "Category",
                newData.getId().longValue(),
                "UPDATE",
                oldCopy,
                newData,
                "Category updated"
        );
    }

    // ========================= DELETE =========================
    public boolean delete(int id) {

        Category cat = dao.findById(id);
        if (cat == null) return false;

        // Không cho xoá nếu có Category con
        if (cat.getChildren() != null && !cat.getChildren().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete category that has children!");
        }

        boolean success = dao.delete(id);

        if (success) {
            audit.log(
                    "Category",
                    (long) id,
                    "DELETE",
                    cat,
                    null,
                    "Category deleted"
            );
        }

        return success;
    }

    // ========================= FIND =========================
    public List<Category> findAll() {
        return dao.findAll();
    }

    public Category findById(int id) {
        return dao.findById(id);
    }

    public List<Category> findRoot() {
        return dao.findRoot();
    }

    public List<Category> findChildren(int id) {
        return dao.findChildren(id);
    }
}
