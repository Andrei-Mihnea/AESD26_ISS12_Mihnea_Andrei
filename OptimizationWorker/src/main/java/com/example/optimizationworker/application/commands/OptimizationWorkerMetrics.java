package com.example.optimizationworker.application.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.LongAdder;

@Component
public class OptimizationWorkerMetrics {
    private static final Logger log = LoggerFactory.getLogger(OptimizationWorkerMetrics.class);
    private final ThreadPoolTaskExecutor executor;
    private final LongAdder completed = new LongAdder();
    private final LongAdder failed = new LongAdder();
    private final LongAdder elapsedNanos = new LongAdder();
    private volatile long previousReportAt = System.nanoTime();

    public OptimizationWorkerMetrics(@Qualifier("optimizationTaskExecutor") ThreadPoolTaskExecutor executor) {
        this.executor = executor;
    }

    public void recordCompleted(long durationNanos) {
        completed.increment();
        elapsedNanos.add(durationNanos);
    }

    public void recordFailed(long durationNanos) {
        failed.increment();
        elapsedNanos.add(durationNanos);
    }

    @Scheduled(fixedDelayString = "${optimization.worker.metrics.interval-ms:10000}")
    public void reportThroughput() {
        long now = System.nanoTime();
        double intervalSeconds = (now - previousReportAt) / 1_000_000_000.0;
        previousReportAt = now;
        long completedInWindow = completed.sumThenReset();
        long failedInWindow = failed.sumThenReset();
        long elapsedInWindow = elapsedNanos.sumThenReset();
        double throughput = intervalSeconds <= 0 ? 0 : (completedInWindow + failedInWindow) / intervalSeconds;
        double averageMillis = completedInWindow + failedInWindow == 0 ? 0
                : elapsedInWindow / 1_000_000.0 / (completedInWindow + failedInWindow);
        log.info("Optimization throughput: completed={}, failed={}, jobsPerSecond={}, averageDurationMs={}, activeThreads={}, poolSize={}, queueDepth={}",
                completedInWindow, failedInWindow, round(throughput), round(averageMillis), executor.getActiveCount(),
                executor.getPoolSize(), executor.getThreadPoolExecutor().getQueue().size());
    }

    private static double round(double value) { return Math.round(value * 100.0) / 100.0; }
}
