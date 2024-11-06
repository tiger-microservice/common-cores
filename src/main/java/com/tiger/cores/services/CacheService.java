package com.tiger.cores.services;

import java.util.function.Function;

public interface CacheService {
    Long increment(String key);

    Long decrement(String key);

    void expireTime(String key, long miniSeconds);

    Long incrementExpireTime(String key, long milliSeconds);

    Long decrementExpireTime(String key, long milliSeconds);

    Object getValue(String key);

    Object getAndExpireTime(String key, long milliSeconds);

    void put(String key, Object value, long milliSeconds);

    Object get(String key);

    <T, R> R lock(String key, T input, Function<T, R> funcCallback, long milliSeconds);

    boolean lock(String key, String value, long milliSeconds);

    void releaseLock(String key);
}
