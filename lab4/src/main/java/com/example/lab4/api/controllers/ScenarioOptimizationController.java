package com.example.lab4.api.controllers;

import com.example.lab4.application.commands.scenarios.SubmitScenarioOptimizationCommand;
import com.example.lab4.application.cqrs.CommandBus;
import com.example.lab4.application.dto.OptimizationSubmission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/scenarios", "/api/v1/scenarios", "/api/v2/scenarios"})
@Tag(name = "Scenarios")
public class ScenarioOptimizationController {
    private final CommandBus commands;
    public ScenarioOptimizationController(CommandBus commands) { this.commands = commands; }

    @PostMapping("/{id}/solve")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DRIVER')")
    @Operation(summary = "Request scenario optimization", description = "Publishes the selected vehicles and deliveries to RabbitMQ. Returns 202 with a request ID after broker confirmation. Poll /api/optimization-jobs/{requestId}/status and retrieve /solution when complete.")
    public OptimizationSubmission solve(@PathVariable Long id) { return commands.execute(new SubmitScenarioOptimizationCommand(id)); }
}
