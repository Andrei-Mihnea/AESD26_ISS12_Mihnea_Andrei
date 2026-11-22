package com.example.vros.messaging;

public final class OptimizationQueues {
    private OptimizationQueues() {}
    public static final String EXCHANGE = "vros.optimization";
    public static final String REQUESTS = "vros.optimization.requests.v2";
    public static final String ACKNOWLEDGMENTS = "vros.optimization.acknowledgments";
    public static final String DEAD_LETTER_EXCHANGE = "vros.optimization.dlx";
    public static final String DEAD_LETTER_QUEUE = "vros.optimization.requests.dlq";
    public static final String DEAD_LETTER_KEY = "optimization.request.failed";
    public static final String REQUEST_KEY = "optimization.requested";
    public static final String ACK_KEY = "optimization.acknowledged";
}
