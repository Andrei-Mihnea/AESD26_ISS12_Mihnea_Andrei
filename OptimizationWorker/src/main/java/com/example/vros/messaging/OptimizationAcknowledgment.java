package com.example.vros.messaging;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Correlated optimization result returned by the Worker. */
public record OptimizationAcknowledgment(UUID requestId, Long scenarioId, String status, Instant acknowledgedAt,
                                         List<VehicleRoute> routes) {
    public OptimizationAcknowledgment { routes = routes == null ? List.of() : List.copyOf(routes); }
    public record VehicleRoute(Long vehicleId, List<Long> deliveryIds, int totalDemand, double distanceKm) {
        public VehicleRoute { deliveryIds = List.copyOf(deliveryIds); }
    }
}
