package com.ucop.service;

import com.ucop.entity.*;
import com.ucop.repository.*;
import com.ucop.util.OrderNumberGenerator;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for shipment and appointment management
 */
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;
    private final AppointmentRepository appointmentRepository;
    private final OrderRepository orderRepository;

    public ShipmentService(ShipmentRepository shipmentRepository,
                          AppointmentRepository appointmentRepository,
                          OrderRepository orderRepository) {
        this.shipmentRepository = shipmentRepository;
        this.appointmentRepository = appointmentRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Create shipment for physical order
     */
    public Shipment createShipment(Long orderId, String carrier, Long warehouseId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new IllegalArgumentException("Order not found");
        }

        Order order = orderOpt.get();
        Shipment shipment = new Shipment(order, OrderNumberGenerator.generateTrackingNumber(), carrier);
        shipment.setWarehouseId(warehouseId);
        shipment.setStatus(Shipment.ShipmentStatus.PENDING);
        shipment.setExpectedDeliveryDate(LocalDateTime.now().plusDays(3));
        shipment.setCreatedAt(LocalDateTime.now());

        order.addShipment(shipment);
        orderRepository.update(order);

        return shipmentRepository.save(shipment);
    }

    /**
     * Update shipment status
     */
    public void updateShipmentStatus(Long shipmentId, Shipment.ShipmentStatus newStatus) {
        Optional<Shipment> shipmentOpt = shipmentRepository.findById(shipmentId);
        if (shipmentOpt.isEmpty()) {
            throw new IllegalArgumentException("Shipment not found");
        }

        Shipment shipment = shipmentOpt.get();
        shipment.setStatus(newStatus);
        shipment.setUpdatedAt(LocalDateTime.now());

        if (newStatus == Shipment.ShipmentStatus.DELIVERED) {
            shipment.setActualDeliveryDate(LocalDateTime.now());
        }

        shipmentRepository.update(shipment);
    }

    /**
     * Get shipment by ID
     */
    public Optional<Shipment> getShipment(Long shipmentId) {
        return shipmentRepository.findById(shipmentId);
    }

    /**
     * Get shipments for order
     */
    public List<Shipment> getShipmentsByOrder(Long orderId) {
        return shipmentRepository.findByOrderId(orderId);
    }

    /**
     * Get shipments by status
     */
    public List<Shipment> getShipmentsByStatus(Shipment.ShipmentStatus status) {
        return shipmentRepository.findByStatus(status.name());
    }

    /**
     * Track shipment by tracking number
     */
    public Optional<Shipment> trackShipment(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber);
    }

    /**
     * Create appointment for service order
     */
    public Appointment createAppointment(Long orderId, LocalDateTime scheduledTime, String location) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new IllegalArgumentException("Order not found");
        }

        Order order = orderOpt.get();
        Appointment appointment = new Appointment(
                order,
                OrderNumberGenerator.generateAppointmentNumber(),
                scheduledTime
        );
        appointment.setLocation(location);
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);
        appointment.setCreatedAt(LocalDateTime.now());

        order.addAppointment(appointment);
        orderRepository.update(order);

        return appointmentRepository.save(appointment);
    }

    /**
     * Update appointment status
     */
    public void updateAppointmentStatus(Long appointmentId, Appointment.AppointmentStatus newStatus) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            throw new IllegalArgumentException("Appointment not found");
        }

        Appointment appointment = appointmentOpt.get();
        appointment.setStatus(newStatus);
        appointment.setUpdatedAt(LocalDateTime.now());

        if (newStatus == Appointment.AppointmentStatus.COMPLETED) {
            appointment.setActualTime(LocalDateTime.now());
        }

        appointmentRepository.update(appointment);
    }

    /**
     * Get appointment by ID
     */
    public Optional<Appointment> getAppointment(Long appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }

    /**
     * Get appointments for order
     */
    public List<Appointment> getAppointmentsByOrder(Long orderId) {
        return appointmentRepository.findByOrderId(orderId);
    }

    /**
     * Get appointments by status
     */
    public List<Appointment> getAppointmentsByStatus(Appointment.AppointmentStatus status) {
        return appointmentRepository.findByStatus(status.name());
    }

    /**
     * Reschedule appointment
     */
    public void rescheduleAppointment(Long appointmentId, LocalDateTime newTime) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            throw new IllegalArgumentException("Appointment not found");
        }

        Appointment appointment = appointmentOpt.get();
        appointment.setScheduledTime(newTime);
        appointment.setStatus(Appointment.AppointmentStatus.RESCHEDULED);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.update(appointment);
    }
}
