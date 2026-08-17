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
            log.error("❌ Failed to set Redis key '{}': {}", key, e.getMessage());
        }
    }

    public <T> T get(String key, Class<T> entityClass) {
        try {
            Object obj = redisTemplate.opsForValue().get(key);
            if (obj == null) return null;
            return objectMapper.convertValue(obj, entityClass);
        } catch (Exception e) {
            log.error("❌ Failed to fetch Redis key '{}': {}", key, e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String key) {
        try {
            Object o = redisTemplate.opsForValue().get(key);

            if (o == null) {
                return Collections.emptyList();
            }

            if (o instanceof List<?>) {
                return (List<T>) o;
            }

            log.error("❌ Redis: Cached object for key '{}' is not a List (found: {})", key, o.getClass().getSimpleName());
            throw new IllegalStateException("Cached object is not a List");
        } catch (Exception e) {
            log.error("❌ Failed to fetch list from Redis for key '{}': {}", key, e.getMessage());
            return Collections.emptyList();
        }
    }

    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            log.info("🗑️ Deleted Redis key '{}'", key);
        } catch (Exception e) {
            log.error("❌ Failed to delete Redis key '{}': {}", key, e.getMessage());
        }
    }

    /**
     * Deletes every key starting with `prefix`. Used for cache families keyed
     * per-entity (e.g. one "category_descendants_&lt;id&gt;" entry per category)
     * where a single write can invalidate an unknown number of them - a
     * category move/delete can change the subtree of any ancestor.
     */
    public void deleteByPrefix(String prefix) {
        try {
            java.util.Set<String> keys = redisTemplate.keys(prefix + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("🗑️ Deleted {} Redis keys with prefix '{}'", keys.size(), prefix);
            }
        } catch (Exception e) {
            log.error("❌ Failed to delete Redis keys with prefix '{}': {}", prefix, e.getMessage());
        }
    }

    // ✅ Optional health check
    public boolean ping() {
        try {
            String pong = (String) redisTemplate.getConnectionFactory().getConnection().ping();
            return "PONG".equalsIgnoreCase(pong);
        } catch (Exception e) {
            log.error("❌ Redis ping failed: {}", e.getMessage());
            return false;
        }
    }
}
