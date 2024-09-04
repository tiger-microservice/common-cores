package com.tiger.cores.aops;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiger.cores.configs.logging.LoggingConfig;
import com.tiger.cores.configs.logging.LoggingProperties;
import com.tiger.cores.constants.AppConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    final ObjectMapper objectMapper;
    final LoggingProperties loggingProperties;

    @ConditionalOnProperty(prefix = "app.log.function", name = "enable", havingValue = "true", matchIfMissing = true)
    @Around(LoggingConfig.BASE_BEANS_POINTCUT)
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();

        String className = methodSignature.getDeclaringType().getSimpleName();
        String method = methodSignature.getName();

        log.info(
                "Enter {}.{}() with argument(s) : {}",
                className,
                method,
                markInput(methodSignature, joinPoint.getArgs()));
        log.info("[{}.{}] execution time: {} ms", className, method, stopWatch.getTotalTimeMillis());

        log.info("Exit [{}.{}] result: {}", className, method, markOutput(result));

        return result;
    }

    private String markInput(MethodSignature methodSignature, Object[] args) {
        Class<?>[] parameterTypes = methodSignature.getParameterTypes();
        String[] parameterNames = methodSignature.getParameterNames();
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            var obj = this.createParamValue(args[i]);
            // check with object
            if (!this.isPrimitiveOrWrapper(parameterTypes[i])) {
                map.put(this.createParamKey(parameterTypes[i], parameterNames[i]), obj);
            } else {
                final String paramName = parameterNames[i];
                if (Arrays.stream(this.loggingProperties.getMarkKeys())
                        .anyMatch(item -> item.equalsIgnoreCase(paramName))) {
                    map.put(paramName, AppConstants.START);
                }
            }
        }

        return map.toString();
    }

    public boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz == Boolean.class
                || clazz == Byte.class
                || clazz == Character.class
                || clazz == Short.class
                || clazz == Integer.class
                || clazz == Long.class
                || clazz == Float.class
                || clazz == Double.class
                || clazz == String.class;
    }

    private String createParamKey(Class<?> clazz, String paramName) {
        StringBuilder sb = new StringBuilder();

        sb.append(clazz.getName());
        sb.append("-");
        sb.append(paramName);

        return sb.toString();
    }

    private String createParamValue(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }

        String json = castToString(obj);
        if (Strings.isBlank(json) || json.equalsIgnoreCase("null")) {
            return "";
        }

        // replace exactly term
        // "\"" + key + "\" : \"", "\",|\"}"
        for (String key : loggingProperties.getMarkKeys()) {
            json = this.replaceBetween(json, "\"" + key + "\":\"", "\",|\"}", AppConstants.START);
        }

        // replace like term
        // "\"\\w*" + key + "\\w*\":\"", "\",|\"}"
        for (String key : loggingProperties.getMarkKeys()) {
            json = this.replaceBetween(json, "\"\\w*" + key + "\\w+\":\"", "\",|\"}", AppConstants.START);
        }

        return json;
    }

    private String markOutput(Object o) {
        return this.createParamValue(o);
    }

    private String replaceBetween(String input, String start, String end, String replaceWith) {
        return this.replaceBetween(input, start, end, false, false, replaceWith);
    }

    private String replaceBetween(
            String input, String start, String end, boolean startInclusive, boolean endInclusive, String replaceWith) {
        return input.replaceAll(
                this.createReplaceRegex(start, end), this.createReplaceWith(startInclusive, endInclusive, replaceWith));
    }

    private String createReplaceRegex(String start, String end) {
        StringBuilder sb = new StringBuilder();

        sb.append("(?i)");
        sb.append("(");
        sb.append(start);
        sb.append(")");
        sb.append(".*");
        sb.append("?");
        sb.append("(");
        sb.append(end);
        sb.append(")");

        return sb.toString();
    }

    private String createReplaceWith(boolean startInclusive, boolean endInclusive, String replaceWith) {
        StringBuilder sb = new StringBuilder();

        sb.append(startInclusive ? "" : "$1");
        sb.append(replaceWith);
        sb.append(endInclusive ? "" : "$2");

        return sb.toString();
    }

    private String castToString(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            log.error("castToString error {}", e.getMessage());
            return "";
        }
    }
}
