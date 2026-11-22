package com.delivery.tracker.controller;

import com.delivery.tracker.model.RateCard;
import com.delivery.tracker.model.Zone;
import com.delivery.tracker.repository.RateCardRepository;
import com.delivery.tracker.repository.ZoneRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rate-cards")
public class RateCardController {

    private final RateCardRepository rateCardRepository;
    private final ZoneRepository zoneRepository;

    public RateCardController(RateCardRepository rateCardRepository, ZoneRepository zoneRepository) {
        this.rateCardRepository = rateCardRepository;
        this.zoneRepository = zoneRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RateCard> createOrUpdateRateCard(@RequestBody Map<String, Object> payload) {
        Long originId = Long.valueOf(payload.get("originZoneId").toString());
        Long destId = Long.valueOf(payload.get("destinationZoneId").toString());
        String orderType = payload.get("orderType").toString().toUpperCase();
        Double baseRate = Double.valueOf(payload.get("baseRatePerKg").toString());
        Double codSurcharge = Double.valueOf(payload.get("codSurcharge").toString());

        Zone origin = zoneRepository.findById(originId)
                .orElseThrow(() -> new RuntimeException("Origin zone not found"));
        Zone dest = zoneRepository.findById(destId)
                .orElseThrow(() -> new RuntimeException("Destination zone not found"));

        RateCard rateCard = rateCardRepository
                .findByOriginZoneAndDestinationZoneAndOrderType(origin, dest, orderType)
                .orElse(new RateCard());

        rateCard.setOriginZone(origin);
        rateCard.setDestinationZone(dest);
        rateCard.setOrderType(orderType);
        rateCard.setBaseRatePerKg(baseRate);
        rateCard.setCodSurcharge(codSurcharge);

        return ResponseEntity.ok(rateCardRepository.save(rateCard));
    }

    @GetMapping
    public ResponseEntity<List<RateCard>> getAllRateCards() {
        return ResponseEntity.ok(rateCardRepository.findAll());
    }
}