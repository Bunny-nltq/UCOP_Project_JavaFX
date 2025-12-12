package com.ucop.Dao;

import java.math.BigDecimal;

public class PaymentCalculationDAO {
    private BigDecimal subtotal;
    private BigDecimal itemDiscount;
    private BigDecimal cartDiscount;
    private BigDecimal taxAmount;
    private BigDecimal shippingFee;
    private BigDecimal codFee;
    private BigDecimal gatewayFee;
    private BigDecimal grandTotal;
    private BigDecimal amountDue;

    public PaymentCalculationDAO() {
    }

    public PaymentCalculationDAO(BigDecimal subtotal, BigDecimal itemDiscount, BigDecimal cartDiscount,
                                  BigDecimal taxAmount, BigDecimal shippingFee, BigDecimal codFee,
                                  BigDecimal gatewayFee) {
        this.subtotal = subtotal;
        this.itemDiscount = itemDiscount;
        this.cartDiscount = cartDiscount;
        this.taxAmount = taxAmount;
        this.shippingFee = shippingFee;
        this.codFee = codFee;
        this.gatewayFee = gatewayFee;
        calculateGrandTotal();
    }

    public void calculateGrandTotal() {
        this.grandTotal = subtotal
                .subtract(itemDiscount)
                .subtract(cartDiscount)
                .add(taxAmount)
                .add(shippingFee)
                .add(codFee)
                .add(gatewayFee);
        this.amountDue = this.grandTotal;
    }

    // Getters & Setters
    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getItemDiscount() {
        return itemDiscount;
    }

    public void setItemDiscount(BigDecimal itemDiscount) {
        this.itemDiscount = itemDiscount;
    }

    public BigDecimal getCartDiscount() {
        return cartDiscount;
    }

    public void setCartDiscount(BigDecimal cartDiscount) {
        this.cartDiscount = cartDiscount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getCodFee() {
        return codFee;
    }

    public void setCodFee(BigDecimal codFee) {
        this.codFee = codFee;
    }

    public BigDecimal getGatewayFee() {
        return gatewayFee;
    }

    public void setGatewayFee(BigDecimal gatewayFee) {
        this.gatewayFee = gatewayFee;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public BigDecimal getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(BigDecimal amountDue) {
        this.amountDue = amountDue;
    }
}
