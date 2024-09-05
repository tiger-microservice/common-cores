package com.tiger.cores.services.impl;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.tiger.cores.exceptions.BusinessLogicException;
import com.tiger.cores.exceptions.ErrorCode;
import com.tiger.cores.services.CacheService;
import com.tiger.cores.utils.ThreadUtil;

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
    public Object getValue(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        log.info("[getValue] andExpire {}", value);
        return value;
    }

    @Override
    public Object getAndExpireTime(String key, long milliSeconds) {
        return redisTemplate.opsForValue().getAndExpire(key, milliSeconds, TimeUnit.MILLISECONDS);
    }

    @Override
    public void put(String key, Object value, long milliSeconds) {
        redisTemplate.opsForValue().set(key, value, milliSeconds, TimeUnit.MILLISECONDS);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public <T, R> R lock(String key, T input, Function<T, R> funcCallback, long milliSeconds) {
        AtomicInteger counter = new AtomicInteger(0);
        while (Objects.nonNull(get(key))) {
            ThreadUtil.sleep(1 * 1000L); // 1 seconds
            if (counter.incrementAndGet() > 5) { // max 5 terms
                throw new BusinessLogicException(ErrorCode.MAX_TERMS_RETRY);
            }
        }

        put(key, "LOCK", milliSeconds); // 10 seconds
        try {
            return funcCallback.apply(input);
        } finally {
            // release lock
            expireTime(key, 0);
        }
    }
}
