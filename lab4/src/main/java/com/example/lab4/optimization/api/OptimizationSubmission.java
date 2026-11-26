package com.example.lab4.optimization.api;

import java.time.Instant;
import java.util.UUID;

public record OptimizationSubmission(UUID requestId, Long scenarioId, String status, Instant submittedAt) {}
