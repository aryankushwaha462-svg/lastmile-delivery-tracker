package com.delivery.tracker.config;

import com.delivery.tracker.model.*;
import com.delivery.tracker.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ZoneRepository zoneRepository;
    private final AreaRepository areaRepository;
    private final RateCardRepository rateCardRepository;
    private final AgentRepository agentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(ZoneRepository zoneRepository,
                           AreaRepository areaRepository,
                           RateCardRepository rateCardRepository,
                           AgentRepository agentRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.zoneRepository = zoneRepository;
        this.areaRepository = areaRepository;
        this.rateCardRepository = rateCardRepository;
        this.agentRepository = agentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (zoneRepository.count() > 0) {
            return;
        }

        // 1. Zones
        Zone northZone = saveZone("NORTH_ZONE");
        Zone southZone = saveZone("SOUTH_ZONE");
        Zone westZone = saveZone("WEST_ZONE");

        // 2. Areas
        saveArea("110001", "Delhi Central", northZone);
        saveArea("208001", "Kanpur Central", northZone);
        saveArea("400001", "Mumbai Fort", westZone);
        saveArea("560001", "Bengaluru GPO", southZone);

        // 3. Rate Cards (Intra-zone & Inter-zone for B2B and B2C)
        saveRateCard(northZone, northZone, "B2C", 10.0, 5.0);
        saveRateCard(northZone, northZone, "B2B", 7.0, 3.0);
        saveRateCard(northZone, westZone, "B2C", 15.0, 5.0);
        saveRateCard(northZone, westZone, "B2B", 10.0, 3.0);
        saveRateCard(northZone, southZone, "B2C", 18.0, 6.0);
        saveRateCard(northZone, southZone, "B2B", 12.0, 4.0);

        // 4. Delivery Agents
        saveAgent("John Doe", "john.agent@delivery.com", "9876543210", northZone);
        saveAgent("Priya Sharma", "priya.agent@delivery.com", "9876543211", northZone);

        // 5. Users
        seedUser("admin@delivery.com", "admin123", Role.ADMIN);
        seedUser("john.agent@delivery.com", "agent123", Role.AGENT);
        seedUser("priya.agent@delivery.com", "agent123", Role.AGENT);
        seedUser("user@delivery.com", "user123", Role.USER);

        System.out.println(">>> Sample seed data initialized successfully! <<<");
    }

    private Zone saveZone(String name) {
        Zone z = new Zone();
        z.setName(name);
        return zoneRepository.save(z);
    }

    private void saveArea(String pincode, String name, Zone zone) {
        Area a = new Area();
        a.setPincode(pincode);
        a.setAreaName(name);
        a.setZone(zone);
        areaRepository.save(a);
    }

    private void saveRateCard(Zone origin, Zone dest, String type, double baseRate, double cod) {
        RateCard rc = new RateCard();
        rc.setOriginZone(origin);
        rc.setDestinationZone(dest);
        rc.setOrderType(type);
        rc.setBaseRatePerKg(baseRate);
        rc.setCodSurcharge(cod);
        rateCardRepository.save(rc);
    }

    private void saveAgent(String name, String email, String phone, Zone zone) {
        Agent agent = new Agent();
        agent.setName(name);
        agent.setEmail(email);
        agent.setPhone(phone);
        agent.setIsAvailable(true);
        agent.setCurrentZone(zone);
        agentRepository.save(agent);
    }

    private void seedUser(String email, String rawPassword, Role role) {
        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setRole(role);
            userRepository.save(user);
        }
    }
}