package com.example.optimizationworker.infrastructure.messaging;

import com.example.optimizationworker.application.interfaces.OptimizationProgressPublisher;
import com.example.vros.messaging.OptimizationProgressEvent;
import com.example.vros.messaging.OptimizationQueues;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitOptimizationProgressPublisher implements OptimizationProgressPublisher {
    private final RabbitTemplate rabbit;

    public RabbitOptimizationProgressPublisher(RabbitTemplate rabbit) { this.rabbit = rabbit; }

    @Override
    public void publish(OptimizationProgressEvent event) {
        rabbit.convertAndSend(OptimizationQueues.EXCHANGE, OptimizationQueues.PROGRESS_KEY, event, message -> {
            message.getMessageProperties().setCorrelationId(event.requestId().toString());
            return message;
        });
    }
}
