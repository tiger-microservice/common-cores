package com.tiger.cores.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisRateLimiter {

    final CacheService cacheService;

    // cache on redis
    public boolean tryAcquire(String key, int maxRequests, long timeWindowSeconds) {
        return cacheService.incrementExpireTime(key, timeWindowSeconds * 1000L) <= maxRequests;
    }
}
