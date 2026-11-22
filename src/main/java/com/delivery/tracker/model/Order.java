package com.delivery.tracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pickupAddress;
    private String pickupPincode;
    private String dropAddress;
    private String dropPincode;

    private Double length;
    private Double breadth;
    private Double height;
    private Double actualWeight;
    private Double volumetricWeight;
    private Double chargeableWeight;

    private String orderType;
    private String paymentType;

    private Double baseCharge;
    private Double codSurcharge;
    private Double totalCharge;

    @ManyToOne
    @JoinColumn(name = "pickup_zone_id")
    private Zone pickupZone;

    @ManyToOne
    @JoinColumn(name = "drop_zone_id")
    private Zone dropZone;

    @ManyToOne
    @JoinColumn(name = "assigned_agent_id")
    private Agent assignedAgent;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String customerEmail;
    private LocalDateTime createdAt;
    private LocalDateTime rescheduledDate;

    public Order() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
    public String getPickupPincode() { return pickupPincode; }
    public void setPickupPincode(String pickupPincode) { this.pickupPincode = pickupPincode; }
    public String getDropAddress() { return dropAddress; }
    public void setDropAddress(String dropAddress) { this.dropAddress = dropAddress; }
    public String getDropPincode() { return dropPincode; }
    public void setDropPincode(String dropPincode) { this.dropPincode = dropPincode; }
    public Double getLength() { return length; }
    public void setLength(Double length) { this.length = length; }
    public Double getBreadth() { return breadth; }
    public void setBreadth(Double breadth) { this.breadth = breadth; }
    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }
    public Double getActualWeight() { return actualWeight; }
    public void setActualWeight(Double actualWeight) { this.actualWeight = actualWeight; }
    public Double getVolumetricWeight() { return volumetricWeight; }
    public void setVolumetricWeight(Double volumetricWeight) { this.volumetricWeight = volumetricWeight; }
    public Double getChargeableWeight() { return chargeableWeight; }
    public void setChargeableWeight(Double chargeableWeight) { this.chargeableWeight = chargeableWeight; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    public Double getBaseCharge() { return baseCharge; }
    public void setBaseCharge(Double baseCharge) { this.baseCharge = baseCharge; }
    public Double getCodSurcharge() { return codSurcharge; }
    public void setCodSurcharge(Double codSurcharge) { this.codSurcharge = codSurcharge; }
    public Double getTotalCharge() { return totalCharge; }
    public void setTotalCharge(Double totalCharge) { this.totalCharge = totalCharge; }
    public Zone getPickupZone() { return pickupZone; }
    public void setPickupZone(Zone pickupZone) { this.pickupZone = pickupZone; }
    public Zone getDropZone() { return dropZone; }
    public void setDropZone(Zone dropZone) { this.dropZone = dropZone; }
    public Agent getAssignedAgent() { return assignedAgent; }
    public void setAssignedAgent(Agent assignedAgent) { this.assignedAgent = assignedAgent; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getRescheduledDate() { return rescheduledDate; }
    public void setRescheduledDate(LocalDateTime rescheduledDate) { this.rescheduledDate = rescheduledDate; }

    public static OrderBuilder builder() { return new OrderBuilder(); }

    public static class OrderBuilder {
        private final Order o = new Order();
        public OrderBuilder pickupAddress(String v) { o.pickupAddress = v; return this; }
        public OrderBuilder pickupPincode(String v) { o.pickupPincode = v; return this; }
        public OrderBuilder dropAddress(String v) { o.dropAddress = v; return this; }
        public OrderBuilder dropPincode(String v) { o.dropPincode = v; return this; }
        public OrderBuilder length(Double v) { o.length = v; return this; }
        public OrderBuilder breadth(Double v) { o.breadth = v; return this; }
        public OrderBuilder height(Double v) { o.height = v; return this; }
        public OrderBuilder actualWeight(Double v) { o.actualWeight = v; return this; }
        public OrderBuilder volumetricWeight(Double v) { o.volumetricWeight = v; return this; }
        public OrderBuilder chargeableWeight(Double v) { o.chargeableWeight = v; return this; }
        public OrderBuilder orderType(String v) { o.orderType = v; return this; }
        public OrderBuilder paymentType(String v) { o.paymentType = v; return this; }
        public OrderBuilder baseCharge(Double v) { o.baseCharge = v; return this; }
        public OrderBuilder codSurcharge(Double v) { o.codSurcharge = v; return this; }
        public OrderBuilder totalCharge(Double v) { o.totalCharge = v; return this; }
        public OrderBuilder pickupZone(Zone v) { o.pickupZone = v; return this; }
        public OrderBuilder dropZone(Zone v) { o.dropZone = v; return this; }
        public OrderBuilder assignedAgent(Agent v) { o.assignedAgent = v; return this; }
        public OrderBuilder status(OrderStatus v) { o.status = v; return this; }
        public OrderBuilder customerEmail(String v) { o.customerEmail = v; return this; }
        public OrderBuilder createdAt(LocalDateTime v) { o.createdAt = v; return this; }
        public Order build() { return o; }
    }
}