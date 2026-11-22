package com.delivery.tracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rate_cards")
public class RateCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "origin_zone_id")
    private Zone originZone;

    @ManyToOne
    @JoinColumn(name = "destination_zone_id")
    private Zone destinationZone;

    private String orderType; // B2B / B2C
    private Double baseRatePerKg;
    private Double codSurcharge;

    public RateCard() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Zone getOriginZone() { return originZone; }
    public void setOriginZone(Zone originZone) { this.originZone = originZone; }

    public Zone getDestinationZone() { return destinationZone; }
    public void setDestinationZone(Zone destinationZone) { this.destinationZone = destinationZone; }

    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }

    public Double getBaseRatePerKg() { return baseRatePerKg; }
    public void setBaseRatePerKg(Double baseRatePerKg) { this.baseRatePerKg = baseRatePerKg; }

    public Double getCodSurcharge() { return codSurcharge; }
    public void setCodSurcharge(Double codSurcharge) { this.codSurcharge = codSurcharge; }
}