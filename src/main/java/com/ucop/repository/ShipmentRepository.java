package com.ucop.repository;

import com.ucop.entity.Shipment;
import java.util.Optional;
import java.util.List;

public interface ShipmentRepository {
    Shipment save(Shipment shipment);
    Optional<Shipment> findById(Long id);
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
    List<Shipment> findByOrderId(Long orderId);
    List<Shipment> findByStatus(String status);
    List<Shipment> findAll();
    void delete(Long id);
    void update(Shipment shipment);
}
