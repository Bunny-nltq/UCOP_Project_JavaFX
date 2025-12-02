package com.ucop.repository;

import com.ucop.entity.Refund;
import java.util.Optional;
import java.util.List;

public interface RefundRepository {
    Refund save(Refund refund);
    Optional<Refund> findById(Long id);
    List<Refund> findByPaymentId(Long paymentId);
    List<Refund> findByStatus(String status);
    List<Refund> findAll();
    void delete(Long id);
    void update(Refund refund);
}
