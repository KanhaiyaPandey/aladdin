package com.store.aladdin.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void set(String key, Object value, long ttlSeconds) {
        try {
            redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("❌ Failed to set Redis key {}: {}", key, e.getMessage());
        }
    }

    public <T> T get(String key, Class<T> entityClass) {
        try {
            Object obj = redisTemplate.opsForValue().get(key);
            if (obj == null) return null;
            return objectMapper.convertValue(obj, entityClass);
        } catch (Exception e) {
            log.error("❌ Failed to fetch Redis key {}: {}", key, e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String key) {
        Object o = redisTemplate.opsForValue().get(key);
        if (o == null) return Collections.emptyList();

        if (o instanceof List<?>) {
            return (List<T>) o;
        }
        throw new IllegalStateException("Cached object is not a List");
    }
    public void delete(String key) {
        redisTemplate.delete(key);
    }



}
