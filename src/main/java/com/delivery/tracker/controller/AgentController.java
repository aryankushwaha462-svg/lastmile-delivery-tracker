package com.delivery.tracker.controller;

import com.delivery.tracker.model.Order;
import com.delivery.tracker.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final OrderService orderService;

    public AgentController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getMyAssignedOrders(Authentication auth) {
        return ResponseEntity.ok(orderService.getOrdersByAgentEmail(auth.getName()));
    }
}