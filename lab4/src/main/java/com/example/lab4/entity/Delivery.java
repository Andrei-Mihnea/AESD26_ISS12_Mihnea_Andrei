package com.example.lab4.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "deliveries")
public class Delivery {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer", nullable = false)
    private User customer;
    @Column(nullable = false)
    private Integer quantity;
    @Column(columnDefinition = "TEXT")
    private String notes;
    @Column(name = "\"timestamp\"", nullable = false)
    private OffsetDateTime timestamp;

    protected Delivery() {}
    public Delivery(User customer, Integer quantity, String notes, OffsetDateTime timestamp) {
        customer.requireRole(Role.CUSTOMER);
        this.customer = customer; this.quantity = quantity; this.notes = notes; this.timestamp = timestamp;
    }
    public void updateDetails(User customer, Integer quantity, String notes, OffsetDateTime timestamp) {
        customer.requireRole(Role.CUSTOMER);
        this.customer = customer;
        this.quantity = quantity;
        this.notes = notes;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public User getCustomer() { return customer; }
    public Integer getQuantity() { return quantity; }
    public String getNotes() { return notes; }
    public OffsetDateTime getTimestamp() { return timestamp; }
}
