package com.delivery.tracker.service;

import com.delivery.tracker.model.Agent;
import com.delivery.tracker.model.Zone;
import com.delivery.tracker.repository.AgentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AutoAssignmentService {

    private final AgentRepository agentRepository;

    public AutoAssignmentService(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    public Optional<Agent> findNearestAvailableAgent(Zone pickupZone) {
        List<Agent> availableAgents = agentRepository.findByCurrentZoneAndIsAvailableTrue(pickupZone);
        return availableAgents.stream().findFirst();
    }
}