package com.example.optimizationworker.infrastructure.messaging;

import com.example.optimizationworker.application.commands.AcceptOptimizationRequestCommandHandler;
import com.example.optimizationworker.application.commands.AcceptOptimizationRequestCommand;
import com.example.vros.messaging.OptimizationQueues;
import com.example.vros.messaging.OptimizationRequest;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class OptimizationRequestListener {
    private static final Logger log = LoggerFactory.getLogger(OptimizationRequestListener.class);
    private final AcceptOptimizationRequestCommandHandler handler;
    private final int maxAttempts;
    private final long retryDelayMillis;

    public OptimizationRequestListener(AcceptOptimizationRequestCommandHandler handler,
                                       @Value("${optimization.worker.retry.max-attempts:3}") int maxAttempts,
                                       @Value("${optimization.worker.retry.delay-ms:200}") long retryDelayMillis) {
        if (maxAttempts < 1 || retryDelayMillis < 0) throw new IllegalArgumentException("Invalid retry configuration");
        this.handler = handler;
        this.maxAttempts = maxAttempts;
        this.retryDelayMillis = retryDelayMillis;
    }

    @RabbitListener(queues = OptimizationQueues.REQUESTS)
    public void receive(OptimizationRequest request) {
        if (request == null) throw new AmqpRejectAndDontRequeueException("Invalid optimization request");
        var command = new AcceptOptimizationRequestCommand(request);
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                handler.handle(command);
                return;
            } catch (IllegalArgumentException exception) {
                throw new AmqpRejectAndDontRequeueException("Invalid optimization request", exception);
            } catch (RuntimeException exception) {
                if (attempt == maxAttempts) {
                    log.error("Optimization request exhausted retries: requestId={}, attempts={}",
                            request.requestId(), attempt, exception);
                    throw new AmqpRejectAndDontRequeueException("Optimization request failed after retries", exception);
                }
                log.warn("Optimization request failed; retrying: requestId={}, attempt={}/{}",
                        request.requestId(), attempt, maxAttempts);
                pauseBeforeRetry(attempt);
            }
        }
    }

    private void pauseBeforeRetry(int attempt) {
        if (retryDelayMillis == 0) return;
        try {
            Thread.sleep(Math.multiplyExact(retryDelayMillis, attempt));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AmqpRejectAndDontRequeueException("Interrupted while retrying optimization request", exception);
        }
    }
}
