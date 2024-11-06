package com.tiger.cores.services;

import com.tiger.cores.exceptions.ErrorCode;
import org.springframework.stereotype.Service;

import com.tiger.cores.exceptions.StaleDataException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VersionTrackingService {

    private final CacheService cacheService;
    private static final String VERSION_KEY = "user_version:";

    public void trackVersion(String username, String entityId, String version, long ttl) {
        String key = getKey(username, entityId);
        cacheService.put(key, version, ttl);
    }

    public String getUserVersion(String username, String entityId) {
        String key = getKey(username, entityId);
        var version = cacheService.get(key);

        if (version == null) {
            throw new StaleDataException(ErrorCode.CONCURRENT_REQUEST_ERROR);
        }

        return version.toString();
    }

    // username::id::version
    private String getKey(String username, String entityId) {
        return VERSION_KEY + username + "::" + entityId;
    }
}
