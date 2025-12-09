package com.ucop.entity;



import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

import java.util.HashSet;

import java.util.Set;



@Entity

@Table(name = "warehouses")

public class Warehouse {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;



    @NotBlank(message = "Warehouse name cannot be blank")

    @Column(nullable = false, length = 100)

    private String name;



    @Column(length = 255)

    private String address;



    @Column(length = 20)

    private String phone;



    @Column(name = "is_active", nullable = false)

    private Boolean isActive = true;



    @Column(name = "created_at", nullable = false, updatable = false)

    private LocalDateTime createdAt = LocalDateTime.now();



    @Column(name = "updated_at")

    private LocalDateTime updatedAt = LocalDateTime.now();



    @Column(name = "created_by", length = 100)

    private String createdBy;



    @Column(name = "updated_by", length = 100)

    private String updatedBy;



    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)

    private Set<StockItem> stockItems = new HashSet<>();



    // Constructors

    public Warehouse() {

    }



    public Warehouse(String name, String address, String phone) {

        this.name = name;

        this.address = address;

        this.phone = phone;

    }



    // Getters & Setters

    public Long getId() {

        return id;

    }



    public void setId(Long id) {

        this.id = id;

    }



    public String getName() {

        return name;

    }



    public void setName(String name) {

        this.name = name;

    }



    public String getAddress() {

        return address;

    }



    public void setAddress(String address) {

        this.address = address;

    }



    public String getPhone() {

        return phone;

    }



    public void setPhone(String phone) {

        this.phone = phone;

    }



    public Boolean getIsActive() {

        return isActive;

    }



    public void setIsActive(Boolean active) {

        isActive = active;

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



    public Set<StockItem> getStockItems() {

        return stockItems;

    }



    public void setStockItems(Set<StockItem> stockItems) {

        this.stockItems = stockItems;

    }

}




