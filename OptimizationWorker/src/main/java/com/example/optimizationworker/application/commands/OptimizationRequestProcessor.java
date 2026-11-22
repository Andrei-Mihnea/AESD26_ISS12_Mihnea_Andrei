package com.example.optimizationworker.application.commands;

import com.example.vros.messaging.OptimizationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class OptimizationRequestProcessor {
    private static final Logger log = LoggerFactory.getLogger(OptimizationRequestProcessor.class);
    private final AcceptOptimizationRequestCommandHandler handler;
    private final OptimizationWorkerMetrics metrics;
    private final int maxAttempts;
    private final long retryDelayMillis;

    public OptimizationRequestProcessor(AcceptOptimizationRequestCommandHandler handler,
                                        OptimizationWorkerMetrics metrics,
                                        @org.springframework.beans.factory.annotation.Value("${optimization.worker.retry.max-attempts:3}") int maxAttempts,
                                        @org.springframework.beans.factory.annotation.Value("${optimization.worker.retry.delay-ms:200}") long retryDelayMillis) {
        if (maxAttempts < 1 || retryDelayMillis < 0) throw new IllegalArgumentException("Invalid retry configuration");
        this.handler = handler;
        this.metrics = metrics;
        this.maxAttempts = maxAttempts;
        this.retryDelayMillis = retryDelayMillis;
    }

    @Async("optimizationTaskExecutor")
    public CompletableFuture<Void> process(OptimizationRequest request) {
        long startedAt = System.nanoTime();
        try {
            processWithRetries(request);
            metrics.recordCompleted(System.nanoTime() - startedAt);
            return CompletableFuture.completedFuture(null);
        } catch (RuntimeException exception) {
            metrics.recordFailed(System.nanoTime() - startedAt);
            return CompletableFuture.failedFuture(exception);
        }
    }

    void processWithRetries(OptimizationRequest request) {
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
