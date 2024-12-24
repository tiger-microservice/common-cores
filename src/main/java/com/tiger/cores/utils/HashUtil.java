package com.tiger.cores.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

    // Hàm băm value để tạo ra offset
    public static long hashValue(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            // Chuyển hash thành số nguyên dương
            return Math.abs(((long) hash[0] & 0xff)
                    | ((long) hash[1] & 0xff) << 8
                    | ((long) hash[2] & 0xff) << 16
                    | ((long) hash[3] & 0xff) << 24);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing value", e);
        }
    }
}
