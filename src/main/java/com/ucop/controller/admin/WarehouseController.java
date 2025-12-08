package com.ucop.controller.admin;

import com.ucop.entity.Warehouse;
import com.ucop.entity.StockItem;
import com.ucop.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST Controller for Warehouse & Stock Management
 */
@RestController
@RequestMapping("/api/warehouses")
@CrossOrigin(origins = "*")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    /**
     * Create warehouse
     */
    @PostMapping("")
    public ResponseEntity<?> createWarehouse(@RequestParam String name,
                                            @RequestParam String address,
                                            @RequestParam String phone) {
        try {
            Warehouse warehouse = warehouseService.createWarehouse(name, address, phone);
            return ResponseEntity.status(HttpStatus.CREATED).body(warehouse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get all warehouses
     */
    @GetMapping("")
    public ResponseEntity<?> getAllWarehouses() {
        try {
            List<Warehouse> warehouses = warehouseService.getAllWarehouses();
            return ResponseEntity.ok(warehouses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get active warehouses
     */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveWarehouses() {
        try {
            List<Warehouse> warehouses = warehouseService.getActiveWarehouses();
            return ResponseEntity.ok(warehouses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get warehouse by ID
     */
    @GetMapping("/{warehouseId}")
    public ResponseEntity<?> getWarehouse(@PathVariable Long warehouseId) {
        try {
            Optional<Warehouse> warehouse = warehouseService.getWarehouse(warehouseId);
            if (warehouse.isPresent()) {
                return ResponseEntity.ok(warehouse.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Warehouse not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get low stock items
     */
    @GetMapping("/low-stock")
    public ResponseEntity<?> getLowStockItems() {
        try {
            List<StockItem> items = warehouseService.getLowStockItems();
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get stock items for warehouse
     */
    @GetMapping("/{warehouseId}/stock")
    public ResponseEntity<?> getStockItems(@PathVariable Long warehouseId) {
        try {
            List<StockItem> items = warehouseService.getStockItemsByWarehouse(warehouseId);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Add stock item
     */
    @PostMapping("/{warehouseId}/stock")
    public ResponseEntity<?> addStockItem(@PathVariable Long warehouseId,
                                         @RequestParam Long itemId,
                                         @RequestParam Long onHand,
                                         @RequestParam(defaultValue = "10") Long lowStockThreshold) {
        try {
            StockItem item = warehouseService.addStockItem(warehouseId, itemId, onHand, lowStockThreshold);
            return ResponseEntity.status(HttpStatus.CREATED).body(item);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update stock quantity
     */
    @PutMapping("/stock/{stockItemId}/quantity")
    public ResponseEntity<?> updateStockQuantity(@PathVariable Long stockItemId,
                                                @RequestParam Long newQuantity) {
        try {
            warehouseService.updateStockQuantity(stockItemId, newQuantity);
            return ResponseEntity.ok(Map.of("message", "Stock quantity updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Check stock availability
     */
    @GetMapping("/{warehouseId}/check-availability")
    public ResponseEntity<?> checkStockAvailability(@PathVariable Long warehouseId,
                                                   @RequestParam Long itemId,
                                                   @RequestParam Long requiredQuantity) {
        try {
            boolean available = warehouseService.isStockAvailable(warehouseId, itemId, requiredQuantity);
            Long availableQty = warehouseService.getAvailableQuantity(warehouseId, itemId);
            return ResponseEntity.ok(Map.of(
                    "available", available,
                    "availableQuantity", availableQty
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
