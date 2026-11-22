package com.example.lab4.infrastructure.configuration;

import com.example.vros.messaging.OptimizationQueues;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Map;

@Configuration
public class OptimizationMessagingConfiguration {
    @Bean
    public Declarables optimizationTopology() {
        DirectExchange exchange = new DirectExchange(OptimizationQueues.EXCHANGE, true, false);
        DirectExchange deadLetterExchange = new DirectExchange(OptimizationQueues.DEAD_LETTER_EXCHANGE, true, false);
        Queue requests = new Queue(OptimizationQueues.REQUESTS, true, false, false, Map.of(
                "x-dead-letter-exchange", OptimizationQueues.DEAD_LETTER_EXCHANGE,
                "x-dead-letter-routing-key", OptimizationQueues.DEAD_LETTER_KEY));
        Queue acknowledgments = new Queue(OptimizationQueues.ACKNOWLEDGMENTS, true);
        Queue progress = new Queue(OptimizationQueues.PROGRESS_QUEUE, true);
        Queue deadLetters = new Queue(OptimizationQueues.DEAD_LETTER_QUEUE, true);
        return new Declarables(exchange, deadLetterExchange, requests, acknowledgments, progress, deadLetters,
                BindingBuilder.bind(requests).to(exchange).with(OptimizationQueues.REQUEST_KEY),
                BindingBuilder.bind(acknowledgments).to(exchange).with(OptimizationQueues.ACK_KEY),
                BindingBuilder.bind(progress).to(exchange).with(OptimizationQueues.PROGRESS_KEY),
                BindingBuilder.bind(deadLetters).to(deadLetterExchange).with(OptimizationQueues.DEAD_LETTER_KEY));
    }

    @Bean
    public JacksonJsonMessageConverter optimizationJsonConverter() {
        return new JacksonJsonMessageConverter("com.example.vros.messaging");
    }
}
