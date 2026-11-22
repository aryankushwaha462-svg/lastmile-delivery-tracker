package com.delivery.tracker.service;

import com.delivery.tracker.dto.ChargeCalculationDto;
import com.delivery.tracker.dto.OrderRequestDto;
import com.delivery.tracker.model.Area;
import com.delivery.tracker.model.RateCard;
import com.delivery.tracker.model.Zone;
import com.delivery.tracker.repository.AreaRepository;
import com.delivery.tracker.repository.RateCardRepository;
import com.delivery.tracker.repository.ZoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RateCalculationService {

    private final AreaRepository areaRepository;
    private final RateCardRepository rateCardRepository;
    private final ZoneRepository zoneRepository;

    public RateCalculationService(AreaRepository areaRepository,
                                  RateCardRepository rateCardRepository,
                                  ZoneRepository zoneRepository) {
        this.areaRepository = areaRepository;
        this.rateCardRepository = rateCardRepository;
        this.zoneRepository = zoneRepository;
    }

    public ChargeCalculationDto calculateCharge(OrderRequestDto dto) {
        Area pickupArea = resolveOrCreateArea(dto.getPickupPincode());
        Area dropArea = resolveOrCreateArea(dto.getDropPincode());

        String orderType = dto.getOrderType().toUpperCase();
        RateCard rateCard = rateCardRepository
                .findByOriginZoneAndDestinationZoneAndOrderType(
                        pickupArea.getZone(), dropArea.getZone(), orderType)
                .orElseGet(() -> defaultRateCard(orderType));

        double volumetricWeight = (dto.getLength() * dto.getBreadth() * dto.getHeight()) / 5000.0;
        double chargeableWeight = Math.max(dto.getActualWeight(), volumetricWeight);
        double baseCharge = chargeableWeight * rateCard.getBaseRatePerKg();
        double codSurcharge = "COD".equalsIgnoreCase(dto.getPaymentType()) ? rateCard.getCodSurcharge() : 0.0;
        double totalCharge = Math.round((baseCharge + codSurcharge) * 100.0) / 100.0;

        return ChargeCalculationDto.builder()
                .volumetricWeight(Math.round(volumetricWeight * 100.0) / 100.0)
                .chargeableWeight(Math.round(chargeableWeight * 100.0) / 100.0)
                .baseCharge(Math.round(baseCharge * 100.0) / 100.0)
                .codSurcharge(codSurcharge)
                .totalCharge(totalCharge)
                .pickupZoneName(pickupArea.getZone().getName())
                .dropZoneName(dropArea.getZone().getName())
                .build();
    }

    private Area resolveOrCreateArea(String pincode) {
        return areaRepository.findByPincode(pincode).orElseGet(() -> {
            List<Zone> zones = zoneRepository.findAll();
            Zone assignedZone = zones.stream()
                    .filter(z -> z.getName().equals(mapZoneName(pincode)))
                    .findFirst()
                    .orElse(zones.get(0));

            Area newArea = new Area();
            newArea.setPincode(pincode);
            newArea.setAreaName("Area " + pincode);
            newArea.setZone(assignedZone);
            return areaRepository.save(newArea);
        });
    }

    private String mapZoneName(String pincode) {
        if (pincode != null && !pincode.isEmpty()) {
            char first = pincode.charAt(0);
            if (first == '1' || first == '2') return "NORTH_ZONE";
            if (first == '3' || first == '4') return "WEST_ZONE";
            if (first == '5' || first == '6') return "SOUTH_ZONE";
        }
        return "NORTH_ZONE";
    }

    private RateCard defaultRateCard(String orderType) {
        RateCard fallback = new RateCard();
        fallback.setOrderType(orderType);
        fallback.setBaseRatePerKg("B2B".equals(orderType) ? 10.0 : 15.0);
        fallback.setCodSurcharge("B2B".equals(orderType) ? 3.0 : 5.0);
        return fallback;
    }
}