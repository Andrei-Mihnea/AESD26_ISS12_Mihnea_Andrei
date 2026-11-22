package com.example.optimizationworker.application.interfaces;

import com.example.vros.messaging.OptimizationAcknowledgment;
public interface AcknowledgmentPublisher { void publish(OptimizationAcknowledgment acknowledgment); }
