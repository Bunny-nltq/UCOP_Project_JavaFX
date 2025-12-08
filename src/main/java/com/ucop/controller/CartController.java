package com.ucop.controller;

import com.ucop.dao.CartItemDAO;
import com.ucop.entity.Cart;
import com.ucop.entity.Order;
import com.ucop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST Controller for Cart operations
 */
@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private OrderService orderService;

    /**
     * Get or create cart for customer
     */
    @PostMapping("/get-or-create/{accountId}")
    public ResponseEntity<?> getOrCreateCart(@PathVariable Long accountId) {
        try {
            Cart cart = orderService.getOrCreateCart(accountId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Add item to cart
     */
    @PostMapping("/{cartId}/add-item")
    public ResponseEntity<?> addToCart(@PathVariable Long cartId, @RequestBody CartItemDAO itemDTO) {
        try {
            orderService.addToCart(cartId, itemDTO);
            return ResponseEntity.ok(Map.of("message", "Item added to cart successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Remove item from cart
     */
    @DeleteMapping("/{cartId}/item/{itemId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long cartId, @PathVariable Long itemId) {
        try {
            orderService.removeFromCart(cartId, itemId);
            return ResponseEntity.ok(Map.of("message", "Item removed from cart successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update cart item quantity
     */
    @PutMapping("/{cartId}/item/{itemId}/quantity")
    public ResponseEntity<?> updateQuantity(@PathVariable Long cartId,
                                           @PathVariable Long itemId,
                                           @RequestParam Long quantity) {
        try {
            orderService.updateCartItemQuantity(cartId, itemId, quantity);
            return ResponseEntity.ok(Map.of("message", "Quantity updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Place order from cart
     */
    @PostMapping("/{cartId}/place-order")
    public ResponseEntity<?> placeOrder(@PathVariable Long cartId) {
        try {
            Order order = orderService.placeOrder(cartId, Order.OrderStatus.PLACED);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
