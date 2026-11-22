package com.delivery.tracker.repository;

import com.delivery.tracker.model.OrderTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderTrackingRepository extends JpaRepository<OrderTracking, Long> {
    List<OrderTracking> findByOrderIdOrderByTimestampAsc(Long orderId);
}