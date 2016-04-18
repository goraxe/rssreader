package com.github.goraxe.rss_reader;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;

/**
 * Created by goraxe on 2016-04-07.
 * Boo Yar
 */
@Configuration
public class MongoConfiguration {

    public @Bean
    MongoClientFactoryBean mongo() {
        MongoClientFactoryBean mongo = new MongoClientFactoryBean();
        mongo.setHost("192.168.99.100");
        mongo.setPort(32778);
        return mongo;
    }
}
