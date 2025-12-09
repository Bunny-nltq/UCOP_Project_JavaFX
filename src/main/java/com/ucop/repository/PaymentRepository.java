package com.ucop.repository;

import com.ucop.entity.Payment;
import java.util.Optional;
import java.util.List;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByOrderId(Long orderId);
    List<Payment> findByStatus(String status);
    List<Payment> findAll();
    void delete(Long id);
    void update(Payment payment);
}
