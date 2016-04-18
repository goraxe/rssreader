package com.github.goraxe.rss_reader;

import com.github.goraxe.rss_reader.service.RSSFeedFetchService;
import org.apache.log4j.Logger;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Created by goraxe on 2016-03-23.
 * Boo Yar
 */

@SpringBootApplication
@EnableMongoRepositories( basePackages = "com.github.goraxe.rss_reader.repositories.mongo")
@EnableAsync
class App implements AsyncConfigurer {


    @Autowired
    RSSFeedFetchService feedFetchService;


    /*
    @PostConstruct
    public void startUp() {
        feedFetchService.fetchFeeds();
    }
    */


    static Logger log = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        System.out.println("starting up");
        SpringApplication.run(App.class, args);
        System.out.println("Init complete");
    }

    @Override
    public Executor getAsyncExecutor() {

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(50);
        taskExecutor.setQueueCapacity(10000);
        taskExecutor.setMaxPoolSize(50);
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}
