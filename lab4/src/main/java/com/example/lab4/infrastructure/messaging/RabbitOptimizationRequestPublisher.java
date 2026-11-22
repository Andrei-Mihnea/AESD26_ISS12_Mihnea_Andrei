package com.example.lab4.infrastructure.messaging;

import com.example.lab4.application.interfaces.OptimizationRequestPublisher;
import com.example.vros.messaging.OptimizationRequest;
import com.example.vros.messaging.OptimizationQueues;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitOptimizationRequestPublisher implements OptimizationRequestPublisher {
    private final RabbitTemplate rabbit;
    public RabbitOptimizationRequestPublisher(RabbitTemplate rabbit) { this.rabbit = rabbit; }
    @Override public void publish(OptimizationRequest request) {
        rabbit.invoke(operations -> {
            operations.convertAndSend(OptimizationQueues.EXCHANGE, OptimizationQueues.REQUEST_KEY, request, message -> {
                message.getMessageProperties().setMessageId(request.requestId().toString());
                message.getMessageProperties().setCorrelationId(request.requestId().toString());
                return message;
            });
            operations.waitForConfirmsOrDie(5000);
            return null;
        });
    }
}
