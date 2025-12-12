package com.ucop.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.ucop.entity.StockItem;
import com.ucop.entity.Warehouse;
import com.ucop.service.WarehouseService;

/**
 * Controller for Warehouse & Stock Management
 */
public class WarehouseController {

    private WarehouseService warehouseService;
    
    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    /**
     * Create warehouse
     */
    public Warehouse createWarehouse(String name, String address, String phone) {
        try {
            return warehouseService.createWarehouse(name, address, phone);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get all warehouses
     */
    public List<Warehouse> getAllWarehouses() {
        try {
            return warehouseService.getAllWarehouses();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get active warehouses
     */
    public List<Warehouse> getActiveWarehouses() {
        try {
            return warehouseService.getActiveWarehouses();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get warehouse by ID
     */
    public Optional<Warehouse> getWarehouse(Long warehouseId) {
        try {
            return warehouseService.getWarehouse(warehouseId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get low stock items
     */
    public List<StockItem> getLowStockItems() {
        try {
            return warehouseService.getLowStockItems();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get stock items for warehouse
     */
    public List<StockItem> getStockItems(Long warehouseId) {
        try {
            return warehouseService.getStockItemsByWarehouse(warehouseId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add stock item
     */
    public StockItem addStockItem(Long warehouseId, Long itemId, Long onHand, Long lowStockThreshold) {
        try {
            return warehouseService.addStockItem(warehouseId, itemId, onHand, lowStockThreshold);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update stock quantity
     */
    public void updateStockQuantity(Long stockItemId, Long newQuantity) {
        try {
            warehouseService.updateStockQuantity(stockItemId, newQuantity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Check stock availability
     */
    public Map<String, Object> checkStockAvailability(Long warehouseId, Long itemId, Long requiredQuantity) {
        try {
            boolean available = warehouseService.isStockAvailable(warehouseId, itemId, requiredQuantity);
            Long availableQty = warehouseService.getAvailableQuantity(warehouseId, itemId);
            return Map.of(
                    "available", available,
                    "availableQuantity", availableQty
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
