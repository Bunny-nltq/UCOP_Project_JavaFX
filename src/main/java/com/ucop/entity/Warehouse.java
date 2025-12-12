package com.ucop.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class Warehouse {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private Boolean isActive = true;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private String createdBy;
    private String updatedBy;
    private Set<StockItem> stockItems = new HashSet<>();


    // Constructors
    public Warehouse() {}

    public Warehouse(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
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

    // Getters & Setters

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public Boolean getIsActive() { return isActive; }

    public void setIsActive(Boolean active) { this.isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }

    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }

    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public Set<StockItem> getStockItems() { return stockItems; }

    public void setStockItems(Set<StockItem> stockItems) { this.stockItems = stockItems; }
}
