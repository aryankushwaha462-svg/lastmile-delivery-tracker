package com.delivery.tracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_tracking")
public class OrderTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String updatedBy;
    private String remarks;
    private LocalDateTime timestamp;

    public OrderTracking() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public static OrderTrackingBuilder builder() { return new OrderTrackingBuilder(); }

    public static class OrderTrackingBuilder {
        private final OrderTracking t = new OrderTracking();
        public OrderTrackingBuilder order(Order v) { t.order = v; return this; }
        public OrderTrackingBuilder status(OrderStatus v) { t.status = v; return this; }
        public OrderTrackingBuilder updatedBy(String v) { t.updatedBy = v; return this; }
        public OrderTrackingBuilder remarks(String v) { t.remarks = v; return this; }
        public OrderTrackingBuilder timestamp(LocalDateTime v) { t.timestamp = v; return this; }
        public OrderTracking build() { return t; }
    }
}