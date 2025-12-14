package com.ucop.repository;

import java.util.List;
import java.util.Optional;

import com.ucop.entity.Refund;

public interface RefundRepository {
    Refund save(Refund refund);
    Optional<Refund> findById(Long id);
    List<Refund> findByPaymentId(Long paymentId);
    List<Refund> findByStatus(String status);
    List<Refund> findAll();
    void delete(Long id);
    void update(Refund refund);
}
