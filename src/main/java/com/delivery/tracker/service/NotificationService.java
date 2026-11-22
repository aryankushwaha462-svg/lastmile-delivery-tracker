package com.delivery.tracker.service;

import com.delivery.tracker.model.Order;
import com.delivery.tracker.model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.mail.from:}")
    private String fromEmail;

    @Value("${BREVO_API_KEY:}")
    private String brevoApiKey;

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        String subject = "LastMileTracker — Verify Your Account";
        String text = "Your 6-digit verification code is: " + otp +
                "\n\nThis code expires in 5 minutes.";
        sendViaBrevoApi(toEmail, subject, text);
    }

    @Async
    public void sendStatusNotification(Order order, OrderStatus status) {
        if (order == null || order.getCustomerEmail() == null || order.getCustomerEmail().isBlank()) {
            return;
        }
        String subject = "Order #" + order.getId() + " Status Update: " + status.name();
        String text = "Dear Customer,\n\nYour order #" + order.getId() + " is now: " + status.name() +
                ".\nTrack your order status live on our portal.\n\nThank you!";
        sendViaBrevoApi(order.getCustomerEmail(), subject, text);
    }

    private void sendViaBrevoApi(String toEmail, String subject, String textContent) {
        if (brevoApiKey == null || brevoApiKey.isBlank()) {
            log.error("BREVO_API_KEY is missing! Cannot send email to {}", toEmail);
            return;
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            Map<String, Object> body = Map.of(
                    "sender", Map.of("name", "LastMileTracker", "email", fromEmail),
                    "to", List.of(Map.of("email", toEmail)),
                    "subject", subject,
                    "textContent", textContent
            );

            HttpEntity> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(BREVO_API_URL, request, String.class);
            log.info("Email sent via Brevo HTTPS API to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email via Brevo API to {}: {}", toEmail, e.getMessage());
        }
    }
}