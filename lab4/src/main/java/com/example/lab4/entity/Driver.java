package com.example.lab4.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "drivers")
@AttributeOverrides({
        @AttributeOverride(name = "email", column = @Column(nullable = false, unique = true)),
        @AttributeOverride(name = "phone", column = @Column(nullable = false, unique = true, length = 40))
})
public class Driver extends Person {
    @OneToOne(mappedBy = "driver", fetch = FetchType.LAZY)
    private Vehicle vehicle;

    protected Driver() {}
    public Driver(String name, String email, String phone) { super(name, email, phone); }
    public Vehicle getVehicle() { return vehicle; }
}
