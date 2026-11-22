package com.delivery.tracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "areas")
public class Area {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String pincode;

    private String areaName;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;

    public Area() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getAreaName() { return areaName; }
    public void setAreaName(String areaName) { this.areaName = areaName; }

    public Zone getZone() { return zone; }
    public void setZone(Zone zone) { this.zone = zone; }
}