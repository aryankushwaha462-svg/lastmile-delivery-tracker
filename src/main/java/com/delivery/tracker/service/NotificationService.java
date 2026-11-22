package com.delivery.tracker.service;

import com.delivery.tracker.model.Order;
import com.delivery.tracker.model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:}")
    private String fromEmail;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            if (fromEmail != null && !fromEmail.isBlank()) {
                message.setFrom(fromEmail);
            }
            message.setTo(toEmail);
            message.setSubject("LastMileTracker — Verify Your Account");
            message.setText("Your 6-digit verification code is: " + otp +
                    "\n\nThis code expires in 5 minutes.");
            mailSender.send(message);
            log.info("OTP email dispatched asynchronously to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendStatusNotification(Order order, OrderStatus status) {
        if (order == null || order.getCustomerEmail() == null || order.getCustomerEmail().isBlank()) {
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            if (fromEmail != null && !fromEmail.isBlank()) {
                message.setFrom(fromEmail);
            }
            message.setTo(order.getCustomerEmail());
            message.setSubject("Order #" + order.getId() + " Status Update: " + status.name());
            message.setText("Dear Customer,\n\nYour order #" + order.getId() + " is now: " + status.name() +
                    ".\nTrack your order status live on our portal.\n\nThank you!");
            mailSender.send(message);
            log.info("Status notification ({}) sent asynchronously for Order #{} to {}",
                    status.name(), order.getId(), order.getCustomerEmail());
        } catch (Exception e) {
            log.error("Failed to send email notification for Order #{}: {}", order.getId(), e.getMessage());
        }
    }
}