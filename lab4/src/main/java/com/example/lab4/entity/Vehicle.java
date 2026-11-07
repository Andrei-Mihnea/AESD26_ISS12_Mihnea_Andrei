package com.example.lab4.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 120)
    private String name;
    @Column(nullable = false)
    private Integer capacity;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "depot", nullable = false)
    private Depot depot;
    @Column(nullable = false)
    private boolean available = true;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver", unique = true)
    private User driver;

    protected Vehicle() {}
    public Vehicle(String name, Integer capacity, Depot depot, boolean available, User driver) {
        if (driver != null) driver.requireRole(Role.DRIVER);
        this.name = name; this.capacity = capacity; this.depot = depot; this.available = available; this.driver = driver;
    }
    public void updateDetails(String name, Integer capacity, boolean available, Depot depot) {
        this.name = name;
        this.capacity = capacity;
        this.available = available;
        this.depot = depot;
        this.driver = null;
    }

    public void unassignDriver() { this.driver = null; }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Integer getCapacity() { return capacity; }
    public Depot getDepot() { return depot; }
    public boolean isAvailable() { return available; }
    public User getDriver() { return driver; }
}
