package com.ucop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // DB: order_id (NOT NULL)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // DB: tracking_number varchar(100) NULL
    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    // DB: carrier varchar(100) NULL
    @Column(name = "carrier", length = 100)
    private String carrier;

    // DB: status enum(...) NOT NULL
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ShipmentStatus status = ShipmentStatus.PENDING;

    // DB: expected_delivery_date datetime NULL
    @Column(name = "expected_delivery_date")
    private LocalDateTime expectedDeliveryDate;

    // DB: actual_delivery_date datetime NULL
    @Column(name = "actual_delivery_date")
    private LocalDateTime actualDeliveryDate;

    // DB: warehouse_id bigint NULL
    @Column(name = "warehouse_id")
    private Long warehouseId;

    // DB: created_by varchar(100) NULL
    @Column(name = "created_by", length = 100)
    private String createdBy;

    // DB: notes varchar(500) NULL  (❌ không phải TEXT)
    @Column(name = "notes", length = 500)
    private String notes;

    // DB: updated_at datetime(6) NULL
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // DB: updated_by varchar(100) NULL
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    public Shipment() {}

    public Shipment(Order order, String trackingNumber, String carrier) {
        this.order = order;
        this.trackingNumber = trackingNumber;
        this.carrier = carrier;
        this.status = ShipmentStatus.PENDING;
    }

    // Optional: tự set updatedAt nếu DB không tự update
    @PrePersist
    protected void onCreate() {
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }

    public ShipmentStatus getStatus() { return status; }
    public void setStatus(ShipmentStatus status) { this.status = status; }

    public LocalDateTime getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public void setExpectedDeliveryDate(LocalDateTime expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }

    public LocalDateTime getActualDeliveryDate() { return actualDeliveryDate; }
    public void setActualDeliveryDate(LocalDateTime actualDeliveryDate) { this.actualDeliveryDate = actualDeliveryDate; }

    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    // ===== Enum =====
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
