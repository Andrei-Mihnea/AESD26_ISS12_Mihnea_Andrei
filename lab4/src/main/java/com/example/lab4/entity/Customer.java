package com.example.lab4.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@AttributeOverride(name = "email", column = @Column(nullable = false, unique = true))
public class Customer extends Person {
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private String coordinates;
    @OneToMany(mappedBy = "customer")
    private List<Delivery> deliveries = new ArrayList<>();

    protected Customer() {}
    public Customer(String name, String email, String phone, String address, String coordinates) {
        super(name, email, phone);
        this.address = address; this.coordinates = coordinates;
    }
    public String getAddress() { return address; }
    public String getCoordinates() { return coordinates; }
    public List<Delivery> getDeliveries() { return deliveries; }
}
