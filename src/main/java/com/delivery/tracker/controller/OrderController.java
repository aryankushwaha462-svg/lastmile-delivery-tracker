package com.delivery.tracker.controller;

import com.delivery.tracker.dto.ChargeCalculationDto;
import com.delivery.tracker.dto.OrderRequestDto;
import com.delivery.tracker.model.Order;
import com.delivery.tracker.model.OrderStatus;
import com.delivery.tracker.model.OrderTracking;
import com.delivery.tracker.service.OrderService;
import com.delivery.tracker.service.RateCalculationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final RateCalculationService rateCalculationService;

    public OrderController(OrderService orderService, RateCalculationService rateCalculationService) {
        this.orderService = orderService;
        this.rateCalculationService = rateCalculationService;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) Long agentId,
            Authentication auth) {
        if (hasRole(auth, "ROLE_ADMIN")) {
            return ResponseEntity.ok(orderService.getFilteredOrders(status, zoneId, agentId));
        }
        return ResponseEntity.ok(orderService.getOrdersByCustomerEmail(auth.getName()));
    }

    @PostMapping("/calculate-charge")
    public ResponseEntity<ChargeCalculationDto> calculateCharge(@RequestBody OrderRequestDto dto) {
        return ResponseEntity.ok(rateCalculationService.calculateCharge(dto));
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequestDto dto, Authentication auth) {
        if (!hasRole(auth, "ROLE_ADMIN") || dto.getCustomerEmail() == null || dto.getCustomerEmail().isBlank()) {
            dto.setCustomerEmail(auth.getName());
        }
        return ResponseEntity.ok(orderService.createOrder(dto));
    }

    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status,
            @RequestParam(required = false) String remarks,
            Authentication auth) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status, auth.getName(), remarks));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/assign-agent")
    public ResponseEntity<Order> assignAgentManually(
            @PathVariable Long id,
            @RequestParam Long agentId,
            Authentication auth) {
        return ResponseEntity.ok(orderService.assignAgentManually(id, agentId, auth.getName()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/auto-assign")
    public ResponseEntity<Order> triggerAutoAssign(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(orderService.triggerAutoAssignment(id, auth.getName()));
    }

    @PostMapping("/{id}/reschedule")
    public ResponseEntity<Order> rescheduleOrder(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newDate,
            Authentication auth) {
        return ResponseEntity.ok(
                orderService.rescheduleFailedDelivery(id, newDate, auth.getName(), hasRole(auth, "ROLE_ADMIN")));
    }

    @GetMapping("/{id}/tracking")
    public ResponseEntity<List<OrderTracking>> getTrackingHistory(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(
                orderService.getTrackingHistory(id, auth.getName(), hasRole(auth, "ROLE_ADMIN"), hasRole(auth, "ROLE_AGENT")));
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role));
    }
}