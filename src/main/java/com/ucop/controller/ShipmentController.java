package com.ucop.controller;

import com.ucop.entity.Shipment;
import com.ucop.entity.Appointment;
import com.ucop.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * REST Controller for Shipment & Appointment Management
 */
@RestController
@RequestMapping("/api/shipments")
@CrossOrigin(origins = "*")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * Create shipment
     */
    @PostMapping("")
    public ResponseEntity<?> createShipment(@RequestParam Long orderId,
                                           @RequestParam String carrier,
                                           @RequestParam(defaultValue = "1") Long warehouseId) {
        try {
            Shipment shipment = shipmentService.createShipment(orderId, carrier, warehouseId);
            return ResponseEntity.status(HttpStatus.CREATED).body(shipment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get shipment by ID
     */
    @GetMapping("/{shipmentId}")
    public ResponseEntity<?> getShipment(@PathVariable Long shipmentId) {
        try {
            Optional<Shipment> shipment = shipmentService.getShipment(shipmentId);
            if (shipment.isPresent()) {
                return ResponseEntity.ok(shipment.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Shipment not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Track shipment
     */
    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<?> trackShipment(@PathVariable String trackingNumber) {
        try {
            Optional<Shipment> shipment = shipmentService.trackShipment(trackingNumber);
            if (shipment.isPresent()) {
                return ResponseEntity.ok(shipment.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Shipment not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get shipments by order
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getShipmentsByOrder(@PathVariable Long orderId) {
        try {
            List<Shipment> shipments = shipmentService.getShipmentsByOrder(orderId);
            return ResponseEntity.ok(shipments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update shipment status
     */
    @PutMapping("/{shipmentId}/status")
    public ResponseEntity<?> updateShipmentStatus(@PathVariable Long shipmentId,
                                                 @RequestParam String status) {
        try {
            Shipment.ShipmentStatus shipmentStatus = Shipment.ShipmentStatus.valueOf(status);
            shipmentService.updateShipmentStatus(shipmentId, shipmentStatus);
            return ResponseEntity.ok(Map.of("message", "Shipment status updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid shipment status"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Create appointment
     */
    @PostMapping("/appointments")
    public ResponseEntity<?> createAppointment(@RequestParam Long orderId,
                                              @RequestParam String scheduledTime,
                                              @RequestParam String location) {
        try {
            LocalDateTime time = LocalDateTime.parse(scheduledTime, formatter);
            Appointment appointment = shipmentService.createAppointment(orderId, time, location);
            return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get appointment
     */
    @GetMapping("/appointments/{appointmentId}")
    public ResponseEntity<?> getAppointment(@PathVariable Long appointmentId) {
        try {
            Optional<Appointment> appointment = shipmentService.getAppointment(appointmentId);
            if (appointment.isPresent()) {
                return ResponseEntity.ok(appointment.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Appointment not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get appointments by order
     */
    @GetMapping("/appointments/order/{orderId}")
    public ResponseEntity<?> getAppointmentsByOrder(@PathVariable Long orderId) {
        try {
            List<Appointment> appointments = shipmentService.getAppointmentsByOrder(orderId);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update appointment status
     */
    @PutMapping("/appointments/{appointmentId}/status")
    public ResponseEntity<?> updateAppointmentStatus(@PathVariable Long appointmentId,
                                                    @RequestParam String status) {
        try {
            Appointment.AppointmentStatus appointmentStatus = Appointment.AppointmentStatus.valueOf(status);
            shipmentService.updateAppointmentStatus(appointmentId, appointmentStatus);
            return ResponseEntity.ok(Map.of("message", "Appointment status updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid appointment status"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
