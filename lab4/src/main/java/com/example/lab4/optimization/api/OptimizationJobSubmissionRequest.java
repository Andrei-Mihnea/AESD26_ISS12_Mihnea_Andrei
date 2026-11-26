package com.example.lab4.optimization.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OptimizationJobSubmissionRequest(@NotNull @Positive Long scenarioId) {}
