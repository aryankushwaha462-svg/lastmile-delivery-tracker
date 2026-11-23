package com.delivery.tracker.controller;

import com.delivery.tracker.model.Agent;
import com.delivery.tracker.model.Order;
import com.delivery.tracker.model.OrderStatus;
import com.delivery.tracker.repository.AgentRepository;
import com.delivery.tracker.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agent")
public class AgentProfileController {

    private final AgentRepository agentRepository;
    private final OrderRepository orderRepository;

    public AgentProfileController(AgentRepository agentRepository, OrderRepository orderRepository) {
        this.agentRepository = agentRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication auth) {
        Agent agent = agentRepository.findByEmailIgnoreCase(auth.getName()).orElse(null);
        if (agent == null) {
            return ResponseEntity.status(404).body("Agent profile not found.");
        }

        List<Order> orders = orderRepository.findByAssignedAgentId(agent.getId());

        long delivered = orders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count();
        long failed = orders.stream().filter(o -> o.getStatus() == OrderStatus.FAILED).count();
        long active = orders.stream().filter(o ->
                o.getStatus() != OrderStatus.DELIVERED && o.getStatus() != OrderStatus.FAILED
        ).count();

        double successRate = (delivered + failed) > 0
                ? Math.round((delivered * 1000.0) / (delivered + failed)) / 10.0
                : 100.0;

        double codCollected = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED && "COD".equalsIgnoreCase(String.valueOf(o.getPaymentType())))
                .mapToDouble(o -> o.getTotalCharge() != null ? o.getTotalCharge() : 0.0)
                .sum();

        Map<String, Object> response = new HashMap<>();
        response.put("id", agent.getId());
        response.put("name", agent.getName());
        response.put("email", agent.getEmail());
        response.put("phone", agent.getPhone() != null ? agent.getPhone() : "");
        response.put("vehicleType", agent.getVehicleType() != null ? agent.getVehicleType() : "BIKE");
        response.put("vehicleNumber", agent.getVehicleNumber() != null ? agent.getVehicleNumber() : "");
        response.put("isAvailable", agent.isAvailable());
        response.put("maxCapacity", agent.getMaxCapacity() != null ? agent.getMaxCapacity() : 5);
        response.put("zoneName", agent.getZone() != null ? agent.getZone().getName() : "Unassigned");
        response.put("stats", Map.of(
                "delivered", delivered,
                "failed", failed,
                "active", active,
                "successRate", successRate,
                "codCollected", Math.round(codCollected * 100.0) / 100.0
        ));

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/availability")
    public ResponseEntity<?> toggleAvailability(Authentication auth, @RequestBody Map<String, Boolean> body) {
        Agent agent = agentRepository.findByEmailIgnoreCase(auth.getName()).orElse(null);
        if (agent == null) {
            return ResponseEntity.status(404).body("Agent profile not found.");
        }

        boolean newStatus = body.getOrDefault("isAvailable", !agent.isAvailable());
        agent.setAvailable(newStatus);
        agentRepository.save(agent);

        return ResponseEntity.ok(Map.of("isAvailable", agent.isAvailable()));
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication auth, @RequestBody Map<String, String> body) {
        Agent agent = agentRepository.findByEmailIgnoreCase(auth.getName()).orElse(null);
        if (agent == null) {
            return ResponseEntity.status(404).body("Agent profile not found.");
        }

        if (body.containsKey("name") && !body.get("name").isBlank()) {
            agent.setName(body.get("name").trim());
        }
        if (body.containsKey("phone")) {
            agent.setPhone(body.get("phone").trim());
        }
        if (body.containsKey("vehicleType")) {
            agent.setVehicleType(body.get("vehicleType").trim());
        }
        if (body.containsKey("vehicleNumber")) {
            agent.setVehicleNumber(body.get("vehicleNumber").trim().toUpperCase());
        }

        agentRepository.save(agent);
        return ResponseEntity.ok("Agent profile updated successfully.");
    }
}