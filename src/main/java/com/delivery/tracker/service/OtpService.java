package com.delivery.tracker.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private final JavaMailSender mailSender;
    private final String senderEmail;
    private final SecureRandom random = new SecureRandom();
    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();

    public OtpService(JavaMailSender mailSender,
                      @Value("${spring.mail.username:}") String senderEmail) {
        this.mailSender = mailSender;
        this.senderEmail = senderEmail;
    }

    public void generateAndSendOtp(String email) {
        String otp = String.format("%06d", random.nextInt(1_000_000));
        SimpleMailMessage message = new SimpleMailMessage();
        if (!senderEmail.isBlank()) {
            message.setFrom(senderEmail);
        }
        message.setTo(email);
        message.setSubject("LastMileTracker - Your Verification OTP");
        message.setText("Your 6-digit verification code is: " + otp + "\n\nThis code expires in 5 minutes.");
        mailSender.send(message);

        otpStore.put(email.trim().toLowerCase(), new OtpEntry(otp, LocalDateTime.now().plusMinutes(5)));
    }

    public boolean verifyOtp(String email, String inputOtp) {
        OtpEntry entry = otpStore.get(email.toLowerCase());
        if (entry == null || LocalDateTime.now().isAfter(entry.expiresAt)) {
            otpStore.remove(email.toLowerCase());
            return false;
        }
        if (entry.code.equals(inputOtp)) {
            otpStore.remove(email.toLowerCase()); // Single-use OTP
            return true;
        }
        return false;
    }

    private record OtpEntry(String code, LocalDateTime expiresAt) {}
}