package com.ucop.controller;

import com.ucop.dao.PaymentCalculationDAO;
import com.ucop.entity.Payment;
import com.ucop.service.PaymentService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /** Calculate payment breakdown */
    public PaymentCalculationDAO calculatePayment(BigDecimal subtotal,
                                                  BigDecimal itemDiscount,
                                                  BigDecimal cartDiscount,
                                                  String paymentMethod) {

        Payment.PaymentMethod method = Payment.PaymentMethod.valueOf(paymentMethod);

        return paymentService.calculatePayment(
                subtotal,
                itemDiscount == null ? BigDecimal.ZERO : itemDiscount,
                cartDiscount == null ? BigDecimal.ZERO : cartDiscount,
                method
        );
    }

    /** Create payment */
    public Payment createPayment(Long orderId, String paymentMethod, BigDecimal amount) {

        Payment.PaymentMethod method = Payment.PaymentMethod.valueOf(paymentMethod);

        return paymentService.createPayment(orderId, method, amount);
    }

    /** Process payment */
    public Payment processPayment(Long paymentId, boolean success) {
        return paymentService.processPayment(paymentId, success);
    }

    /** Get payment by ID */
    public Optional<Payment> getPayment(Long paymentId) {
        return paymentService.getPaymentById(paymentId);
    }

    /** Get payments by order */
    public List<Payment> getPaymentsByOrder(Long orderId) {
        return paymentService.getPaymentsByOrderId(orderId);
    }

    /** Update payment status */
    public void updatePaymentStatus(Long paymentId, String status) {
        Payment.PaymentStatus paymentStatus = Payment.PaymentStatus.valueOf(status);
        paymentService.updatePaymentStatus(paymentId, paymentStatus);
    }
}
