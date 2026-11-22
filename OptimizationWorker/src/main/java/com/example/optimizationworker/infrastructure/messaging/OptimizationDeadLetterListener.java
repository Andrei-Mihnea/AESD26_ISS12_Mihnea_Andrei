package com.example.optimizationworker.infrastructure.messaging;

import com.example.optimizationworker.application.interfaces.AcknowledgmentPublisher;
import com.example.optimizationworker.application.interfaces.OptimizationProgressPublisher;
import com.example.vros.messaging.OptimizationAcknowledgment;
import com.example.vros.messaging.OptimizationProgressEvent;
import com.example.vros.messaging.OptimizationQueues;
import com.example.vros.messaging.OptimizationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

@Component
public class OptimizationDeadLetterListener {
    private static final Logger log = LoggerFactory.getLogger(OptimizationDeadLetterListener.class);
    private final AcknowledgmentPublisher results;
    private final OptimizationProgressPublisher progress;

    public OptimizationDeadLetterListener(AcknowledgmentPublisher results) { this(results, event -> {}); }

    @Autowired
    public OptimizationDeadLetterListener(AcknowledgmentPublisher results, OptimizationProgressPublisher progress) {
        this.results = results;
        this.progress = progress;
    }

    @RabbitListener(queues = OptimizationQueues.DEAD_LETTER_QUEUE, containerFactory = "rabbitDlqListenerContainerFactory")
    public void receive(OptimizationRequest request, Message message) {
        var properties = message.getMessageProperties();
        log.error("Optimization request sent to DLQ: messageId={}, correlationId={}, reason={}, xDeath={}",
                properties.getMessageId(), properties.getCorrelationId(),
                properties.getHeaders().get("x-exception-message"), properties.getHeaders().get("x-death"));
        if (request != null && request.requestId() != null && request.scenarioId() != null) {
            int total = request.deliveries() == null ? 0 : request.deliveries().size();
            results.publish(new OptimizationAcknowledgment(request.requestId(), request.scenarioId(), "FAILED",
                    Instant.now(), List.of()));
            progress.publish(new OptimizationProgressEvent(request.requestId(), request.scenarioId(),
                    "FAILED", 0, total, Instant.now()));
        }
    }
}
