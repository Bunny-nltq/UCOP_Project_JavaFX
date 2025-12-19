package com.ucop.service;

import com.ucop.entity.*;
import com.ucop.repository.*;

import java.util.*;

public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final StockItemRepository stockItemRepository;

    public WarehouseService(WarehouseRepository warehouseRepository,
                            StockItemRepository stockItemRepository) {
        this.warehouseRepository = warehouseRepository;
        this.stockItemRepository = stockItemRepository;
    }

    /**
     * Create warehouse
     */
    public Warehouse createWarehouse(String name, String address, String phone) {
        Warehouse warehouse = new Warehouse(name, address, phone);
        // createdAt, updatedAt tự được set bởi @PrePersist
        return warehouseRepository.save(warehouse);
    }

    /**
     * Get warehouse by ID
     */
    public Optional<Warehouse> getWarehouse(Long warehouseId) {
        return warehouseRepository.findById(warehouseId);
    }

    /**
     * Get all warehouses
     */
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    /**
     * Get only active warehouses
     */
    public List<Warehouse> getActiveWarehouses() {
        return warehouseRepository.findAllActive();
    }

    /**
     * Update warehouse info
     */
    public void updateWarehouse(Long warehouseId, String name, String address, String phone) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));

        warehouse.setName(name);
        warehouse.setAddress(address);
        warehouse.setPhone(phone);

        // updatedAt sẽ tự set bởi @PreUpdate
        warehouseRepository.update(warehouse);
    }

    /**
     * Deactivate warehouse
     */
    public void deactivateWarehouse(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));

        warehouse.setIsActive(false);

        warehouseRepository.update(warehouse);
    }

    /**
     * Add stock item to warehouse
     */
    public StockItem addStockItem(Long warehouseId, Long itemId, Long onHand, Long lowStockThreshold) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));

        Optional<StockItem> existing = stockItemRepository.findByWarehouseAndItem(warehouseId, itemId);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Stock item already exists in this warehouse");
        }

        StockItem stockItem = new StockItem(warehouse, itemId, onHand, lowStockThreshold);

        // createdAt sẽ tự set bởi @PrePersist
        return stockItemRepository.save(stockItem);
    }

    /**
     * Update stock quantity
     */
    public void updateStockQuantity(Long stockItemId, Long newQuantity) {
        StockItem stock = stockItemRepository.findById(stockItemId)
                .orElseThrow(() -> new IllegalArgumentException("Stock item not found"));

        stock.setOnHand(newQuantity);
        stock.updateLowStockStatus();

        // updatedAt sẽ tự set bởi @PreUpdate
        stockItemRepository.update(stock);
    }

    /**
     * Get stock item
     */
    public Optional<StockItem> getStockItem(Long stockItemId) {
        return stockItemRepository.findById(stockItemId);
    }

    /**
     * Get all stock items of a warehouse
     */
    public List<StockItem> getStockItemsByWarehouse(Long warehouseId) {
        return stockItemRepository.findByWarehouseId(warehouseId);
    }

    /**
     * Get low stock items
     */
    public List<StockItem> getLowStockItems() {
        return stockItemRepository.findLowStock();
    }

    /**
     * Check if warehouse has enough stock
     */
    public boolean isStockAvailable(Long warehouseId, Long itemId, Long requiredQuantity) {
        return stockItemRepository.findByWarehouseAndItem(warehouseId, itemId)
                .map(stock -> stock.canAllocate(requiredQuantity))
                .orElse(false);
    }

    /**
     * Get available quantity in a warehouse
     */
    public Long getAvailableQuantity(Long warehouseId, Long itemId) {
        return stockItemRepository.findByWarehouseAndItem(warehouseId, itemId)
                .map(StockItem::getAvailable)
                .orElse(0L);
    }
}
