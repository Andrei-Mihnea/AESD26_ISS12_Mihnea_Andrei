package com.example.optimizationworker.infrastructure.messaging;

import com.example.optimizationworker.application.interfaces.AcknowledgmentPublisher;
import com.example.vros.messaging.OptimizationAcknowledgment;
import com.example.vros.messaging.OptimizationQueues;
import com.example.vros.messaging.OptimizationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class OptimizationDeadLetterListener {
    private static final Logger log = LoggerFactory.getLogger(OptimizationDeadLetterListener.class);
    private final AcknowledgmentPublisher results;

    public OptimizationDeadLetterListener(AcknowledgmentPublisher results) { this.results = results; }

    @RabbitListener(queues = OptimizationQueues.DEAD_LETTER_QUEUE)
    public void receive(OptimizationRequest request, Message message) {
        var properties = message.getMessageProperties();
        log.error("Optimization request sent to DLQ: messageId={}, correlationId={}, reason={}, xDeath={}",
                properties.getMessageId(), properties.getCorrelationId(),
                properties.getHeaders().get("x-exception-message"), properties.getHeaders().get("x-death"));
        if (request != null && request.requestId() != null && request.scenarioId() != null) {
            results.publish(new OptimizationAcknowledgment(request.requestId(), request.scenarioId(), "FAILED",
                    Instant.now(), List.of()));
        }
    }
}
