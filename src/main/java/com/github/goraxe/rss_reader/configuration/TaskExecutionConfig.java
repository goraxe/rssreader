package com.github.goraxe.rss_reader.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created by goraxe on 2016-04-13.
 * Boo Yar
 */
@Configuration
public class TaskExecutionConfig {

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(20);
        taskExecutor.setQueueCapacity(10000);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.initialize();
        return taskExecutor;
    }


}
