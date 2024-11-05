package com.tiger.cores.services;

import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VersionTrackingService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String VERSION_KEY = "user_version:";

    public void trackVersion(String username, String entityType, String entityId, UUID version) {
        String key = getKey(username, entityType, entityId);
        redisTemplate.opsForValue().set(key, version.toString());
    }

    public String getUserVersion(String username, String entityType, String entityId) {
        String key = getKey(username, entityType, entityId);
        return redisTemplate.opsForValue().get(key);
    }

    private String getKey(String username, String entityType, String entityId) {
        return VERSION_KEY + username + ":" + entityType + ":" + entityId;
    }
}
