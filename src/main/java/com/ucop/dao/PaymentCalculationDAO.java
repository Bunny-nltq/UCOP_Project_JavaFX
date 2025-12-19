package com.ucop.dao;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Payment Calculation
 * Used for transferring payment breakdown information between layers
 */
public class PaymentCalculationDAO {
    private BigDecimal subtotal;
    private BigDecimal itemDiscount;
    private BigDecimal cartDiscount;
    private BigDecimal taxAmount;
    private BigDecimal shippingFee;
    private BigDecimal codFee;
    private BigDecimal gatewayFee;

    public PaymentCalculationDAO() {}

    public PaymentCalculationDAO(BigDecimal subtotal,
                                 BigDecimal itemDiscount,
                                 BigDecimal cartDiscount,
                                 BigDecimal taxAmount,
                                 BigDecimal shippingFee,
                                 BigDecimal codFee,
                                 BigDecimal gatewayFee) {
        this.subtotal = subtotal;
        this.itemDiscount = itemDiscount;
        this.cartDiscount = cartDiscount;
        this.taxAmount = taxAmount;
        this.shippingFee = shippingFee;
        this.codFee = codFee;
        this.gatewayFee = gatewayFee;
    }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getItemDiscount() { return itemDiscount; }
    public void setItemDiscount(BigDecimal itemDiscount) { this.itemDiscount = itemDiscount; }

    public BigDecimal getCartDiscount() { return cartDiscount; }
    public void setCartDiscount(BigDecimal cartDiscount) { this.cartDiscount = cartDiscount; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getShippingFee() { return shippingFee; }
    public void setShippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; }

    public BigDecimal getCodFee() { return codFee; }
    public void setCodFee(BigDecimal codFee) { this.codFee = codFee; }

    public BigDecimal getGatewayFee() { return gatewayFee; }
    public void setGatewayFee(BigDecimal gatewayFee) { this.gatewayFee = gatewayFee; }

    /**
     * Calculate total payment amount
     * Total = subtotal - itemDiscount - cartDiscount + taxAmount + shippingFee + codFee + gatewayFee
     */
    public BigDecimal getTotalAmount() {
        BigDecimal total = subtotal
                .subtract(itemDiscount != null ? itemDiscount : BigDecimal.ZERO)
                .subtract(cartDiscount != null ? cartDiscount : BigDecimal.ZERO)
                .add(taxAmount != null ? taxAmount : BigDecimal.ZERO)
                .add(shippingFee != null ? shippingFee : BigDecimal.ZERO)
                .add(codFee != null ? codFee : BigDecimal.ZERO)
                .add(gatewayFee != null ? gatewayFee : BigDecimal.ZERO);
        return total.max(BigDecimal.ZERO); // Ensure non-negative
    }

    @Override
    public String toString() {
        return "PaymentCalculationDTO{" +
                "subtotal=" + subtotal +
                ", itemDiscount=" + itemDiscount +
                ", cartDiscount=" + cartDiscount +
                ", taxAmount=" + taxAmount +
                ", shippingFee=" + shippingFee +
                ", codFee=" + codFee +
                ", gatewayFee=" + gatewayFee +
                ", total=" + getTotalAmount() +
                '}';
    }
}
