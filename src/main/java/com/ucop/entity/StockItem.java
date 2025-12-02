package com.ucop.entity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_items", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"warehouse_id", "item_id"})
})
public class StockItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @NotNull
    @Column(name = "item_id", nullable = false)
    private Long itemId;  // Reference to Item (from Catalog module)

    @NotNull
    @Min(value = 0, message = "On hand quantity cannot be negative")
    @Column(name = "on_hand", nullable = false)
    private Long onHand = 0L;

    @NotNull
    @Min(value = 0, message = "Reserved quantity cannot be negative")
    @Column(name = "reserved", nullable = false)
    private Long reserved = 0L;

    @NotNull
    @Min(value = 0, message = "Low stock threshold must be non-negative")
    @Column(name = "low_stock_threshold", nullable = false)
    private Long lowStockThreshold = 10L;

    @Column(name = "is_low_stock")
    private Boolean isLowStock = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    // Constructors
    public StockItem() {
    }

    public StockItem(Warehouse warehouse, Long itemId, Long onHand, Long lowStockThreshold) {
        this.warehouse = warehouse;
        this.itemId = itemId;
        this.onHand = onHand;
        this.lowStockThreshold = lowStockThreshold;
        updateLowStockStatus();
    }

    // Business methods
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
            throw new IllegalArgumentException("Insufficient stock to reserve. Available: " + getAvailable() + ", Requested: " + quantity);
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
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getOnHand() {
        return onHand;
    }

    public void setOnHand(Long onHand) {
        this.onHand = onHand;
    }

    public Long getReserved() {
        return reserved;
    }

    public void setReserved(Long reserved) {
        this.reserved = reserved;
    }

    public Long getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(Long lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public Boolean getIsLowStock() {
        return isLowStock;
    }

    public void setIsLowStock(Boolean lowStock) {
        isLowStock = lowStock;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
