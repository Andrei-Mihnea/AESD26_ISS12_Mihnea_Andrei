package com.example.lab4.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** The single application user entity; Role determines customer, driver, or administrator behavior. */
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "uk_users_role_email", columnNames = {"role", "email"}))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false, length = 40)
    private String phone;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;
    @Column(unique = true)
    private String username;
    @Column(name = "password_hash")
    private String passwordHash;
    @Column(nullable = false)
    private boolean enabled;
    @Column
    private String address;
    @Column
    private String coordinates;
    @OneToMany(mappedBy = "customer")
    private List<Delivery> deliveries = new ArrayList<>();
    @OneToOne(mappedBy = "driver", fetch = FetchType.LAZY)
    private Vehicle vehicle;

    protected User() {}

    public User(String name, String email, String phone) {
        this(name, email, phone, Role.ADMIN);
    }

    private User(String name, String email, String phone, Role role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = Objects.requireNonNull(role);
    }

    public static User customer(String name, String email, String phone, String address, String coordinates) {
        User user = new User(name, email, phone, Role.CUSTOMER);
        user.address = Objects.requireNonNull(address);
        user.coordinates = Objects.requireNonNull(coordinates);
        return user;
    }

    public static User driver(String name, String email, String phone) {
        return new User(name, email, phone, Role.DRIVER);
    }

    public void requireRole(Role expected) {
        if (role != expected) throw new IllegalArgumentException("Expected " + expected + " user, got " + role);
    }

    public void updateCustomerDetails(String name, String email, String phone, String address, String coordinates) {
        requireRole(Role.CUSTOMER);
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = Objects.requireNonNull(address);
        this.coordinates = Objects.requireNonNull(coordinates);
    }

    /** Called with a password already encoded by the application's PasswordEncoder. */
    public void configureLogin(String username, String encodedPassword) {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Username is required");
        if (encodedPassword == null || !encodedPassword.startsWith("{bcrypt}")) {
            throw new IllegalArgumentException("A BCrypt-encoded password is required");
        }
        this.username = username;
        this.passwordHash = encodedPassword;
        this.enabled = true;
    }

    public void disableLogin() { this.enabled = false; }
    public String getUsername() { return username; }
    @JsonIgnore
    public String getPasswordHash() { return passwordHash; }
    public boolean isEnabled() { return enabled; }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Role getRole() { return role; }
    public String getAddress() { return address; }
    public String getCoordinates() { return coordinates; }
    public List<Delivery> getDeliveries() { return deliveries; }
    public Vehicle getVehicle() { return vehicle; }
}
