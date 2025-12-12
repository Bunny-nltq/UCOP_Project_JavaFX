package com.ucop.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.ucop.entity.Appointment;
import com.ucop.entity.Shipment;
import com.ucop.service.ShipmentService;

/**
 * Controller for Shipment & Appointment Management
 */
public class ShipmentController {

    private ShipmentService shipmentService;
    
    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * Create shipment
     */
    public Shipment createShipment(Long orderId, String carrier, Long warehouseId) {
        try {
            return shipmentService.createShipment(orderId, carrier, warehouseId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get shipment by ID
     */
    public Optional<Shipment> getShipment(Long shipmentId) {
        try {
            return shipmentService.getShipment(shipmentId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Track shipment
     */
    public Optional<Shipment> trackShipment(String trackingNumber) {
        try {
            return shipmentService.trackShipment(trackingNumber);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get shipments by order
     */
    public List<Shipment> getShipmentsByOrder(Long orderId) {
        try {
            return shipmentService.getShipmentsByOrder(orderId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update shipment status
     */
    public void updateShipmentStatus(Long shipmentId, String status) {
        try {
            Shipment.ShipmentStatus shipmentStatus = Shipment.ShipmentStatus.valueOf(status);
            shipmentService.updateShipmentStatus(shipmentId, shipmentStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid shipment status", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create appointment
     */
    public Appointment createAppointment(Long orderId, String scheduledTime, String location) {
        try {
            LocalDateTime time = LocalDateTime.parse(scheduledTime, formatter);
            return shipmentService.createAppointment(orderId, time, location);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get appointment
     */
    public Optional<Appointment> getAppointment(Long appointmentId) {
        try {
            return shipmentService.getAppointment(appointmentId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get appointments by order
     */
    public List<Appointment> getAppointmentsByOrder(Long orderId) {
        try {
            return shipmentService.getAppointmentsByOrder(orderId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update appointment status
     */
    public void updateAppointmentStatus(Long appointmentId, String status) {
        try {
            Appointment.AppointmentStatus appointmentStatus = Appointment.AppointmentStatus.valueOf(status);
            shipmentService.updateAppointmentStatus(appointmentId, appointmentStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid appointment status", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
