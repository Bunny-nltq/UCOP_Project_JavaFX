package com.ucop.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.ucop.Dao.CartItemDAO;
import com.ucop.entity.Cart;
import com.ucop.entity.CartItem;
import com.ucop.entity.Order;
import com.ucop.entity.OrderItem;
import com.ucop.entity.StockItem;
import com.ucop.repository.CartRepository;
import com.ucop.repository.OrderRepository;
import com.ucop.repository.ShipmentRepository;
import com.ucop.repository.StockItemRepository;
import com.ucop.util.OrderNumberGenerator;

/**
 * Service for managing shopping carts and orders
 */
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final StockItemRepository stockItemRepository;
    private final ShipmentRepository shipmentRepository;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        StockItemRepository stockItemRepository,
                        ShipmentRepository shipmentRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.stockItemRepository = stockItemRepository;
        this.shipmentRepository = shipmentRepository;
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
        if (cartId == null || cartId <= 0) {
            throw new IllegalArgumentException("Cart ID must be valid");
        }
        
        if (itemDTO == null || itemDTO.getItemId() == null || itemDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("Invalid cart item data");
        }

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
            item.setQuantity(item.getQuantity() + itemDTO.getQuantity().intValue());
            item.setUpdatedAt(LocalDateTime.now());
        } else {
            // Add new item
            CartItem newItem = new CartItem(
                    itemDTO.getItemId(),
                    itemDTO.getQuantity().intValue(),
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
        if (cartId == null || cartId <= 0 || itemId == null || itemId <= 0) {
            throw new IllegalArgumentException("Cart ID and Item ID must be valid");
        }

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
    public void updateCartItemQuantity(Long cartId, Long itemId, int newQuantity) {
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
                    (long) cartItem.getQuantity(),
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
     * Reserve stock for order items with improved performance
     */
    public void reserveStock(Long orderId, Long warehouseId) {
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Order ID must be valid");
        }
        
        if (warehouseId == null || warehouseId <= 0) {
            warehouseId = 1L; // Default to primary warehouse
        }

        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new IllegalArgumentException("Order not found");
        }

        Order order = orderOpt.get();
        
        // Fetch all stock items for warehouse once, not per item
        List<StockItem> warehouseStocks = stockItemRepository.findByWarehouseId(warehouseId);
        
        for (OrderItem item : order.getItems()) {
            Optional<StockItem> foundStock = warehouseStocks.stream()
                    .filter(si -> si.getItemId().equals(item.getItemId()))
                    .findFirst();

            if (foundStock.isEmpty() || !foundStock.get().canAllocate(item.getQuantity())) {
                throw new IllegalStateException(
                        "Insufficient stock for item: " + item.getItemId() + 
                        " (required: " + item.getQuantity() + ")"
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
    public void cancelOrder(Long orderId, Long warehouseId) {
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Order ID must be valid");
        }
        
        if (warehouseId == null || warehouseId <= 0) {
            warehouseId = 1L; // Default to primary warehouse
        }

        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new IllegalArgumentException("Order not found");
        }

        Order order = orderOpt.get();
        
        if (order.getStatus() == Order.OrderStatus.CANCELED) {
            throw new IllegalStateException("Order is already canceled");
        }
        
        order.setStatus(Order.OrderStatus.CANCELED);
        order.setUpdatedAt(LocalDateTime.now());

        // Fetch warehouse stocks once for efficiency
        List<StockItem> warehouseStocks = stockItemRepository.findByWarehouseId(warehouseId);
        
        // Unreserve stock
        for (OrderItem item : order.getItems()) {
            warehouseStocks.stream()
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
     * Update order status with stock management
     */
    public void updateOrderStatus(Long orderId, Order.OrderStatus newStatus, Long warehouseId) {
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Order ID must be valid");
        }
        
        if (newStatus == null) {
            throw new IllegalArgumentException("Order status cannot be null");
        }
        
        if (warehouseId == null || warehouseId <= 0) {
            warehouseId = 1L; // Default to primary warehouse
        }

        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new IllegalArgumentException("Order not found");
        }

        Order order = orderOpt.get();
        Order.OrderStatus previousStatus = order.getStatus();
        
        // When order is PAID, deduct from inventory
        if (newStatus == Order.OrderStatus.PAID && previousStatus != Order.OrderStatus.PAID) {
            List<StockItem> warehouseStocks = stockItemRepository.findByWarehouseId(warehouseId);
            
            for (OrderItem item : order.getItems()) {
                warehouseStocks.stream()
                        .filter(si -> si.getItemId().equals(item.getItemId()))
                        .findFirst()
                        .ifPresent(stock -> {
                            stock.deduct(item.getQuantity());
                            stockItemRepository.update(stock);
                        });
            }
        }
        
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        
        if (newStatus == Order.OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        }
        
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
