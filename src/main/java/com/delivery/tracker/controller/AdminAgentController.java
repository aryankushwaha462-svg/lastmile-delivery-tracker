package com.delivery.tracker.controller;

import com.delivery.tracker.model.Agent;
import com.delivery.tracker.model.Role;
import com.delivery.tracker.model.User;
import com.delivery.tracker.model.Zone;
import com.delivery.tracker.repository.AgentRepository;
import com.delivery.tracker.repository.UserRepository;
import com.delivery.tracker.repository.ZoneRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/agents")
public class AdminAgentController {

    private final UserRepository userRepository;
    private final AgentRepository agentRepository;
    private final ZoneRepository zoneRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminAgentController(UserRepository userRepository,
                              AgentRepository agentRepository,
                              ZoneRepository zoneRepository,
                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.agentRepository = agentRepository;
        this.zoneRepository = zoneRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<?> createAgent(@RequestBody Map<String, Object> req) {
        String name = String.valueOf(req.getOrDefault("name", "")).trim();
        String email = String.valueOf(req.getOrDefault("email", "")).trim().toLowerCase();
        String password = String.valueOf(req.getOrDefault("password", "")).trim();
        String phone = String.valueOf(req.getOrDefault("phone", "")).trim();
        String vehicleType = String.valueOf(req.getOrDefault("vehicleType", "BIKE")).trim();
        String vehicleNumber = String.valueOf(req.getOrDefault("vehicleNumber", "")).trim().toUpperCase();
        Object zoneIdObj = req.get("zoneId");

        if (name.isBlank() || email.isBlank() || password.isBlank() || zoneIdObj == null || String.valueOf(zoneIdObj).isBlank()) {
            return ResponseEntity.badRequest().body("Name, email, password, and zone are required.");
        }

        Long zoneId = Long.valueOf(String.valueOf(zoneIdObj));
        Zone zone = zoneRepository.findById(zoneId).orElse(null);
        if (zone == null) {
            return ResponseEntity.badRequest().body("Selected zone does not exist.");
        }

        User user = userRepository.findByEmailIgnoreCase(email).orElseGet(User::new);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.AGENT);
        userRepository.save(user);

        Agent agent = agentRepository.findByEmailIgnoreCase(email).orElseGet(Agent::new);
        agent.setName(name);
        agent.setEmail(email);
        agent.setPhone(phone);
        agent.setVehicleType(vehicleType);
        agent.setVehicleNumber(vehicleNumber);
        agent.setCurrentZone(zone);
        if (agent.getIsAvailable() == null) {
            agent.setIsAvailable(true);
        }
        if (agent.getMaxCapacity() == null) {
            agent.setMaxCapacity(5);
        }
        agentRepository.save(agent);

        return ResponseEntity.ok("Agent account & profile created for " + email);
    }
}