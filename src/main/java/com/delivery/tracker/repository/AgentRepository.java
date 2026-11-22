package com.delivery.tracker.repository;

import com.delivery.tracker.model.Agent;
import com.delivery.tracker.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AgentRepository extends JpaRepository<Agent, Long> {
    List<Agent> findByCurrentZoneAndIsAvailableTrue(Zone currentZone);
}