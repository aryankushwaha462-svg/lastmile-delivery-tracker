package com.delivery.tracker.dto;

public class OrderRequestDto {
    private String pickupAddress;
    private String pickupPincode;
    private String dropAddress;
    private String dropPincode;
    private Double length;
    private Double breadth;
    private Double height;
    private Double actualWeight;
    private String orderType;
    private String paymentType;
    private String customerEmail;

    public OrderRequestDto() {}

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
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
}