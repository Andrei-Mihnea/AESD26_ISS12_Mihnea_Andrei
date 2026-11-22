package com.example.lab4.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OptimizationJobSubmissionRequest(@NotNull @Positive Long scenarioId) {}
