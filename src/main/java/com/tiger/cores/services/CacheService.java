package com.tiger.cores.services;

public interface CacheService {
    Long increment(String key);

    Long decrement(String key);

    void expireTime(String key, long miniSeconds);

    Long incrementExpireTime(String key, long milliSeconds);

    Long decrementExpireTime(String key, long milliSeconds);

    Long getValue(String key);

    Long getValue(String key, long milliSeconds);
}
