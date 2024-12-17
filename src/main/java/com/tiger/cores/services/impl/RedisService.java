package com.tiger.cores.services.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
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
        return redisTemplate.opsForValue().increment(key); // return value first is 1
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
        while (Boolean.FALSE.equals(redisTemplate.opsForValue().setIfAbsent(key, "locked"))) {
            ThreadUtil.sleep(1000L); // 1 seconds
            if (counter.incrementAndGet() > 5) { // max 5 terms
                throw new BusinessLogicException(ErrorCode.MAX_TERMS_RETRY);
            }
        }

        try {
            return funcCallback.apply(input);
        } finally {
            // release lock
            expireTime(key, 0);
        }
    }

    @Override
    public boolean lock(String key, String value, long milliSeconds) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofMillis(milliSeconds)));
    }

    @Override
    public void releaseLock(String key) {
        redisTemplate.delete(key);
    }

    // Lưu value vào bitmap
    @Override
    public void saveValueBitmap(String value, String bitmapKey) {
        long offset = hashValue(value);
        redisTemplate.opsForValue().setBit(bitmapKey, offset, true);
    }

    // Kiểm tra value đã tồn tại hay chưa
    @Override
    public boolean isValueExistsBitmap(String value, String bitmapKey) {
        long offset = hashValue(value);
        Boolean exists = redisTemplate.opsForValue().getBit(bitmapKey, offset);
        return Boolean.TRUE.equals(exists);
    }

    // Hàm băm value để tạo ra offset
    private long hashValue(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            // Chuyển hash thành số nguyên dương
            return Math.abs(((long) hash[0] & 0xff) |
                    ((long) hash[1] & 0xff) << 8 |
                    ((long) hash[2] & 0xff) << 16 |
                    ((long) hash[3] & 0xff) << 24);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing value", e);
        }
    }

}
