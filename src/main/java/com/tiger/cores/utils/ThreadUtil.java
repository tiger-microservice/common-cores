package com.tiger.cores.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadUtil {

    public static void sleep(long milliseconds) {
        try {
            TimeUnit.SECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
