package com.example.optimizationworker.infrastructure.messaging;

import com.example.optimizationworker.application.commands.OptimizationRequestProcessor;
import com.example.vros.messaging.OptimizationQueues;
import com.example.vros.messaging.OptimizationRequest;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class OptimizationRequestListener {
    private final OptimizationRequestProcessor processor;

    public OptimizationRequestListener(OptimizationRequestProcessor processor) { this.processor = processor; }

    @RabbitListener(queues = OptimizationQueues.REQUESTS,
            concurrency = "${optimization.worker.pool-size:4}", ackMode = "MANUAL")
    public CompletableFuture<Void> receive(OptimizationRequest request) {
        if (request == null) {
            return CompletableFuture.failedFuture(new AmqpRejectAndDontRequeueException("Invalid optimization request"));
        }
        try {
            // Keep the delivery unacknowledged until the background computation completes.
            return processor.process(request);
        } catch (TaskRejectedException exception) {
            return CompletableFuture.failedFuture(new AmqpRejectAndDontRequeueException(
                    "Optimization worker thread pool is full", exception));
        }
    }
}
