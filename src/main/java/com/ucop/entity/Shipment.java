package com.ucop.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "shipments")
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @Column(unique = true)
    private String trackingNumber;
    private String carrier;
    private ShipmentStatus status = ShipmentStatus.PENDING;
    private LocalDateTime expectedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private Long warehouseId;
    private String notes;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private String createdBy;
    private String updatedBy;



    // Constructors

    public Shipment() {

    }



    public Shipment(Order order, String trackingNumber, String carrier) {

        this.order = order;

        this.trackingNumber = trackingNumber;

        this.carrier = carrier;

        this.status = ShipmentStatus.PENDING;

    }



    // Getters & Setters

    public Long getId() {

        return id;

    }



    public void setId(Long id) {

        this.id = id;

    }



    public Order getOrder() {

        return order;

    }



    public void setOrder(Order order) {

        this.order = order;

    }



    public String getTrackingNumber() {

        return trackingNumber;

    }



    public void setTrackingNumber(String trackingNumber) {

        this.trackingNumber = trackingNumber;

    }



    public String getCarrier() {

        return carrier;

    }



    public void setCarrier(String carrier) {

        this.carrier = carrier;

    }



    public ShipmentStatus getStatus() {

        return status;

    }



    public void setStatus(ShipmentStatus status) {

        this.status = status;

    }



    public LocalDateTime getExpectedDeliveryDate() {

        return expectedDeliveryDate;

    }



    public void setExpectedDeliveryDate(LocalDateTime expectedDeliveryDate) {

        this.expectedDeliveryDate = expectedDeliveryDate;

    }



    public LocalDateTime getActualDeliveryDate() {

        return actualDeliveryDate;

    }



    public void setActualDeliveryDate(LocalDateTime actualDeliveryDate) {

        this.actualDeliveryDate = actualDeliveryDate;

    }



    public Long getWarehouseId() {

        return warehouseId;

    }



    public void setWarehouseId(Long warehouseId) {

        this.warehouseId = warehouseId;

    }



    public String getNotes() {

        return notes;

    }



    public void setNotes(String notes) {

        this.notes = notes;

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



    // Enum

    public enum ShipmentStatus {

        PENDING,

        PICKED,

        PACKED,

        SHIPPED,

        IN_TRANSIT,

        OUT_FOR_DELIVERY,

        DELIVERED,

        FAILED,

        RETURNED

    }

}




