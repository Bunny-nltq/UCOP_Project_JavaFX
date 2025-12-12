package com.ucop.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PaymentCalculator {
    private static final int SCALE = 4;

    // Tax rates
    private static final BigDecimal VAT_RATE = new BigDecimal("0.10"); // 10% VAT
    private static final BigDecimal BASE_SHIPPING_RATE = new BigDecimal("25000");
    private static final BigDecimal COD_RATE = new BigDecimal("0.015"); // 1.5% of order value
    private static final BigDecimal GATEWAY_RATE = new BigDecimal("0.025"); // 2.5% of order value

    /**
     * Calculate tax (VAT) on subtotal
     */
    public static BigDecimal calculateTax(BigDecimal subtotal) {
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return subtotal.multiply(VAT_RATE).setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Calculate shipping fee based on subtotal tiers
     * - Free shipping: >= 500,000
     * - Reduced fee: >= 100,000 (10,000)
     * - Standard fee: < 100,000 (25,000)
     */
    public static BigDecimal calculateShippingFee(BigDecimal subtotal) {
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
        return BASE_SHIPPING_RATE;
    }

    /**
     * Calculate COD fee (Cash on Delivery)
     */
    public static BigDecimal calculateCODFee(BigDecimal subtotal) {
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return subtotal.multiply(COD_RATE).setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Calculate gateway fee (for online payment methods)
     */
    public static BigDecimal calculateGatewayFee(BigDecimal subtotal) {
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return subtotal.multiply(GATEWAY_RATE).setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Calculate discount percentage
     */
    public static BigDecimal calculateDiscountPercentage(BigDecimal subtotal, BigDecimal percentage) {
        if (subtotal == null || percentage == null || percentage.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return subtotal.multiply(percentage).divide(new BigDecimal("100"), SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Calculate fixed discount
     */
    public static BigDecimal calculateFixedDiscount(BigDecimal discount) {
        return discount != null ? discount : BigDecimal.ZERO;
    }

    /**
     * Round to 2 decimal places
     */
    public static BigDecimal round(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value.setScale(SCALE, RoundingMode.HALF_UP);
    }
}
