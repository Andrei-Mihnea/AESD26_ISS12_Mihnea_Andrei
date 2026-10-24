package com.example.lab4.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "depots")
public class Depot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 120)
    private String name;
    @Column(nullable = false)
    private String coordinates;
    @OneToMany(mappedBy = "depot")
    private List<Vehicle> vehicles = new ArrayList<>();

    protected Depot() {}
    public Depot(String name, String coordinates) { this.name = name; this.coordinates = coordinates; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCoordinates() { return coordinates; }
    public List<Vehicle> getVehicles() { return vehicles; }
}
