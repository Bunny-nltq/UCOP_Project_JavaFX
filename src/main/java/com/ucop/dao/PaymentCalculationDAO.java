package com.ucop.dao;

import com.ucop.util.HibernateUtil;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * DAO for Payment Calculation operations
 * Handles data access and calculation for payment-related transactions
 */
public class PaymentCalculationDAO {

    /**
     * Calculate total amount for an order including items and shipping
     * @param orderId - ID of the order
     * @return Map containing:
     *         - "subtotal": Sum of all order items (item price * quantity)
     *         - "shipping": Calculated shipping fee
     *         - "tax": Calculated tax amount
     *         - "total": Final total amount
     */
    public Map<String, BigDecimal> calculateOrderTotal(Long orderId) {
        Map<String, BigDecimal> result = new HashMap<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Get subtotal from order items
            BigDecimal subtotal = session.createQuery(
                    "SELECT COALESCE(SUM(oi.unitPrice * oi.quantity), 0) FROM OrderItem oi WHERE oi.order.id = :orderId",
                    BigDecimal.class
            )
            .setParameter("orderId", orderId)
            .uniqueResult();

            if (subtotal == null) subtotal = BigDecimal.ZERO;

            result.put("subtotal", subtotal);

            // Calculate shipping fee based on subtotal
            BigDecimal shipping = calculateShippingFee(subtotal);
            result.put("shipping", shipping);

            // Calculate tax (assume 10% tax rate)
            BigDecimal tax = subtotal.multiply(new BigDecimal("0.10"));
            result.put("tax", tax);

            // Total = subtotal + shipping + tax
            BigDecimal total = subtotal.add(shipping).add(tax);
            result.put("total", total);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Calculate shipping fee based on subtotal
     * - Free shipping: >= 500,000
     * - Reduced fee: >= 100,000 (10,000)
     * - Standard fee: < 100,000 (25,000)
     * @param subtotal - Order subtotal
     * @return Calculated shipping fee
     */
    public BigDecimal calculateShippingFee(BigDecimal subtotal) {
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Free shipping for orders >= 500,000
        if (subtotal.compareTo(new BigDecimal("500000")) >= 0) {
            return BigDecimal.ZERO;
        }

        // Reduced shipping for orders >= 100,000
        if (subtotal.compareTo(new BigDecimal("100000")) >= 0) {
            return new BigDecimal("10000");
        }

        // Standard shipping fee for orders < 100,000
        return new BigDecimal("25000");
    }

    /**
     * Calculate discount amount based on discount percentage
     * @param subtotal - Order subtotal
     * @param discountPercent - Discount percentage (0-100)
     * @return Calculated discount amount
     */
    public BigDecimal calculateDiscount(BigDecimal subtotal, BigDecimal discountPercent) {
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if (discountPercent == null || discountPercent.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        // Ensure discount doesn't exceed 100%
        if (discountPercent.compareTo(new BigDecimal("100")) > 0) {
            discountPercent = new BigDecimal("100");
        }

        return subtotal.multiply(discountPercent).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Calculate tax amount based on subtotal
     * Default tax rate: 10%
     * @param subtotal - Order subtotal
     * @param taxRate - Tax rate (default 10%)
     * @return Calculated tax amount
     */
    public BigDecimal calculateTax(BigDecimal subtotal, BigDecimal taxRate) {
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if (taxRate == null || taxRate.compareTo(BigDecimal.ZERO) < 0) {
            taxRate = new BigDecimal("10");
        }

        return subtotal.multiply(taxRate).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Get the count of payments for an order
     * @param orderId - ID of the order
     * @return Number of payments for the order
     */
    public long getPaymentCountByOrderId(Long orderId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                    "SELECT COUNT(*) FROM Payment WHERE order.id = :orderId",
                    Long.class
            )
            .setParameter("orderId", orderId)
            .uniqueResult();
            return count != null ? count : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Get total payment amount for an order
     * @param orderId - ID of the order
     * @return Total amount paid for the order
     */
    public BigDecimal getTotalPaymentAmount(Long orderId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            BigDecimal total = session.createQuery(
                    "SELECT COALESCE(SUM(amount), 0) FROM Payment WHERE order.id = :orderId AND status = 'SUCCESS'",
                    BigDecimal.class
            )
            .setParameter("orderId", orderId)
            .uniqueResult();
            return total != null ? total : BigDecimal.ZERO;
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    /**
     * Check if order is fully paid
     * @param orderId - ID of the order
     * @return true if total payment amount equals order total, false otherwise
     */
    public boolean isOrderFullyPaid(Long orderId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            BigDecimal orderTotal = session.createQuery(
                    "SELECT o.totalAmount FROM Order o WHERE o.id = :orderId",
                    BigDecimal.class
            )
            .setParameter("orderId", orderId)
            .uniqueResult();

            if (orderTotal == null) return false;

            BigDecimal paidAmount = getTotalPaymentAmount(orderId);
            return paidAmount.compareTo(orderTotal) >= 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
