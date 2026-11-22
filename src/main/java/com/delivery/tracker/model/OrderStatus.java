package com.delivery.tracker.model;

public enum OrderStatus {
    CREATED,
    ASSIGNED,
    PICKED_UP,
    IN_TRANSIT,
    OUT_FOR_DELIVERY,
    DELIVERED,
    FAILED,
    RESCHEDULED
}