package com.delivery.tracker.service;

import com.delivery.tracker.model.Order;
import com.delivery.tracker.model.OrderStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendStatusNotification(Order order, OrderStatus status) {
        if (order.getCustomerEmail() == null || order.getCustomerEmail().isEmpty()) return;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(order.getCustomerEmail());
            message.setSubject("Order #" + order.getId() + " Status Update: " + status.name());
            message.setText("Dear Customer,\n\nYour order #" + order.getId() + " is now: " + status.name() +
                    ".\nTrack your order status live on our portal.\n\nThank you!");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
        }
    }
}