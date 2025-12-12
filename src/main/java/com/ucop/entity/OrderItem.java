package com.ucop.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class OrderItem {
    private Long id;
    private Order order;
    private Long itemId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal itemDiscount = BigDecimal.ZERO;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public OrderItem() {}

    public OrderItem(Long itemId, Integer quantity, BigDecimal unitPrice) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getTotalAfterDiscount() {
        return getSubtotal().subtract(itemDiscount != null ? itemDiscount : BigDecimal.ZERO);
    }

    // Getters & Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }

    public void setOrder(Order order) { this.order = order; }

    public Long getItemId() { return itemId; }

    public void setItemId(Long itemId) { this.itemId = itemId; }

    public Integer getQuantity() { return quantity; }

    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }

    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getItemDiscount() { return itemDiscount; }

    public void setItemDiscount(BigDecimal itemDiscount) { this.itemDiscount = itemDiscount; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
