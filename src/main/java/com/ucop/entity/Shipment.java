package com.ucop.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "shipments")
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    
    @Column(name = "tracking_number", unique = true)
    private String trackingNumber;
    
    @Column
    private String carrier;
    
    @Enumerated(EnumType.STRING)
    @Column
    private ShipmentStatus status = ShipmentStatus.PENDING;
    
    @Column(name = "expected_delivery_date")
    private LocalDateTime expectedDeliveryDate;
    
    @Column(name = "actual_delivery_date")
    private LocalDateTime actualDeliveryDate;
    
    @Column(name = "warehouse_id")
    private Long warehouseId;
    
    @Column
    private String notes;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_by")
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





