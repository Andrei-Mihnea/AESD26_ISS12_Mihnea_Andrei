package com.example.lab4.application.commands.scenarios;

import com.example.lab4.application.dto.OptimizationSubmission;
import com.example.lab4.application.cqrs.Command;

public record SubmitScenarioOptimizationCommand(Long scenarioId) implements Command<OptimizationSubmission> {}
