package com.ucop.entity;

import java.time.LocalDateTime;

public class StockItem {
    private Long id;
    private Warehouse warehouse;
    private Long itemId;
    private Long onHand = 0L;
    private Long lowStockThreshold = 10L;
    private Long reserved = 0L;
    private Boolean isLowStock = false;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private String createdBy;
    private String updatedBy;

    public StockItem() {}

    public StockItem(Warehouse warehouse, Long itemId, Long onHand, Long lowStockThreshold) {
        this.warehouse = warehouse;
        this.itemId = itemId;
        this.onHand = onHand;
        this.lowStockThreshold = lowStockThreshold;
        updateLowStockStatus();
    }

    // Auto timestamps
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Business logic
    public Long getAvailable() {
        return onHand - reserved;
    }

    public void updateLowStockStatus() {
        this.isLowStock = this.onHand <= this.lowStockThreshold;
    }

    public boolean canAllocate(Long quantity) {
        return getAvailable() >= quantity;
    }

    public void reserve(Long quantity) {
        if (!canAllocate(quantity)) {
            throw new IllegalArgumentException(
                    "Insufficient stock. Available: " + getAvailable() + ", Requested: " + quantity
            );
        }
        this.reserved += quantity;
    }

    public void unreserve(Long quantity) {
        if (this.reserved < quantity) {
            throw new IllegalArgumentException("Cannot unreserve more than reserved quantity");
        }
        this.reserved -= quantity;
    }

    public void deduct(Long quantity) {
        if (this.onHand < quantity) {
            throw new IllegalArgumentException("Insufficient on-hand stock");
        }
        this.onHand -= quantity;
        this.reserved -= quantity;
        updateLowStockStatus();
    }

    public void addStock(Long quantity) {
        this.onHand += quantity;
        updateLowStockStatus();
    }

    // Getters & Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Warehouse getWarehouse() { return warehouse; }

    public void setWarehouse(Warehouse warehouse) { this.warehouse = warehouse; }

    public Long getItemId() { return itemId; }

    public void setItemId(Long itemId) { this.itemId = itemId; }

    public Long getOnHand() { return onHand; }

    public void setOnHand(Long onHand) { this.onHand = onHand; }

    public Long getReserved() { return reserved; }

    public void setReserved(Long reserved) { this.reserved = reserved; }

    public Long getLowStockThreshold() { return lowStockThreshold; }

    public void setLowStockThreshold(Long lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }

    public Boolean getIsLowStock() { return isLowStock; }

    public void setIsLowStock(Boolean isLowStock) { this.isLowStock = isLowStock; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }

    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }

    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
