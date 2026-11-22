package com.example.optimizationworker.application.interfaces;

import com.example.vros.messaging.OptimizationProgressEvent;

public interface OptimizationProgressPublisher {
    void publish(OptimizationProgressEvent event);
}
