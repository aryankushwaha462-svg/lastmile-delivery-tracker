package com.delivery.tracker.repository;

import com.delivery.tracker.model.Order;
import com.delivery.tracker.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerEmailIgnoreCase(String email);
    List<Order> findByAssignedAgentEmailIgnoreCase(String email);
    List<Order> findByAssignedAgentId(Long agentId);

    boolean existsByOrderNumber(String orderNumber);
    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("SELECT o FROM Order o WHERE " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:zoneId IS NULL OR o.pickupZone.id = :zoneId OR o.dropZone.id = :zoneId) AND " +
           "(:agentId IS NULL OR o.assignedAgent.id = :agentId)")
    List<Order> findWithFilters(@Param("status") OrderStatus status,
                                @Param("zoneId") Long zoneId,
                                @Param("agentId") Long agentId);
}