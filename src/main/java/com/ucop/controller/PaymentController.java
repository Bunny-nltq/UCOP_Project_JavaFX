package com.ucop.controller;

import com.ucop.dto.PaymentCalculationDTO;
import com.ucop.entity.Payment;
import com.ucop.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * REST Controller for Payment operations
 */
@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * Calculate payment breakdown
     */
    @PostMapping("/calculate")
    public ResponseEntity<?> calculatePayment(@RequestParam BigDecimal subtotal,
                                             @RequestParam(defaultValue = "0") BigDecimal itemDiscount,
                                             @RequestParam(defaultValue = "0") BigDecimal cartDiscount,
                                             @RequestParam String paymentMethod) {
        try {
            Payment.PaymentMethod method = Payment.PaymentMethod.valueOf(paymentMethod);
            PaymentCalculationDTO calculation = paymentService.calculatePayment(
                    subtotal, itemDiscount, cartDiscount, method
            );
            return ResponseEntity.ok(calculation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid payment method"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Create payment
     */
    @PostMapping("")
    public ResponseEntity<?> createPayment(@RequestParam Long orderId,
                                          @RequestParam String paymentMethod,
                                          @RequestParam BigDecimal amount) {
        try {
            Payment.PaymentMethod method = Payment.PaymentMethod.valueOf(paymentMethod);
            Payment payment = paymentService.createPayment(orderId, method, amount);
            return ResponseEntity.status(HttpStatus.CREATED).body(payment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid payment method"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Process payment
     */
    @PostMapping("/{paymentId}/process")
    public ResponseEntity<?> processPayment(@PathVariable Long paymentId,
                                           @RequestParam(defaultValue = "true") boolean success) {
        try {
            Payment payment = paymentService.processPayment(paymentId, success);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get payment by ID
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPayment(@PathVariable Long paymentId) {
        try {
            Optional<Payment> payment = paymentService.getPaymentById(paymentId);
            if (payment.isPresent()) {
                return ResponseEntity.ok(payment.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Payment not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get payments by order
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getPaymentsByOrder(@PathVariable Long orderId) {
        try {
            List<Payment> payments = paymentService.getPaymentsByOrderId(orderId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update payment status
     */
    @PutMapping("/{paymentId}/status")
    public ResponseEntity<?> updatePaymentStatus(@PathVariable Long paymentId,
                                                @RequestParam String status) {
        try {
            Payment.PaymentStatus paymentStatus = Payment.PaymentStatus.valueOf(status);
            paymentService.updatePaymentStatus(paymentId, paymentStatus);
            return ResponseEntity.ok(Map.of("message", "Payment status updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid payment status"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
