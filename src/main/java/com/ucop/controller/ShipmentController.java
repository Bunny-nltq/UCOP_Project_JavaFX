package com.ucop.controller;

import java.util.List;
import java.util.Optional;

import com.ucop.entity.Shipment;
import com.ucop.service.ShipmentService;

/**
 * Controller for Shipment Management
 * (✅ fixed: remove Appointment features to match ShipmentService without appointment)
 */
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        if (shipmentService == null) {
            throw new IllegalArgumentException("ShipmentService is null");
        }
        this.shipmentService = shipmentService;
    }

    /**
     * Create shipment
     */
    public Shipment createShipment(Long orderId, String carrier, Long warehouseId) {
        return shipmentService.createShipment(orderId, carrier, warehouseId);
    }

    /**
     * Get shipment by ID
     */
    public Optional<Shipment> getShipment(Long shipmentId) {
        return shipmentService.getShipment(shipmentId);
    }

    /**
     * Track shipment by tracking number
     */
    public Optional<Shipment> trackShipment(String trackingNumber) {
        return shipmentService.trackShipment(trackingNumber);
    }

    /**
     * Get shipments by order
     */
    public List<Shipment> getShipmentsByOrder(Long orderId) {
        return shipmentService.getShipmentsByOrder(orderId);
    }

    /**
     * Update shipment status
     */
    public void updateShipmentStatus(Long shipmentId, String status) {
        Shipment.ShipmentStatus shipmentStatus;
        try {
            shipmentStatus = Shipment.ShipmentStatus.valueOf(status);
        } catch (Exception e) {
            throw new RuntimeException("Invalid shipment status: " + status, e);
        }
        shipmentService.updateShipmentStatus(shipmentId, shipmentStatus);
    }
}
