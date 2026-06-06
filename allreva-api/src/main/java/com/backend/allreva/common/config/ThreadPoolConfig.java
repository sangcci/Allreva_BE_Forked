package com.backend.allreva.common.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class ThreadPoolConfig {

    private static final int AWAIT_SECONDS = 20;

    @Value("${thread-pool.task.core-size}")
    private int taskCoreSize;

    @Value("${thread-pool.task.max-size}")
    private int taskMaxSize;

    @Value("${thread-pool.task.queue-capacity}")
    private int taskQueueCapacity;

    @Value("${thread-pool.notification.core-size}")
    private int notificationCoreSize;

    @Value("${thread-pool.notification.max-size}")
    private int notificationMaxSize;

    @Value("${thread-pool.notification.queue-capacity}")
    private int notificationQueueCapacity;

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(taskCoreSize);
        executor.setMaxPoolSize(taskMaxSize);
        executor.setQueueCapacity(taskQueueCapacity);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(AWAIT_SECONDS);
        executor.setThreadNamePrefix("task-");
        return executor;
    }

    @Bean
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(notificationCoreSize);
        executor.setMaxPoolSize(notificationMaxSize);
        executor.setQueueCapacity(notificationQueueCapacity);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(AWAIT_SECONDS);
        executor.setThreadNamePrefix("notification-");
        return executor;
    }
}
