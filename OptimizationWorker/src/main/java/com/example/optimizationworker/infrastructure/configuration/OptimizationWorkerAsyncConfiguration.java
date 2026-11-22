package com.example.optimizationworker.infrastructure.configuration;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@EnableScheduling
public class OptimizationWorkerAsyncConfiguration {
    @Bean(name = "optimizationTaskExecutor")
    public ThreadPoolTaskExecutor optimizationTaskExecutor(
            @Value("${optimization.worker.pool-size:4}") int poolSize,
            @Value("${optimization.worker.listener.prefetch:1}") int prefetch) {
        if (poolSize < 1 || prefetch < 1) throw new IllegalArgumentException("Worker pool size and prefetch must be positive");
        int queueCapacity = Math.multiplyExact(poolSize, prefetch - 1);
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        // At most one active and (prefetch - 1) queued task per consumer; further work stays in RabbitMQ.
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("optimization-worker-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        return executor;
    }

    @Bean(name = "rabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter messageConverter,
            @Value("${optimization.worker.pool-size:4}") int poolSize,
            @Value("${optimization.worker.listener.prefetch:1}") int prefetch) {
        if (poolSize < 1 || prefetch < 1) throw new IllegalArgumentException("Listener concurrency and prefetch must be positive");
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setConcurrentConsumers(poolSize);
        factory.setMaxConcurrentConsumers(poolSize);
        factory.setPrefetchCount(prefetch);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    @Bean(name = "rabbitDlqListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitDlqListenerContainerFactory(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
