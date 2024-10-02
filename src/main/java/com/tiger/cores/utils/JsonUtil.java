package com.tiger.cores.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiger.common.utils.ObjectMapperUtil;
import com.tiger.cores.exceptions.BusinessLogicException;
import com.tiger.cores.exceptions.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public final class JsonUtil {

    public static ObjectMapper objectMapper() {
        return ObjectMapperUtil.objectMapper();
    }

    public static String castToString(Object value) {
        if (value == null) return "";

        try {
            return objectMapper().writeValueAsString(value);
        } catch (Exception e) {
            log.error("[castToString] error {}", e.getMessage(), e);
            return "";
        }
    }

    public static <T> T castToObject(String value, Class<T> clazz) {
        if (value == null || value.isEmpty()) return null;

        try {
            return objectMapper().readValue(value, clazz);
        } catch (Exception e) {
            log.error("[castToObject] error {}", e.getMessage(), e);
            return null;
        }
    }

    public static byte[] toByteArray(Object value) {
        try {
            return JsonUtil.objectMapper().writeValueAsBytes(value);
        } catch (Exception e) {
            log.error("[toByteArray] error {}", e.getMessage(), e);
            throw new BusinessLogicException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public static boolean isValidJsonFormat(String json) {
        try {
            JsonUtil.objectMapper().readTree(json);
            return true;
        } catch (Exception e) {
            log.error("[isValidJsonFormat] error {}", e.getMessage());
            return false;
        }
    }

    public static <T> T toObject(InputStream inputStream, TypeReference<T> typeReference) {
        try {
            return JsonUtil.objectMapper().readValue(inputStream, typeReference);
        } catch (IOException e) {
            log.error("[toObject] error {}", e.getMessage(), e);
            return null;
        }
    }
}
