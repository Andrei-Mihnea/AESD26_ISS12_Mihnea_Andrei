package com.example.lab4.entity;

import jakarta.persistence.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "scenarios")
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "scenario_vehicles",
            joinColumns = @JoinColumn(name = "scenario_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicle_id"))
    private Set<Vehicle> vehicles = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "scenario_deliveries",
            joinColumns = @JoinColumn(name = "scenario_id"),
            inverseJoinColumns = @JoinColumn(name = "delivery_id"))
    private Set<Delivery> deliveries = new HashSet<>();

    protected Scenario() {}

    public Scenario(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Set<Vehicle> getVehicles() { return Collections.unmodifiableSet(vehicles); }
    public Set<Delivery> getDeliveries() { return Collections.unmodifiableSet(deliveries); }

    public void replaceMembers(Set<Vehicle> vehicles, Set<Delivery> deliveries) {
        this.vehicles.clear();
        this.vehicles.addAll(vehicles);
        this.deliveries.clear();
        this.deliveries.addAll(deliveries);
    }

    public void addVehicle(Vehicle vehicle) { vehicles.add(Objects.requireNonNull(vehicle)); }
    public void removeVehicle(Vehicle vehicle) { vehicles.remove(vehicle); }
    public void addDelivery(Delivery delivery) { deliveries.add(Objects.requireNonNull(delivery)); }
    public void removeDelivery(Delivery delivery) { deliveries.remove(delivery); }
}
