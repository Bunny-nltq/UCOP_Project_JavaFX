package com.ucop.service;

import com.ucop.entity.*;
import com.ucop.dao.CartItemDAO;
import com.ucop.repository.*;
import com.ucop.util.OrderNumberGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final StockItemRepository stockItemRepository;
    private final PaymentRepository paymentRepository;
    private final ShipmentRepository shipmentRepository;
    private final AppointmentRepository appointmentRepository;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        StockItemRepository stockItemRepository,
                        PaymentRepository paymentRepository,
                        ShipmentRepository shipmentRepository,
                        AppointmentRepository appointmentRepository) {

        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.stockItemRepository = stockItemRepository;
        this.paymentRepository = paymentRepository;
        this.shipmentRepository = shipmentRepository;
        this.appointmentRepository = appointmentRepository;
    }

    // Create or get cart
    public Cart getOrCreateCart(Long accountId) {
        return cartRepository.findByAccountId(accountId)
                .orElseGet(() -> cartRepository.save(new Cart(accountId)));
    }

    // Add to cart
    public void addToCart(Long cartId, CartItemDAO itemDTO) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getItemId().equals(itemDTO.getItemId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + itemDTO.getQuantity().intValue());
        } else {
            CartItem newItem = new CartItem(
                    itemDTO.getItemId(),
                    itemDTO.getQuantity().intValue(),
                    itemDTO.getUnitPrice()
            );
            cart.addItem(newItem);
        }

        cartRepository.update(cart);
    }

    // Remove from cart
    public void removeFromCart(Long cartId, Long itemId) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        cart.getItems().removeIf(item -> item.getItemId().equals(itemId));

        cartRepository.update(cart);
    }

    // Update quantity
    public void updateCartItemQuantity(Long cartId, Long itemId, Long newQuantity) {

        if (newQuantity <= 0) {
            removeFromCart(cartId, itemId);
            return;
        }

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        cart.getItems().stream()
                .filter(item -> item.getItemId().equals(itemId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(newQuantity.intValue()));

        cartRepository.update(cart);
    }

    // Place order
    public Order placeOrder(Long cartId, Order.OrderStatus initialStatus) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        Order order = new Order(cart.getAccountId());
        order.setOrderNumber(OrderNumberGenerator.generateOrderNumber());
        order.setStatus(initialStatus);
        order.setPlacedAt(LocalDateTime.now());

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem(
                    cartItem.getItemId(),
                    cartItem.getQuantity(),
                    cartItem.getUnitPrice()
            );
            order.addItem(orderItem);
            subtotal = subtotal.add(orderItem.getSubtotal());
        }

        order.setSubtotal(subtotal);

        Order savedOrder = orderRepository.save(order);

        cart.clearCart();
        cartRepository.update(cart);

        return savedOrder;
    }

    // Reserve stock
    public void reserveStock(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        for (OrderItem item : order.getItems()) {

            List<StockItem> stockItems = stockItemRepository.findByWarehouseId(1L);

            StockItem stock = stockItems.stream()
                    .filter(si -> si.getItemId().equals(item.getItemId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No stock for item " + item.getItemId()));

            if (!stock.canAllocate(item.getQuantity().longValue())) {
                throw new IllegalStateException("Not enough stock for item " + item.getItemId());
            }

            stock.reserve(item.getQuantity().longValue());
            stockItemRepository.update(stock);
        }
    }

    // Cancel order
    public void cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.setStatus(Order.OrderStatus.CANCELED);

        for (OrderItem item : order.getItems()) {
            List<StockItem> stockItems = stockItemRepository.findByWarehouseId(1L);

            stockItems.stream()
                    .filter(si -> si.getItemId().equals(item.getItemId()))
                    .findFirst()
                    .ifPresent(stock -> {
                        stock.unreserve(item.getQuantity().longValue());
                        stockItemRepository.update(stock);
                    });
        }

        orderRepository.update(order);
    }

    // Update order status
    public void updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (newStatus == Order.OrderStatus.PAID &&
                order.getStatus() != Order.OrderStatus.PAID) {

            for (OrderItem item : order.getItems()) {

                List<StockItem> stockItems = stockItemRepository.findByWarehouseId(1L);

                stockItems.stream()
                        .filter(si -> si.getItemId().equals(item.getItemId()))
                        .findFirst()
                        .ifPresent(stock -> {
                            stock.deduct(item.getQuantity().longValue());
                            stockItemRepository.update(stock);
                        });
            }

            order.setPaidAt(LocalDateTime.now());
        }

        if (newStatus == Order.OrderStatus.SHIPPED) {
            order.setShippedAt(LocalDateTime.now());
            order.getShipments().forEach(shipment -> {
                shipment.setStatus(Shipment.ShipmentStatus.SHIPPED);
                shipmentRepository.update(shipment);
            });
        }

        if (newStatus == Order.OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
            order.getShipments().forEach(shipment -> {
                shipment.setStatus(Shipment.ShipmentStatus.DELIVERED);
                shipment.setActualDeliveryDate(LocalDateTime.now());
                shipmentRepository.update(shipment);
            });
        }

        order.setStatus(newStatus);
        orderRepository.update(order);
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public List<Order> getOrdersByAccountId(Long accountId) {
        return orderRepository.findByAccountId(accountId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status.name());
    }
}
