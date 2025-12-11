package com.ucop.controller;

import com.ucop.entity.Shipment;
import com.ucop.entity.Appointment;
import com.ucop.service.ShipmentService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ShipmentController {

    private final ShipmentService shipmentService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    /** Create shipment */
    public Shipment createShipment(Long orderId, String carrier, Long warehouseId) {
        return shipmentService.createShipment(orderId, carrier, warehouseId);
    }

    /** Get shipment by ID */
    public Optional<Shipment> getShipment(Long shipmentId) {
        return shipmentService.getShipment(shipmentId);
    }

    /** Track shipment */
    public Optional<Shipment> trackShipment(String trackingNumber) {
        return shipmentService.trackShipment(trackingNumber);
    }

    /** Get shipments by order */
    public List<Shipment> getShipmentsByOrder(Long orderId) {
        return shipmentService.getShipmentsByOrder(orderId);
    }

    /** Update shipment status */
    public void updateShipmentStatus(Long shipmentId, String status) {
        Shipment.ShipmentStatus shipmentStatus = Shipment.ShipmentStatus.valueOf(status);
        shipmentService.updateShipmentStatus(shipmentId, shipmentStatus);
    }

    /** Create appointment */
    public Appointment createAppointment(Long orderId, String scheduledTime, String location) {
        LocalDateTime time = LocalDateTime.parse(scheduledTime, formatter);
        return shipmentService.createAppointment(orderId, time, location);
    }

    /** Get appointment by ID */
    public Optional<Appointment> getAppointment(Long appointmentId) {
        return shipmentService.getAppointment(appointmentId);
    }

    /** Get appointments by order */
    public List<Appointment> getAppointmentsByOrder(Long orderId) {
        return shipmentService.getAppointmentsByOrder(orderId);
    }

    /** Update appointment status */
    public void updateAppointmentStatus(Long appointmentId, String status) {
        Appointment.AppointmentStatus appointmentStatus =
                Appointment.AppointmentStatus.valueOf(status);

        shipmentService.updateAppointmentStatus(appointmentId, appointmentStatus);
    }
}
