package com.store.aladdin.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object o, Long ttl) {
        redisTemplate.opsForValue().set(key, o, ttl, TimeUnit.SECONDS);
    }

    public <T> T get(String key, Class<T> entityClass) {
        Object o = redisTemplate.opsForValue().get(key);
        return entityClass.cast(o);
    }



}
