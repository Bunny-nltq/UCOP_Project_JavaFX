package com.ucop.service;

import com.ucop.entity.Order;
import com.ucop.entity.Shipment;
import com.ucop.repository.OrderRepository;
import com.ucop.repository.ShipmentRepository;
import com.ucop.util.OrderNumberGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;

    public ShipmentService(ShipmentRepository shipmentRepository,
                           OrderRepository orderRepository) {
        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
    }

    public Shipment createShipment(Long orderId, String carrier, Long warehouseId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        Shipment shipment = new Shipment(order, OrderNumberGenerator.generateTrackingNumber(), carrier);
        shipment.setWarehouseId(warehouseId);
        shipment.setStatus(Shipment.ShipmentStatus.PENDING);
        shipment.setExpectedDeliveryDate(LocalDateTime.now().plusDays(3));
        shipment.setUpdatedAt(LocalDateTime.now());

        // add vào order nếu bạn có mapping order.addShipment()
        // order.addShipment(shipment);
        // orderRepository.update(order);

        return shipmentRepository.save(shipment);
    }

    public void updateShipmentStatus(Long shipmentId, Shipment.ShipmentStatus newStatus) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));

        shipment.setStatus(newStatus);
        shipment.setUpdatedAt(LocalDateTime.now());

        if (newStatus == Shipment.ShipmentStatus.DELIVERED) {
            shipment.setActualDeliveryDate(LocalDateTime.now());
        }
        shipmentRepository.update(shipment);
    }

    public Optional<Shipment> getShipment(Long shipmentId) {
        return shipmentRepository.findById(shipmentId);
    }

    public List<Shipment> getShipmentsByOrder(Long orderId) {
        return shipmentRepository.findByOrderId(orderId);
    }

    public List<Shipment> getShipmentsByStatus(Shipment.ShipmentStatus status) {
        return shipmentRepository.findByStatus(status.name());
    }

    public Optional<Shipment> trackShipment(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber);
    }
}
