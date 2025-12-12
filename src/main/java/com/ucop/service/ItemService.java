package com.ucop.service;

import java.util.List;

import com.ucop.dao.ItemDAO;
import com.ucop.entity.Item;

public class ItemService {

    private final ItemDAO dao = new ItemDAO();
    private final AuditLogService audit = new AuditLogService();

    // ========================= CREATE =========================
    public void save(Item item) {

        // Validate SKU
        if (item.getSku() == null || item.getSku().isBlank()) {
            throw new IllegalArgumentException("SKU cannot be empty!");
        }

        if (dao.findBySku(item.getSku()) != null) {
            throw new IllegalArgumentException("SKU already exists!");
        }

        // Validate price
        if (item.getPrice() != null && item.getPrice().doubleValue() < 0) {
            throw new IllegalArgumentException("Price cannot be negative!");
        }

        dao.save(item);

        audit.log(
                "Item",
                item.getId(),
                "CREATE",
                null,
                item,
                "Item created"
        );
    }

    // ========================= UPDATE =========================
    public void update(Item newItem) {

        Item oldItem = dao.findById(newItem.getId().intValue());
        if (oldItem == null) {
            throw new IllegalArgumentException("Item not found!");
        }

        // Validate SKU update
        Item existed = dao.findBySku(newItem.getSku());
        if (existed != null && !existed.getId().equals(newItem.getId())) {
            throw new IllegalArgumentException("SKU already used by another item!");
        }

        // Copy old item for audit log
        Item oldCopy = new Item();
        oldCopy.setSku(oldItem.getSku());
        oldCopy.setName(oldItem.getName());
        oldCopy.setDescription(oldItem.getDescription());
        oldCopy.setPrice(oldItem.getPrice());
        oldCopy.setStock(oldItem.getStock());
        oldCopy.setStatus(oldItem.getStatus());
        oldCopy.setUnit(oldItem.getUnit());
        oldCopy.setWeight(oldItem.getWeight());
        oldCopy.setImageUrl(oldItem.getImageUrl());
        oldCopy.setCategory(oldItem.getCategory());

        dao.update(newItem);

        audit.log(
                "Item",
                newItem.getId().longValue(),
                "UPDATE",
                oldCopy,
                newItem,
                "Item updated"
        );
    }

    // ========================= DELETE =========================
    public void delete(Item item) {

        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null!");
        }

        dao.delete(item);

        audit.log(
                "Item",
                item.getId().longValue(),
                "DELETE",
                item,
                null,
                "Item deleted"
        );
    }

    // ========================= FIND =========================
    public List<Item> findAll() {
        return dao.findAll();
    }

    public Item findById(int id) {
        return dao.findById(id);
    }

    public List<Item> findByCategory(int categoryId) {
        return dao.findByCategory(categoryId);
    }
}
