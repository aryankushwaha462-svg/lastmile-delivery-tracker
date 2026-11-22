package com.delivery.tracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "agents")
public class Agent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private Boolean isAvailable;

    @ManyToOne
    @JoinColumn(name = "current_zone_id")
    private Zone currentZone;

    public Agent() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public Zone getCurrentZone() { return currentZone; }
    public void setCurrentZone(Zone currentZone) { this.currentZone = currentZone; }
}