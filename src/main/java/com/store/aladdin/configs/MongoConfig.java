package com.store.aladdin.configs;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;


@Configuration
@Slf4j
public class MongoConfig {
        
        @Value("${spring.data.mongodb.uri}")
        private String mongoUri;

        @Bean
        public MongoTemplate mongoTemplate() {
        log.info("✅ MongoDB Connected");
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(mongoUri));

    }
}
