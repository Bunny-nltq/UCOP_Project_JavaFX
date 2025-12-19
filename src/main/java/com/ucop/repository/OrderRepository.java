package com.ucop.repository;

import com.ucop.entity.Order;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long id);

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByAccountId(Long accountId);

    List<Order> findByStatus(String status);

    List<Order> findAll();

    void update(Order order);

    void delete(Long id);

    // ✅ ADD DÒNG NÀY
    Optional<Order> findByIdWithItems(Long id);
}
