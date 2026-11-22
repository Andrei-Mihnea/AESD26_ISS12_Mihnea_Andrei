package com.example.optimizationworker.application.commands;

import com.example.optimizationworker.application.interfaces.AcknowledgmentPublisher;
import com.example.optimizationworker.application.interfaces.OptimizationProgressPublisher;
import com.example.optimizationworker.domain.entities.OptimizationJob;
import com.example.vros.messaging.OptimizationAcknowledgment;
import com.example.vros.messaging.OptimizationRequest;
import com.example.vros.messaging.OptimizationProgressEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class AcceptOptimizationRequestCommandHandler {
    private static final Logger log = LoggerFactory.getLogger(AcceptOptimizationRequestCommandHandler.class);
    private final AcknowledgmentPublisher acknowledgments;
    private final GreedyVrpSolver solver;
    private final OptimizationProgressPublisher progress;

    public AcceptOptimizationRequestCommandHandler(AcknowledgmentPublisher acknowledgments, GreedyVrpSolver solver) {
        this(acknowledgments, solver, event -> {});
    }

    @Autowired
    public AcceptOptimizationRequestCommandHandler(AcknowledgmentPublisher acknowledgments, GreedyVrpSolver solver,
                                                   OptimizationProgressPublisher progress) {
        this.acknowledgments = acknowledgments;
        this.solver = solver;
        this.progress = progress;
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
        int totalDeliveries = job.deliveries().size();
        int[] completedDeliveries = {0};
        publishProgress(job, "RUNNING", 0, totalDeliveries);
        var routes = solver.solve(job, (completed, total) -> {
            completedDeliveries[0] = completed;
            publishProgress(job, "RUNNING", completed, total);
        });
        String status = routes.isPresent() ? "SOLVED" : "UNROUTABLE";
        acknowledgments.publish(new OptimizationAcknowledgment(job.requestId(), job.scenarioId(), status,
                Instant.now(), routes.orElseGet(java.util.List::of)));
        publishProgress(job, status, completedDeliveries[0], totalDeliveries);
        log.info("Optimization result published: requestId={}, scenarioId={}, status={}, routes={}",
                job.requestId(), job.scenarioId(), status, routes.map(java.util.List::size).orElse(0));
    }

    private void publishProgress(OptimizationJob job, String status, int completed, int total) {
        progress.publish(new OptimizationProgressEvent(job.requestId(), job.scenarioId(), status,
                completed, total, Instant.now()));
    }
}
