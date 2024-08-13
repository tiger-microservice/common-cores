package com.tiger.cores.services.impl;

import com.tiger.cores.services.CacheService;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisRateLimiter {

    final CacheService cacheService;

    // cache on redis
    public boolean tryAcquire(String key, int maxRequests, long timeWindowSeconds) {
        return cacheService.incrementExpireTime(key, timeWindowSeconds * 1000L) <= maxRequests;
    }
}
