package com.ucop.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity

@Table(name = "orders")

public class Order {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    
    @Column(nullable = false)
    private Long accountId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.CART;
    
    @Column(nullable = false, unique = true)
    private String orderNumber;



    // Customer Information
    @Column
    private String shippingName;
    
    @Column
    private String shippingPhone;
    
    @Column
    private String shippingAddress;
    
    @Column
    private String shippingCity;
    
    @Column
    private String shippingPostalCode;



    // Amount Calculations
    @Column(nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private BigDecimal itemDiscount = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private BigDecimal cartDiscount = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private BigDecimal shippingFee = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private BigDecimal codFee = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private BigDecimal gatewayFee = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private BigDecimal grandTotal = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private BigDecimal amountDue = BigDecimal.ZERO;



    // Promotion
    @Column
    private String promotionCode;
    
    @Column
    private String notes;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column
    private String createdBy;
    
    @Column
    private String updatedBy;
    
    @Column
    private LocalDateTime placedAt;
    
    @Column
    private LocalDateTime paidAt;
    
    @Column
    private LocalDateTime shippedAt;
    
    @Column
    private LocalDateTime deliveredAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<OrderItem> items = new HashSet<>();
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Payment> payments = new HashSet<>();
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Shipment> shipments = new HashSet<>();
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Appointment> appointments = new HashSet<>();



    // Constructors

    public Order() {

    }



    public Order(Long accountId) {

        this.accountId = accountId;

        this.status = OrderStatus.CART;

    }



    // Business methods

    public void addItem(OrderItem item) {

        item.setOrder(this);

        items.add(item);

    }



    public void removeItem(OrderItem item) {

        items.remove(item);

    }



    public void addPayment(Payment payment) {

        payment.setOrder(this);

        payments.add(payment);

    }



    public void addShipment(Shipment shipment) {

        shipment.setOrder(this);

        shipments.add(shipment);

    }



    public void addAppointment(Appointment appointment) {

        appointment.setOrder(this);

        appointments.add(appointment);

    }



    public BigDecimal calculateGrandTotal() {

        BigDecimal total = subtotal;

        total = total.subtract(itemDiscount);

        total = total.subtract(cartDiscount);

        total = total.add(taxAmount);

        total = total.add(shippingFee);

        total = total.add(codFee);

        total = total.add(gatewayFee);

        return total;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(orderNumber, order.orderNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderNumber);
    }

    // Getters & Setters

    public Long getId() {

        return id;

    }



    public void setId(Long id) {

        this.id = id;

    }



    public Long getAccountId() {

        return accountId;

    }



    public void setAccountId(Long accountId) {

        this.accountId = accountId;

    }



    public OrderStatus getStatus() {

        return status;

    }



    public void setStatus(OrderStatus status) {

        this.status = status;

    }



    public String getOrderNumber() {

        return orderNumber;

    }



    public void setOrderNumber(String orderNumber) {

        this.orderNumber = orderNumber;

    }



    public String getShippingName() {

        return shippingName;

    }



    public void setShippingName(String shippingName) {

        this.shippingName = shippingName;

    }



    public String getShippingPhone() {

        return shippingPhone;

    }



    public void setShippingPhone(String shippingPhone) {

        this.shippingPhone = shippingPhone;

    }



    public String getShippingAddress() {

        return shippingAddress;

    }



    public void setShippingAddress(String shippingAddress) {

        this.shippingAddress = shippingAddress;

    }



    public String getShippingCity() {

        return shippingCity;

    }



    public void setShippingCity(String shippingCity) {

        this.shippingCity = shippingCity;

    }



    public String getShippingPostalCode() {

        return shippingPostalCode;

    }



    public void setShippingPostalCode(String shippingPostalCode) {

        this.shippingPostalCode = shippingPostalCode;

    }



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



    public String getPromotionCode() {

        return promotionCode;

    }



    public void setPromotionCode(String promotionCode) {

        this.promotionCode = promotionCode;

    }



    public String getNotes() {

        return notes;

    }



    public void setNotes(String notes) {

        this.notes = notes;

    }



    public LocalDateTime getCreatedAt() {

        return createdAt;

    }



    public void setCreatedAt(LocalDateTime createdAt) {

        this.createdAt = createdAt;

    }



    public LocalDateTime getUpdatedAt() {

        return updatedAt;

    }



    public void setUpdatedAt(LocalDateTime updatedAt) {

        this.updatedAt = updatedAt;

    }



    public String getCreatedBy() {

        return createdBy;

    }



    public void setCreatedBy(String createdBy) {

        this.createdBy = createdBy;

    }



    public String getUpdatedBy() {

        return updatedBy;

    }



    public void setUpdatedBy(String updatedBy) {

        this.updatedBy = updatedBy;

    }



    public LocalDateTime getPlacedAt() {

        return placedAt;

    }



    public void setPlacedAt(LocalDateTime placedAt) {

        this.placedAt = placedAt;

    }



    public LocalDateTime getPaidAt() {

        return paidAt;

    }



    public void setPaidAt(LocalDateTime paidAt) {

        this.paidAt = paidAt;

    }



    public LocalDateTime getShippedAt() {

        return shippedAt;

    }



    public void setShippedAt(LocalDateTime shippedAt) {

        this.shippedAt = shippedAt;

    }



    public LocalDateTime getDeliveredAt() {

        return deliveredAt;

    }



    public void setDeliveredAt(LocalDateTime deliveredAt) {

        this.deliveredAt = deliveredAt;

    }



    public Set<OrderItem> getItems() {

        return items;

    }



    public void setItems(Set<OrderItem> items) {

        this.items = items;

    }



    public Set<Payment> getPayments() {

        return payments;

    }



    public void setPayments(Set<Payment> payments) {

        this.payments = payments;

    }



    public Set<Shipment> getShipments() {

        return shipments;

    }



    public void setShipments(Set<Shipment> shipments) {

        this.shipments = shipments;

    }



    public Set<Appointment> getAppointments() {

        return appointments;

    }



    public void setAppointments(Set<Appointment> appointments) {

        this.appointments = appointments;

    }



    // Enum for Order Status

    public enum OrderStatus {

        CART,

        PLACED,

        PENDING_PAYMENT,

        PAID,

        PACKED,

        SHIPPED,

        DELIVERED,

        CLOSED,

        CANCELED,

        RMA_REQUESTED,

        REFUNDED

    }

}




