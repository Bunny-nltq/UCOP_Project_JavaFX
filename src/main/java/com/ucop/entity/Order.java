package com.ucop.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @NotNull
    @Column(name = "status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.CART;

    @Column(name = "order_number", unique = true, length = 50)
    private String orderNumber;

    // Customer Information
    @Column(name = "shipping_name", length = 100)
    private String shippingName;

    @Column(name = "shipping_phone", length = 20)
    private String shippingPhone;

    @Column(name = "shipping_address", length = 255)
    private String shippingAddress;

    @Column(name = "shipping_city", length = 50)
    private String shippingCity;

    @Column(name = "shipping_postal_code", length = 20)
    private String shippingPostalCode;

    // Amount Calculations
    @Column(name = "subtotal", nullable = false, precision = 19, scale = 4)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "item_discount", nullable = false, precision = 19, scale = 4)
    private BigDecimal itemDiscount = BigDecimal.ZERO;

    @Column(name = "cart_discount", nullable = false, precision = 19, scale = 4)
    private BigDecimal cartDiscount = BigDecimal.ZERO;

    @Column(name = "tax_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "shipping_fee", nullable = false, precision = 19, scale = 4)
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(name = "cod_fee", nullable = false, precision = 19, scale = 4)
    private BigDecimal codFee = BigDecimal.ZERO;

    @Column(name = "gateway_fee", nullable = false, precision = 19, scale = 4)
    private BigDecimal gatewayFee = BigDecimal.ZERO;

    @Column(name = "grand_total", nullable = false, precision = 19, scale = 4)
    private BigDecimal grandTotal = BigDecimal.ZERO;

    @Column(name = "amount_due", nullable = false, precision = 19, scale = 4)
    private BigDecimal amountDue = BigDecimal.ZERO;

    // Promotion
    @Column(name = "promotion_code", length = 50)
    private String promotionCode;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "placed_at")
    private LocalDateTime placedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OrderItem> items = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Payment> payments = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Shipment> shipments = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
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
