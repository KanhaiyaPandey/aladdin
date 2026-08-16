package com.store.aladdin.configs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * Prints a clean, human-readable startup summary once the application is
 * actually ready to serve traffic. Unlike logging inside a @Bean method
 * (which only proves the bean object was constructed), the checks here make
 * a real round-trip to MongoDB and Redis, so "connected" here means connected.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StartupHealthLogger {

    private final MongoTemplate mongoTemplate;
    private final RedisConnectionFactory redisConnectionFactory;
    private final Environment environment;

    @EventListener(ApplicationReadyEvent.class)
    public void logStartupStatus() {
        checkMongo();
        checkRedis();

        String port = environment.getProperty("local.server.port", environment.getProperty("server.port", "8080"));
        log.info("🚀 Aladdin server is running on port {}", port);
    }

    private void checkMongo() {
        try {
            String dbName = mongoTemplate.getDb().getName();
            mongoTemplate.getDb().listCollectionNames().first();
            log.info("✅ MongoDB connected -> database '{}'", dbName);
        } catch (Exception e) {
            log.error("❌ MongoDB connection failed: {}", e.getMessage());
        }
    }

    private void checkRedis() {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            String pong = connection.ping();
            log.info("✅ Redis connected -> {}", pong);
        } catch (Exception e) {
            log.error("❌ Redis connection failed: {}", e.getMessage());
        }
    }
}
