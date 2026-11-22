package com.delivery.tracker.controller;

import com.delivery.tracker.dto.RegisterDto;
import com.delivery.tracker.model.Role;
import com.delivery.tracker.model.User;
import com.delivery.tracker.repository.UserRepository;
import com.delivery.tracker.service.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          OtpService otpService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
    }

    @GetMapping("/auth/me")
    public ResponseEntity> getCurrentUser(Authentication auth) {
        String role = auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("USER");
        return ResponseEntity.ok(Map.of("email", auth.getName(), "role", role));
    }

    @PostMapping("/auth/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("Email is required!");
        }

        String normalizedEmail = email.trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("An account with this email already exists. Please sign in.");
        }

        try {
            otpService.generateAndSendOtp(normalizedEmail);
        } catch (MailException e) {
            logger.warn("Failed to send OTP email", e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Unable to send OTP email. Check email configuration and try again.");
        }
        return ResponseEntity.ok("OTP sent to " + normalizedEmail);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterDto registerDto) {
        if (registerDto.getEmail() == null || registerDto.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Email is required!");
        }

        String normalizedEmail = registerDto.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("An account with this email already exists. Please sign in.");
        }
        if (registerDto.getOtp() == null || !otpService.verifyOtp(normalizedEmail, registerDto.getOtp())) {
            return ResponseEntity.badRequest().body("Invalid or expired OTP!");
        }

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/create-agent")
    public ResponseEntity<String> createAgent(@RequestBody RegisterDto registerDto) {
        if (registerDto.getEmail() == null || registerDto.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Email is required!");
        }

        String normalizedEmail = registerDto.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already taken!");
        }

        User agent = new User();
        agent.setEmail(normalizedEmail);
        agent.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        agent.setRole(Role.AGENT);

        userRepository.save(agent);
        return ResponseEntity.ok("Agent account created successfully");
    }
}
