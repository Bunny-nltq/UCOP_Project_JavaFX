package com.ucop.repository;

import java.util.List;
import java.util.Optional;

import com.ucop.entity.Order;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByAccountId(Long accountId);
    List<Order> findByStatus(String status);
    List<Order> findAll();
    void delete(Long id);
    void update(Order order);
}
