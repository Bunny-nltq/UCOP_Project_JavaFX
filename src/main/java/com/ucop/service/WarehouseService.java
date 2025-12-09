package com.ucop.service;

import com.ucop.entity.*;
import com.ucop.repository.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for warehouse and stock management
 */
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
        warehouse.setCreatedAt(LocalDateTime.now());
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
     * Get active warehouses
     */
    public List<Warehouse> getActiveWarehouses() {
        return warehouseRepository.findAllActive();
    }

    /**
     * Update warehouse
     */
    public void updateWarehouse(Long warehouseId, String name, String address, String phone) {
        Optional<Warehouse> warehouseOpt = warehouseRepository.findById(warehouseId);
        if (warehouseOpt.isEmpty()) {
            throw new IllegalArgumentException("Warehouse not found");
        }

        Warehouse warehouse = warehouseOpt.get();
        warehouse.setName(name);
        warehouse.setAddress(address);
        warehouse.setPhone(phone);
        warehouse.setUpdatedAt(LocalDateTime.now());
        warehouseRepository.update(warehouse);
    }

    /**
     * Deactivate warehouse
     */
    public void deactivateWarehouse(Long warehouseId) {
        Optional<Warehouse> warehouseOpt = warehouseRepository.findById(warehouseId);
        if (warehouseOpt.isEmpty()) {
            throw new IllegalArgumentException("Warehouse not found");
        }

        Warehouse warehouse = warehouseOpt.get();
        warehouse.setIsActive(false);
        warehouse.setUpdatedAt(LocalDateTime.now());
        warehouseRepository.update(warehouse);
    }

    /**
     * Add stock item to warehouse
     */
    public StockItem addStockItem(Long warehouseId, Long itemId, Long onHand, Long lowStockThreshold) {
        Optional<Warehouse> warehouseOpt = warehouseRepository.findById(warehouseId);
        if (warehouseOpt.isEmpty()) {
            throw new IllegalArgumentException("Warehouse not found");
        }

        Warehouse warehouse = warehouseOpt.get();
        
        // Check if item already exists
        Optional<StockItem> existing = stockItemRepository.findByWarehouseAndItem(warehouseId, itemId);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Stock item already exists in this warehouse");
        }

        StockItem stockItem = new StockItem(warehouse, itemId, onHand, lowStockThreshold);
        stockItem.setCreatedAt(LocalDateTime.now());
        return stockItemRepository.save(stockItem);
    }

    /**
     * Update stock quantity
     */
    public void updateStockQuantity(Long stockItemId, Long newQuantity) {
        Optional<StockItem> stockOpt = stockItemRepository.findById(stockItemId);
        if (stockOpt.isEmpty()) {
            throw new IllegalArgumentException("Stock item not found");
        }

        StockItem stock = stockOpt.get();
        stock.setOnHand(newQuantity);
        stock.updateLowStockStatus();
        stock.setUpdatedAt(LocalDateTime.now());
        stockItemRepository.update(stock);
    }

    /**
     * Get stock item
     */
    public Optional<StockItem> getStockItem(Long stockItemId) {
        return stockItemRepository.findById(stockItemId);
    }

    /**
     * Get stock items for warehouse
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
     * Check stock availability
     */
    public boolean isStockAvailable(Long warehouseId, Long itemId, Long requiredQuantity) {
        Optional<StockItem> stockOpt = stockItemRepository.findByWarehouseAndItem(warehouseId, itemId);
        if (stockOpt.isEmpty()) {
            return false;
        }
        return stockOpt.get().canAllocate(requiredQuantity);
    }

    /**
     * Get available quantity
     */
    public Long getAvailableQuantity(Long warehouseId, Long itemId) {
        Optional<StockItem> stockOpt = stockItemRepository.findByWarehouseAndItem(warehouseId, itemId);
        return stockOpt.map(StockItem::getAvailable).orElse(0L);
    }
}
