package com.store.aladdin.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object o, Long ttl) {
        redisTemplate.opsForValue().set(key, o, ttl, TimeUnit.HOURS);
    }

    public <T> T get(String key, Class<T> entityClass) {
        Object o = redisTemplate.opsForValue().get(key);
        return entityClass.cast(o);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String key, Class<T> elementClass) {
        Object o = redisTemplate.opsForValue().get(key);
        if (o == null) return null;

        if (o instanceof List<?>) {
            return (List<T>) o;
        }
        throw new IllegalStateException("Cached object is not a List");
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }



}
