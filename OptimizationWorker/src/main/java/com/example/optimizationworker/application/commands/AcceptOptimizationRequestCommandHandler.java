package com.example.optimizationworker.application.commands;

import com.example.optimizationworker.application.interfaces.AcknowledgmentPublisher;
import com.example.optimizationworker.domain.entities.OptimizationJob;
import com.example.vros.messaging.OptimizationAcknowledgment;
import com.example.vros.messaging.OptimizationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class AcceptOptimizationRequestCommandHandler {
    private static final Logger log = LoggerFactory.getLogger(AcceptOptimizationRequestCommandHandler.class);
    private final AcknowledgmentPublisher acknowledgments;
    private final GreedyVrpSolver solver;

    public AcceptOptimizationRequestCommandHandler(AcknowledgmentPublisher acknowledgments, GreedyVrpSolver solver) {
        this.acknowledgments = acknowledgments;
        this.solver = solver;
    }

    public void handle(AcceptOptimizationRequestCommand command) {
        OptimizationRequest request = command.request();
        if (request.requestId() == null || request.scenarioId() == null || request.vehicles() == null || request.deliveries() == null) {
            throw new IllegalArgumentException("Invalid optimization request");
        }
        var job = new OptimizationJob(request.requestId(), request.scenarioId(), request.scenarioName(),
                request.vehicles().stream().map(vehicle -> new OptimizationJob.Vehicle(vehicle.id(), vehicle.capacity(), vehicle.depotCoordinates())).toList(),
                request.deliveries().stream().map(delivery -> new OptimizationJob.Delivery(delivery.id(), delivery.demand(), delivery.coordinates())).toList());
        log.info("Optimization request received: requestId={}, scenarioId={}, vehicles={}, deliveries={}",
                job.requestId(), job.scenarioId(), job.vehicles().size(), job.deliveries().size());
        var routes = solver.solve(job);
        String status = routes.isPresent() ? "SOLVED" : "UNROUTABLE";
        acknowledgments.publish(new OptimizationAcknowledgment(job.requestId(), job.scenarioId(), status,
                Instant.now(), routes.orElseGet(java.util.List::of)));
        log.info("Optimization result published: requestId={}, scenarioId={}, status={}, routes={}",
                job.requestId(), job.scenarioId(), status, routes.map(java.util.List::size).orElse(0));
    }
}
