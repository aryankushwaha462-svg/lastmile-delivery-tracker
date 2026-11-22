package com.delivery.tracker.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private final NotificationService notificationService;
    private final SecureRandom random = new SecureRandom();
    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();

    public OtpService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void generateAndSendOtp(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        String otp = String.format("%06d", random.nextInt(1_000_000));
        otpStore.put(normalizedEmail, new OtpEntry(otp, LocalDateTime.now().plusMinutes(5)));

        // Dispatches asynchronously via NotificationService so the UI responds immediately
        notificationService.sendOtpEmail(normalizedEmail, otp);
    }

    public boolean verifyOtp(String email, String inputOtp) {
        if (email == null || inputOtp == null) return false;
        String normalizedEmail = email.trim().toLowerCase();
        OtpEntry entry = otpStore.get(normalizedEmail);
        if (entry == null || LocalDateTime.now().isAfter(entry.expiresAt)) {
            otpStore.remove(normalizedEmail);
            return false;
        }
        if (entry.code.equals(inputOtp.trim())) {
            otpStore.remove(normalizedEmail); // Single-use OTP
            return true;
        }
        return false;
    }

    private record OtpEntry(String code, LocalDateTime expiresAt) {}
}