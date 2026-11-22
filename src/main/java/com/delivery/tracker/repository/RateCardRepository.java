package com.delivery.tracker.repository;

import com.delivery.tracker.model.RateCard;
import com.delivery.tracker.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RateCardRepository extends JpaRepository<RateCard, Long> {
    Optional<RateCard> findByOriginZoneAndDestinationZoneAndOrderType(Zone origin, Zone destination, String orderType);
}