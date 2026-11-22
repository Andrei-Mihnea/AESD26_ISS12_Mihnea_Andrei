package com.example.vros.messaging;

import java.time.Instant;
import java.util.UUID;

/** Progress update for an optimization request, delivered independently from the final result. */
public record OptimizationProgressEvent(UUID requestId, Long scenarioId, String status,
                                        int completedDeliveries, int totalDeliveries, Instant occurredAt) {}
