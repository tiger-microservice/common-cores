package com.tiger.cores.services.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.tiger.cores.services.CacheService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RedisService implements CacheService {
    final RedisTemplate<String, Object> redisTemplate;

    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    public Long decrement(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    public void expireTime(String key, long milliSeconds) {
        redisTemplate.expire(key, milliSeconds, TimeUnit.MILLISECONDS);
    }

    public Long incrementExpireTime(String key, long milliSeconds) {
        Long result = increment(key);
        if (result == 1) {
            redisTemplate.expire(key, milliSeconds, TimeUnit.MILLISECONDS);
        }
        return result;
    }

    public Long decrementExpireTime(String key, long milliSeconds) {
        Long result = increment(key);
        log.info("[decrementExpireTime] result {}", result);
        redisTemplate.expire(key, milliSeconds, TimeUnit.MILLISECONDS);
        return result;
    }

    @Override
    public Long getValue(String key) {
        Object andExpire = redisTemplate.opsForValue().get(key);
        log.info("[getValue] andExpire {}", andExpire);
        return Long.parseLong(andExpire + "");
    }

    @Override
    public Long getValue(String key, long milliSeconds) {
        Object andExpire = redisTemplate.opsForValue().getAndExpire(key, milliSeconds, TimeUnit.MILLISECONDS);
        return (Long) andExpire;
    }

    @Override
    public void put(String key, Object value, long milliSeconds) {
        redisTemplate.opsForValue().set(key, value, milliSeconds, TimeUnit.MILLISECONDS);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
