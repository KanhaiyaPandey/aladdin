package com.store.aladdin.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.aladdin.exceptions.CustomeRuntimeExceptionsHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private final RedisTemplate redisTemplate;

    public void set(String key, Object o, Long ttl) {
        redisTemplate.opsForValue().set(key, o, ttl, TimeUnit.SECONDS);
    }

    public <T> T get(String key, Class<T> entityClass) {
        Object o = redisTemplate.opsForValue().get(key);
        return entityClass.cast(o);
    }



}
