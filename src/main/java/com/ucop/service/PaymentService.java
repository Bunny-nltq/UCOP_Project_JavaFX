package com.ucop.service;

import com.ucop.entity.*;
import com.ucop.dao.PaymentCalculationDAO;
import com.ucop.repository.*;
import com.ucop.util.PaymentCalculator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final OrderRepository orderRepository;

    private static final BigDecimal WALLET_COMMISSION_RATE = new BigDecimal("0.01"); // 1%

    public PaymentService(PaymentRepository paymentRepository,
                          RefundRepository refundRepository,
                          OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.refundRepository = refundRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Calculate payment breakdown
     */
    public PaymentCalculationDAO calculatePayment(BigDecimal subtotal,
                                                  BigDecimal itemDiscount,
                                                  BigDecimal cartDiscount,
                                                  Payment.PaymentMethod paymentMethod) {

        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Subtotal must be greater than zero");
        }

        if (itemDiscount == null) itemDiscount = BigDecimal.ZERO;
        if (cartDiscount == null) cartDiscount = BigDecimal.ZERO;

        BigDecimal taxAmount = PaymentCalculator.calculateTax(subtotal);
        BigDecimal shippingFee = PaymentCalculator.calculateShippingFee(subtotal);

        BigDecimal codFee = BigDecimal.ZERO;
        BigDecimal gatewayFee = BigDecimal.ZERO;

        switch (paymentMethod) {
            case COD -> codFee = PaymentCalculator.calculateCODFee(subtotal);
            case GATEWAY -> gatewayFee = PaymentCalculator.calculateGatewayFee(subtotal);
            case WALLET -> gatewayFee = subtotal.multiply(WALLET_COMMISSION_RATE);
            default -> {}
        }

        return new PaymentCalculationDAO(
                subtotal,
                itemDiscount,
                cartDiscount,
                taxAmount,
                shippingFee,
                codFee,
                gatewayFee
        );
    }

    /**
     * Create a payment for an order
     */
    public Payment createPayment(Long orderId, Payment.PaymentMethod method, BigDecimal amount) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        Payment payment = new Payment(order, method, amount);
        payment.setStatus(Payment.PaymentStatus.PENDING); // ok

        return paymentRepository.save(payment);
    }

    /**
     * Process payment (simulate gateway)
     */
    public Payment processPayment(Long paymentId, boolean success) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (success) {
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
            payment.setTransactionId(generateTransactionId());
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
        }

        paymentRepository.update(payment);
        return payment;
    }

    /**
     * Update payment status
     */
    public void updatePaymentStatus(Long paymentId, Payment.PaymentStatus status) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        payment.setStatus(status);
        paymentRepository.update(payment);
    }

    public Optional<Payment> getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId);
    }

    public List<Payment> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public List<Payment> getPaymentsByStatus(Payment.PaymentStatus status) {
        return paymentRepository.findByStatus(status.name());
    }

    /**
     * Generate unique transaction ID
     */
    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }
}
