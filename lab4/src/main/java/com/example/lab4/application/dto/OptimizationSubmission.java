package com.example.lab4.application.dto;

import java.time.Instant;
import java.util.UUID;

public record OptimizationSubmission(UUID requestId, Long scenarioId, String status, Instant submittedAt) {}
