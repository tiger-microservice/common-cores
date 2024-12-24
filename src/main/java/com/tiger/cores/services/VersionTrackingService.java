package com.tiger.cores.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.tiger.cores.constants.AppConstants;
import com.tiger.cores.exceptions.ErrorCode;
import com.tiger.cores.exceptions.StaleDataException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VersionTrackingService {

    private static final String VERSION_KEY = "object_version";
    private final CacheService cacheService;

    public void trackVersion(String objectName, String username, String entityId, String version, long ttl) {
        String key = getKey(objectName, username, entityId);
        cacheService.put(key, version, ttl);
    }

    public String getUserVersion(String objectName, String username, String entityId) {
        String key = getKey(objectName, username, entityId);
        var version = cacheService.get(key);

        if (version == null) {
            throw new StaleDataException(ErrorCode.CONCURRENT_REQUEST_ERROR);
        }

        return version.toString();
    }

    // objectName:username:id=version
    private String getKey(String... args) {
        return VERSION_KEY + AppConstants.KEY_SEPARATOR + StringUtils.join(args, AppConstants.KEY_SEPARATOR);
    }
}
