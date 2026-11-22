package com.example.optimizationworker.domain.entities;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** The worker's independent optimization domain; no AMQP or API persistence dependencies. */
public record OptimizationJob(UUID requestId, long scenarioId, String name,
                              List<Vehicle> vehicles, List<Delivery> deliveries) {
    public OptimizationJob {
        Objects.requireNonNull(requestId, "Request ID is required");
        if (scenarioId <= 0) throw new IllegalArgumentException("Scenario ID must be positive");
        vehicles = List.copyOf(vehicles);
        deliveries = List.copyOf(deliveries);
    }
    public Acceptance accept(Instant now) { return new Acceptance(requestId, scenarioId, now); }
    public record Vehicle(Long id, int capacity, String depotCoordinates) {}
    public record Delivery(Long id, int demand, String coordinates) {}
    public record Acceptance(UUID requestId, long scenarioId, Instant acceptedAt) {}
}
