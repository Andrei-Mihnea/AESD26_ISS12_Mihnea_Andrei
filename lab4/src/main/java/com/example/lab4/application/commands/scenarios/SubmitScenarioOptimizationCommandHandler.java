package com.example.lab4.application.commands.scenarios;

import com.example.lab4.application.dto.OptimizationSubmission;
import com.example.lab4.application.exceptions.ResourceNotFoundException;
import com.example.lab4.application.interfaces.ScenarioRepository;
import com.example.vros.messaging.OptimizationRequest;
import com.example.lab4.application.interfaces.OptimizationRequestPublisher;
import com.example.lab4.application.services.OptimizationJobService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Comparator;
import java.util.UUID;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class SubmitScenarioOptimizationCommandHandler implements CommandHandler<SubmitScenarioOptimizationCommand, OptimizationSubmission> {
    private final ScenarioRepository scenarios;
    private final OptimizationRequestPublisher publisher;
    private final OptimizationJobService jobs;

    public SubmitScenarioOptimizationCommandHandler(ScenarioRepository scenarios, OptimizationRequestPublisher publisher,
                                                    OptimizationJobService jobs) {
        this.scenarios = scenarios;
        this.publisher = publisher;
        this.jobs = jobs;
    }

    @Override public Class<SubmitScenarioOptimizationCommand> messageType() { return SubmitScenarioOptimizationCommand.class; }

    @Transactional(readOnly = true)
    @Override
    public OptimizationSubmission handle(SubmitScenarioOptimizationCommand command) {
        Long scenarioId = command.scenarioId();
        var scenario = scenarios.findById(scenarioId).orElseThrow(() -> new ResourceNotFoundException("Scenario", scenarioId));
        UUID requestId = UUID.randomUUID();
        Instant now = Instant.now();
        var vehicles = scenario.getVehicles().stream().sorted(Comparator.comparing(vehicle -> vehicle.getId()))
                .map(vehicle -> new OptimizationRequest.VehicleInput(vehicle.getId(), vehicle.getCapacity(), vehicle.getDepot().getCoordinates())).toList();
        var deliveries = scenario.getDeliveries().stream().sorted(Comparator.comparing(delivery -> delivery.getId()))
                .map(delivery -> new OptimizationRequest.DeliveryInput(delivery.getId(), delivery.getQuantity(), delivery.getCustomer().getCoordinates())).toList();
        var event = new OptimizationRequest(requestId, scenario.getId(), scenario.getName(), now, vehicles, deliveries);
        // Commit the pollable job row before publishing so a fast Worker result can always find it.
        jobs.create(requestId, scenarioId, now);
        try {
            publisher.publish(event);
        } catch (RuntimeException exception) {
            jobs.markFailed(requestId);
            throw exception;
        }
        return new OptimizationSubmission(requestId, scenarioId, "SUBMITTED", now);
    }

}
