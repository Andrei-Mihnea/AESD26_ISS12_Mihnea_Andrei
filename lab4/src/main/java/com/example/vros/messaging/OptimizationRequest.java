package com.example.vros.messaging;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OptimizationRequest(UUID requestId, Long scenarioId, String scenarioName, Instant requestedAt,
                                  List<VehicleInput> vehicles, List<DeliveryInput> deliveries) {
    public record VehicleInput(Long id, int capacity, String depotCoordinates) {}
    public record DeliveryInput(Long id, int demand, String coordinates) {}
}