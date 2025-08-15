package com.store.aladdin.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;


@Configuration
public class MongoConfig {
        
        @Value("${spring.data.mongodb.uri}")
        private String mongoUri;

        private static final Logger logger = LoggerFactory.getLogger(MongoConfig.class);

        @Bean
        public MongoTemplate mongoTemplate() {
        logger.info("✅ MongoDB Connected");
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(mongoUri));

    }
}
