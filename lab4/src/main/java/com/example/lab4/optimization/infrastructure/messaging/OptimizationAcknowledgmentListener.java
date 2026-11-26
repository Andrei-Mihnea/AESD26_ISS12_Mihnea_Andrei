package com.example.lab4.optimization.infrastructure.messaging;

import com.example.vros.messaging.OptimizationAcknowledgment;
import com.example.vros.messaging.OptimizationQueues;
import com.example.lab4.optimization.application.OptimizationJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OptimizationAcknowledgmentListener {
    private static final Logger log = LoggerFactory.getLogger(OptimizationAcknowledgmentListener.class);
    private final OptimizationJobService jobs;

    public OptimizationAcknowledgmentListener(OptimizationJobService jobs) { this.jobs = jobs; }

    @RabbitListener(queues = OptimizationQueues.ACKNOWLEDGMENTS)
    public void receive(OptimizationAcknowledgment acknowledgment) {
        if (!jobs.recordResult(acknowledgment)) {
            log.warn("Ignoring result for unknown optimization job: requestId={}", acknowledgment.requestId());
            return;
        }
        log.info("Optimization result stored: requestId={}, scenarioId={}, status={}, routes={}",
                acknowledgment.requestId(), acknowledgment.scenarioId(), acknowledgment.status(),
                acknowledgment.routes() == null ? 0 : acknowledgment.routes().size());
    }
}
