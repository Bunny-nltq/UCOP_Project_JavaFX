package com.ucop.service;

import com.ucop.entity.*;
import com.ucop.dao.CartItemDAO;
import com.ucop.repository.*;
import com.ucop.util.OrderNumberGenerator;
import com.ucop.util.PaymentCalculator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for managing shopping carts and orders
 */
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

    /**
     * Create or get cart for customer
     */
    public Cart getOrCreateCart(Long accountId) {
        Optional<Cart> existingCart = cartRepository.findByAccountId(accountId);
        if (existingCart.isPresent()) {
            return existingCart.get();
        }
        Cart newCart = new Cart(accountId);
        return cartRepository.save(newCart);
    }

    /**
     * Add item to cart
     */
    public void addToCart(Long cartId, CartItemDAO itemDTO) {
        Optional<Cart> cartOpt = cartRepository.findById(cartId);
        if (cartOpt.isEmpty()) {
            throw new IllegalArgumentException("Cart not found");
        }

        Cart cart = cartOpt.get();
        
        // Check if item already exists in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getItemId().equals(itemDTO.getItemId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + itemDTO.getQuantity());
            item.setUpdatedAt(LocalDateTime.now());
        } else {
            // Add new item
            CartItem newItem = new CartItem(
                    itemDTO.getItemId(),
                    itemDTO.getQuantity(),
                    itemDTO.getUnitPrice()
            );
            cart.addItem(newItem);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.update(cart);
    }

    /**
     * Remove item from cart
     */
    public void removeFromCart(Long cartId, Long itemId) {
        Optional<Cart> cartOpt = cartRepository.findById(cartId);
        if (cartOpt.isEmpty()) {
            throw new IllegalArgumentException("Cart not found");
        }

        Cart cart = cartOpt.get();
        cart.getItems().removeIf(item -> item.getItemId().equals(itemId));
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.update(cart);
    }

    /**
     * Update cart item quantity
     */
    public void updateCartItemQuantity(Long cartId, Long itemId, Long newQuantity) {
        if (newQuantity <= 0) {
            removeFromCart(cartId, itemId);
            return;
        }

        Optional<Cart> cartOpt = cartRepository.findById(cartId);
        if (cartOpt.isEmpty()) {
            throw new IllegalArgumentException("Cart not found");
        }

        Cart cart = cartOpt.get();
        cart.getItems().stream()
                .filter(item -> item.getItemId().equals(itemId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(newQuantity);
                    item.setUpdatedAt(LocalDateTime.now());
                });

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.update(cart);
    }

    /**
     * Convert cart to order (Place order)
     * This is transactional - all or nothing
     */
    public Order placeOrder(Long cartId, Order.OrderStatus initialStatus) {
        Optional<Cart> cartOpt = cartRepository.findById(cartId);
        if (cartOpt.isEmpty()) {
            throw new IllegalArgumentException("Cart not found");
        }

        Cart cart = cartOpt.get();
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        // Create new order from cart
        Order order = new Order(cart.getAccountId());
        order.setOrderNumber(OrderNumberGenerator.generateOrderNumber());
        order.setStatus(initialStatus);
        order.setPlacedAt(LocalDateTime.now());
        order.setCreatedAt(LocalDateTime.now());

        // Convert cart items to order items
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
        
        // Save order
        Order savedOrder = orderRepository.save(order);
        
        // Clear cart
        cart.clearCart();
        cartRepository.update(cart);

        return savedOrder;
    }

    /**
     * Reserve stock for order items
     */
    public void reserveStock(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new IllegalArgumentException("Order not found");
        }

        Order order = orderOpt.get();
        for (OrderItem item : order.getItems()) {
            // Try to find and reserve stock from primary warehouse
            List<StockItem> stockItems = stockItemRepository.findByWarehouseId(1L);
            Optional<StockItem> foundStock = stockItems.stream()
                    .filter(si -> si.getItemId().equals(item.getItemId()))
                    .findFirst();

            if (foundStock.isEmpty() || !foundStock.get().canAllocate(item.getQuantity())) {
                throw new IllegalStateException(
                        "Insufficient stock for item: " + item.getItemId()
                );
            }

            StockItem stock = foundStock.get();
            stock.reserve(item.getQuantity());
            stockItemRepository.update(stock);
        }
    }

    /**
     * Cancel order and unreserve stock
     */
    public void cancelOrder(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new IllegalArgumentException("Order not found");
        }

        Order order = orderOpt.get();
        order.setStatus(Order.OrderStatus.CANCELED);
        order.setUpdatedAt(LocalDateTime.now());

        // Unreserve stock
        for (OrderItem item : order.getItems()) {
            List<StockItem> stockItems = stockItemRepository.findByWarehouseId(1L);
            stockItems.stream()
                    .filter(si -> si.getItemId().equals(item.getItemId()))
                    .findFirst()
                    .ifPresent(stock -> {
                        stock.unreserve(item.getQuantity());
                        stockItemRepository.update(stock);
                    });
        }

        orderRepository.update(order);
    }

    /**
     * Update order status
     */
    public void updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new IllegalArgumentException("Order not found");
        }

        Order order = orderOpt.get();
        
        // When order is PAID, deduct from inventory
        if (newStatus == Order.OrderStatus.PAID && order.getStatus() != Order.OrderStatus.PAID) {
            for (OrderItem item : order.getItems()) {
                List<StockItem> stockItems = stockItemRepository.findByWarehouseId(1L);
                stockItems.stream()
                        .filter(si -> si.getItemId().equals(item.getItemId()))
                        .findFirst()
                        .ifPresent(stock -> {
                            stock.deduct(item.getQuantity());
                            stockItemRepository.update(stock);
                        });
            }
            order.setPaidAt(LocalDateTime.now());
        }

        // Update shipment status if applicable
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
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.update(order);
    }

    /**
     * Get order by ID
     */
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    /**
     * Get orders by account ID
     */
    public List<Order> getOrdersByAccountId(Long accountId) {
        return orderRepository.findByAccountId(accountId);
    }

    /**
     * Get all orders
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Get orders by status
     */
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status.name());
    }
}
