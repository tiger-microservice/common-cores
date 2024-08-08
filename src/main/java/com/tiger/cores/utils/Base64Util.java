package com.tiger.cores.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Util {

    public static String convertToBase64(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    public static String convertToString(String token) {
        byte[] decodedBytes = Base64.getDecoder().decode(token);
        return new String(decodedBytes);
    }
}
