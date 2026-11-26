package com.example.lab4.optimization.infrastructure.messaging;

import com.example.vros.messaging.OptimizationProgressEvent;
import com.example.vros.messaging.OptimizationQueues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OptimizationProgressListener {
    private static final Logger log = LoggerFactory.getLogger(OptimizationProgressListener.class);
    private final SimpMessagingTemplate websocket;

    public OptimizationProgressListener(SimpMessagingTemplate websocket) { this.websocket = websocket; }

    @RabbitListener(queues = OptimizationQueues.PROGRESS_QUEUE)
    public void receive(OptimizationProgressEvent event) {
        if (event == null || event.requestId() == null) {
            log.warn("Ignoring malformed optimization progress event");
            return;
        }
        websocket.convertAndSend("/topic/optimization/" + event.requestId(), event);
    }
}
