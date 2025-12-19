package com.ucop.service;

import com.ucop.dao.CategoryDAO;
import com.ucop.entity.Category;

import java.util.List;

public class CategoryService {

    private final CategoryDAO dao = new CategoryDAO();
    private final AuditLogService audit = new AuditLogService();

    // ========================= CREATE =========================
    public void save(Category cat) {

        if (cat.getParent() != null
                && cat.getParent().getId() != null
                && cat.getParent().getId().equals(cat.getId())) {
            throw new IllegalArgumentException("Category cannot be parent of itself!");
        }

        dao.save(cat);

        // ✅ LOG SNAPSHOT – KHÔNG LOG ENTITY
        String newValue = String.format(
                "Category{id=%d, name='%s', status=%d, parentId=%s}",
                cat.getId(),
                cat.getName(),
                cat.getStatus(),
                cat.getParent() != null ? cat.getParent().getId() : "null"
        );

        audit.log(
                "Category",
                cat.getId().longValue(),
                "CREATE",
                null,
                newValue,
                "Category created"
        );
    }

    // ========================= UPDATE =========================
    public void update(Category newData) {

        Category oldData = dao.findById(newData.getId());
        if (oldData == null) {
            throw new IllegalArgumentException("Category update failed: ID not found!");
        }

        if (newData.getParent() != null
                && newData.getParent().getId().equals(newData.getId())) {
            throw new IllegalArgumentException("Category cannot be parent of itself!");
        }

        // ✅ SNAPSHOT CŨ (AN TOÀN – KHÔNG LAZY)
        String oldValue = String.format(
                "Category{id=%d, name='%s', status=%d, parentId=%s}",
                oldData.getId(),
                oldData.getName(),
                oldData.getStatus(),
                oldData.getParent() != null ? oldData.getParent().getId() : "null"
        );

        dao.update(newData);

        // ✅ SNAPSHOT MỚI
        String newValue = String.format(
                "Category{id=%d, name='%s', status=%d, parentId=%s}",
                newData.getId(),
                newData.getName(),
                newData.getStatus(),
                newData.getParent() != null ? newData.getParent().getId() : "null"
        );

        audit.log(
                "Category",
                newData.getId().longValue(),
                "UPDATE",
                oldValue,
                newValue,
                "Category updated"
        );
    }

    // ========================= DELETE =========================
    public void delete(int id) {

        Category cat = dao.findById(id);
        if (cat == null) {
            throw new IllegalArgumentException("Category not found!");
        }

        // ✅ KIỂM TRA CON TRƯỚC KHI XÓA
        if (dao.hasChildren(id)) {
            throw new IllegalStateException(
                    "Cannot delete category because it has child categories!"
            );
        }

        // ✅ SNAPSHOT TRƯỚC KHI XÓA
        String oldValue = String.format(
                "Category{id=%d, name='%s', status=%d}",
                cat.getId(),
                cat.getName(),
                cat.getStatus()
        );

        dao.delete(id);

        audit.log(
                "Category",
                (long) id,
                "DELETE",
                oldValue,
                null,
                "Category deleted"
        );
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
