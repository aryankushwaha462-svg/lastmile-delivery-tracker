package com.delivery.tracker.controller;

import com.delivery.tracker.model.Agent;
import com.delivery.tracker.model.Area;
import com.delivery.tracker.model.Zone;
import com.delivery.tracker.repository.AgentRepository;
import com.delivery.tracker.repository.AreaRepository;
import com.delivery.tracker.repository.ZoneRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ZoneRepository zoneRepository;
    private final AreaRepository areaRepository;
    private final AgentRepository agentRepository;

    public AdminController(ZoneRepository zoneRepository,
                           AreaRepository areaRepository,
                           AgentRepository agentRepository) {
        this.zoneRepository = zoneRepository;
        this.areaRepository = areaRepository;
        this.agentRepository = agentRepository;
    }

    @GetMapping("/zones")
    public ResponseEntity<List<Zone>> getAllZones() {
        return ResponseEntity.ok(zoneRepository.findAll());
    }

    @PostMapping("/zones")
    public ResponseEntity<Zone> createZone(@RequestBody Map<String, String> payload) {
        Zone zone = new Zone();
        zone.setName(payload.get("name").toUpperCase());
        return ResponseEntity.ok(zoneRepository.save(zone));
    }

    @GetMapping("/areas")
    public ResponseEntity<List<Area>> getAllAreas() {
        return ResponseEntity.ok(areaRepository.findAll());
    }

    @PostMapping("/areas")
    public ResponseEntity<Area> assignAreaToZone(@RequestBody Map<String, Object> payload) {
        String pincode = payload.get("pincode").toString();
        String areaName = payload.get("areaName").toString();
        Long zoneId = Long.valueOf(payload.get("zoneId").toString());

        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Zone not found: " + zoneId));

        Area area = areaRepository.findByPincode(pincode).orElse(new Area());
        area.setPincode(pincode);
        area.setAreaName(areaName);
        area.setZone(zone);
        return ResponseEntity.ok(areaRepository.save(area));
    }

    @GetMapping("/agents")
    public ResponseEntity<List<Agent>> getAllAgents() {
        return ResponseEntity.ok(agentRepository.findAll());
    }
}