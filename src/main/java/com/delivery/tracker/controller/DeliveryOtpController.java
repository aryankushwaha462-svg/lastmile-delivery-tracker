package com.delivery.tracker.controller;

import com.delivery.tracker.model.Order;
import com.delivery.tracker.model.OrderStatus;
import com.delivery.tracker.repository.OrderRepository;
import com.delivery.tracker.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class DeliveryOtpController {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    public DeliveryOtpController(OrderRepository orderRepository, NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
    }

    @PostMapping("/{id}/send-delivery-otp")
    public ResponseEntity<?> sendDeliveryOtp(@PathVariable Long id, Authentication auth) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return ResponseEntity.status(404).body("Order not found.");
        }
        if (order.getCustomerEmail() == null || order.getCustomerEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Customer email is missing for this order.");
        }

        String otp = String.valueOf(100000 + RANDOM.nextInt(900000));
        order.setDeliveryOtp(otp);
        order.setDeliveryOtpExpiresAt(LocalDateTime.now().plusMinutes(15));
        orderRepository.save(order);

        notificationService.sendDeliveryOtpEmail(order, otp);
        return ResponseEntity.ok("Delivery OTP sent to customer email.");
    }

    @PostMapping("/{id}/verify-delivery-otp")
    public ResponseEntity<?> verifyDeliveryOtp(@PathVariable Long id,
                                               @RequestBody Map<String, String> body,
                                               Authentication auth) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return ResponseEntity.status(404).body("Order not found.");
        }

        String submittedOtp = String.valueOf(body.getOrDefault("otp", "")).trim();
        if (order.getDeliveryOtp() == null || order.getDeliveryOtpExpiresAt() == null) {
            return ResponseEntity.badRequest().body("No active delivery OTP. Click 'Send OTP' first.");
        }
        if (LocalDateTime.now().isAfter(order.getDeliveryOtpExpiresAt())) {
            return ResponseEntity.badRequest().body("Delivery OTP has expired. Please resend.");
        }
        if (!order.getDeliveryOtp().equals(submittedOtp)) {
            return ResponseEntity.badRequest().body("Invalid delivery OTP.");
        }

        order.setStatus(OrderStatus.DELIVERED);
        order.setDeliveryOtp(null);
        order.setDeliveryOtpExpiresAt(null);
        orderRepository.save(order);

        notificationService.sendStatusNotification(order, OrderStatus.DELIVERED);
        return ResponseEntity.ok("OTP verified! Order marked as DELIVERED.");
    }
}