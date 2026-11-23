package com.delivery.tracker.repository;

import com.delivery.tracker.model.Agent;
import com.delivery.tracker.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AgentRepository extends JpaRepository<Agent, Long> {
    List<Agent> findByCurrentZoneAndIsAvailableTrue(Zone currentZone);
    Optional<Agent> findByEmailIgnoreCase(String email);
}