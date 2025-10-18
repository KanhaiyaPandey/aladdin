package com.store.aladdin.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.aladdin.models.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfig {

//    @Value("${spring.redis.host}")
//    private String redisHost;
//
//    @Value("${spring.redis.port}")
//    private int redisPort;
//
//    @Value("${spring.redis.username}")
//    private String redisUsername;
//
//    @Value("${spring.redis.password}")
//    private String redisPassword;
//
//    @Value("${spring.redis.ssl.enabled:false}")
//    private boolean useSsl;
//
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
//        redisConfig.setHostName(redisHost);
//        redisConfig.setPort(redisPort);
//        redisConfig.setUsername(redisUsername);
//        redisConfig.setPassword(redisPassword);
//
//        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfig);
//        factory.setUseSsl(useSsl);
//        try {
//            factory.afterPropertiesSet();
//            log.info("✅ Successfully connected to Redis at {}:{} (SSL: {})", redisHost, redisPort, useSsl);
//        } catch (Exception e) {
//            log.error("❌ Failed to connect to Redis at {}:{} - {}", redisHost, redisPort, e.getMessage());
//        }
//        return factory;
//    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // Use GenericJackson2JsonRedisSerializer with your ObjectMapper
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);

        return redisTemplate;
    }


}
