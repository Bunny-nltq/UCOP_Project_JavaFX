package com.ucop.repository;

import java.util.List;
import java.util.Optional;

import com.ucop.entity.Payment;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByOrderId(Long orderId);
    List<Payment> findByStatus(String status);
    List<Payment> findAll();
    void delete(Long id);
    Payment update(Payment payment);
}
