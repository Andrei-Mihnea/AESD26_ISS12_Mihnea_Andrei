package com.example.lab4.optimization.api;

import com.example.vros.messaging.OptimizationAcknowledgment;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OptimizationSolutionResponse(UUID requestId, Long scenarioId, String status, Instant completedAt,
                                           List<OptimizationAcknowledgment.VehicleRoute> routes) {
    public OptimizationSolutionResponse { routes = List.copyOf(routes); }
}
