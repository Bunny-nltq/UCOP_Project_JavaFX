package com.ucop.controller;

import java.util.List;
import java.util.Optional;

import com.ucop.entity.Order;
import com.ucop.service.OrderService;

/**
 * Controller for Order operations
 */
public class OrderController {

    private OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Get order by ID
     */
    public Optional<Order> getOrder(Long orderId) {
        try {
            return orderService.getOrderById(orderId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get orders by account ID
     */
    public List<Order> getOrdersByAccount(Long accountId) {
        try {
            return orderService.getOrdersByAccountId(accountId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get all orders
     */
    public List<Order> getAllOrders() {
        try {
            return orderService.getAllOrders();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get orders by status
     */
    public List<Order> getOrdersByStatus(String status) {
        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status);
            return orderService.getOrdersByStatus(orderStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reserve stock for order
     */
    public void reserveStock(Long orderId) {
        try {
            orderService.reserveStock(orderId, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update order status
     */
    public void updateOrderStatus(Long orderId, String status) {
        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status);
            orderService.updateOrderStatus(orderId, orderStatus, null);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Cancel order
     */
    public void cancelOrder(Long orderId) {
        try {
            orderService.cancelOrder(orderId, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
