package com.example.optimizationworker.application.commands;

import com.example.vros.messaging.OptimizationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OptimizationRequestProcessorAsyncTest {
    @Test
    @Timeout(5)
    void asyncInvocationReturnsWhileTheOptimizationIsStillRunning() throws Exception {
        var started = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        try (var context = new AnnotationConfigApplicationContext()) {
            context.registerBean("startedLatch", CountDownLatch.class, () -> started);
            context.registerBean("releaseLatch", CountDownLatch.class, () -> release);
            context.register(TestConfiguration.class);
            context.refresh();
            var processor = context.getBean(OptimizationRequestProcessor.class);
            var future = processor.process(new OptimizationRequest(UUID.randomUUID(), 1L, "async", Instant.now(), List.of(), List.of()));

            assertTrue(started.await(2, TimeUnit.SECONDS));
            assertFalse(future.isDone());
            release.countDown();
            future.get(2, TimeUnit.SECONDS);
        } finally {
            release.countDown();
        }
    }

    @Configuration
    @EnableAsync
    static class TestConfiguration {
        @Bean("optimizationTaskExecutor") ThreadPoolTaskExecutor executor() {
            var executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(1);
            executor.setMaxPoolSize(1);
            executor.setQueueCapacity(0);
            executor.initialize();
            return executor;
        }
        @Bean OptimizationWorkerMetrics metrics(@Qualifier("optimizationTaskExecutor") ThreadPoolTaskExecutor executor) {
            return new OptimizationWorkerMetrics(executor);
        }
        @Bean AcceptOptimizationRequestCommandHandler handler(CountDownLatch startedLatch, CountDownLatch releaseLatch) {
            return new AcceptOptimizationRequestCommandHandler(ack -> {}, new GreedyVrpSolver()) {
                @Override public void handle(AcceptOptimizationRequestCommand command) {
                    startedLatch.countDown();
                    try { releaseLatch.await(); }
                    catch (InterruptedException exception) { Thread.currentThread().interrupt(); throw new IllegalStateException(exception); }
                }
            };
        }
        @Bean OptimizationRequestProcessor processor(AcceptOptimizationRequestCommandHandler handler,
                                                     OptimizationWorkerMetrics metrics) {
            return new OptimizationRequestProcessor(handler, metrics, 1, 0);
        }
    }
}
