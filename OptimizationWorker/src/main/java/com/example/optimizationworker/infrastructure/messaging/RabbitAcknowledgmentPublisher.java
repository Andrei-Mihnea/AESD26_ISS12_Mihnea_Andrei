package com.example.optimizationworker.infrastructure.messaging;

import com.example.optimizationworker.application.interfaces.AcknowledgmentPublisher;
import com.example.vros.messaging.OptimizationAcknowledgment;
import com.example.vros.messaging.OptimizationQueues;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitAcknowledgmentPublisher implements AcknowledgmentPublisher {
    private final RabbitTemplate rabbit;
    public RabbitAcknowledgmentPublisher(RabbitTemplate rabbit) { this.rabbit = rabbit; }
    @Override public void publish(OptimizationAcknowledgment acknowledgment) {
        rabbit.invoke(operations -> {
            operations.convertAndSend(OptimizationQueues.EXCHANGE, OptimizationQueues.ACK_KEY, acknowledgment, message -> {
                message.getMessageProperties().setCorrelationId(acknowledgment.requestId().toString());
                return message;
            });
            operations.waitForConfirmsOrDie(5000);
            return null;
        });
    }
}
