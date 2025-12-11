package com.ucop.controller;

import com.ucop.entity.Order;
import com.ucop.service.OrderService;

import java.util.List;
import java.util.Optional;

public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** Get order by ID */
    public Optional<Order> getOrder(Long orderId) {
        return orderService.getOrderById(orderId);
    }

    /** Get orders by account ID */
    public List<Order> getOrdersByAccount(Long accountId) {
        return orderService.getOrdersByAccountId(accountId);
    }

    /** Get all orders */
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    /** Get orders by status */
    public List<Order> getOrdersByStatus(String status) {
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status);
        return orderService.getOrdersByStatus(orderStatus);
    }

    /** Reserve stock for an order */
    public void reserveStock(Long orderId) {
        orderService.reserveStock(orderId);
    }

    /** Update order status */
    public void updateOrderStatus(Long orderId, String status) {
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status);
        orderService.updateOrderStatus(orderId, orderStatus);
    }

    /** Cancel order */
    public void cancelOrder(Long orderId) {
        orderService.cancelOrder(orderId);
    }
}
