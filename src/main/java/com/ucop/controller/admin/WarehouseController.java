package com.ucop.controller.admin;

import com.ucop.entity.Warehouse;
import com.ucop.entity.StockItem;
import com.ucop.service.WarehouseService;

import java.util.List;
import java.util.Optional;

public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    public Warehouse createWarehouse(String name, String address, String phone) {
        return warehouseService.createWarehouse(name, address, phone);
    }

    public List<Warehouse> getAllWarehouses() {
        return warehouseService.getAllWarehouses();
    }

    public List<Warehouse> getActiveWarehouses() {
        return warehouseService.getActiveWarehouses();
    }

    public Optional<Warehouse> getWarehouse(Long warehouseId) {
        return warehouseService.getWarehouse(warehouseId);
    }

    public List<StockItem> getLowStockItems() {
        return warehouseService.getLowStockItems();
    }

    public List<StockItem> getStockItems(Long warehouseId) {
        return warehouseService.getStockItemsByWarehouse(warehouseId);
    }

    public StockItem addStockItem(Long warehouseId, Long itemId, Long onHand, Long lowStockThreshold) {
        return warehouseService.addStockItem(warehouseId, itemId, onHand, lowStockThreshold);
    }

    public void updateStockQuantity(Long stockItemId, Long newQty) {
        warehouseService.updateStockQuantity(stockItemId, newQty);
    }

    public boolean checkAvailability(Long warehouseId, Long itemId, Long requiredQuantity) {
        return warehouseService.isStockAvailable(warehouseId, itemId, requiredQuantity);
    }

    public Long getAvailableQty(Long warehouseId, Long itemId) {
        return warehouseService.getAvailableQuantity(warehouseId, itemId);
    }
}
