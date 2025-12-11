package com.ucop.controller;

import com.ucop.dao.CartItemDAO;
import com.ucop.entity.Cart;
import com.ucop.entity.Order;
import com.ucop.service.OrderService;

public class CartController {

    private final OrderService orderService;

    public CartController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Get or create cart for customer
     */
    public Cart getOrCreateCart(Long accountId) {
        return orderService.getOrCreateCart(accountId);
    }

    /**
     * Add item to cart
     */
    public void addToCart(Long cartId, CartItemDAO itemDTO) {
        orderService.addToCart(cartId, itemDTO);
    }

    /**
     * Remove item from cart
     */
    public void removeFromCart(Long cartId, Long itemId) {
        orderService.removeFromCart(cartId, itemId);
    }

    /**
     * Update cart item quantity
     */
    public void updateQuantity(Long cartId, Long itemId, Long quantity) {
        orderService.updateCartItemQuantity(cartId, itemId, quantity);
    }

    /**
     * Place order from cart
     */
    public Order placeOrder(Long cartId) {
        return orderService.placeOrder(cartId, Order.OrderStatus.PLACED);
    }
}
