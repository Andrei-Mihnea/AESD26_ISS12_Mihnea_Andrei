package com.example.lab4.optimization.application;

import com.example.lab4.optimization.api.OptimizationSubmission;
import com.example.lab4.application.cqrs.Command;

public record SubmitScenarioOptimizationCommand(Long scenarioId) implements Command<OptimizationSubmission> {}
