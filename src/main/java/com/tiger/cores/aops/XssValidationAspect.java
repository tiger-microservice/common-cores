package com.tiger.cores.aops;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiger.cores.aops.annotations.InternalOnly;
import com.tiger.cores.configs.logging.LoggingConfig;
import com.tiger.cores.exceptions.BusinessLogicException;
import com.tiger.cores.exceptions.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.secure.xss.enable", name = "enable", havingValue = "true", matchIfMissing = true)
public class XssValidationAspect {
    private static final String PACKAGE_CONTAIN_VALUE = ".tiger.";
    private static final String XSS_PATTERNS_REGEX =
            "(?i)(<(script|iframe|object|embed|form|meta)[^>]*>|" + "<\\/(script|iframe|object|embed|form|meta)>|"
                    + "\\bon\\w+\\s*=\\s*[\"'][^\"'>]*[a-zA-Z][^\"'>]*[\"']|"
                    + "expression\\([^)]*\\)|"
                    + "url\\(\\s*javascript:[^)]*\\)|"
                    + "data:\\s*(text\\/html|application\\/xml)[^,]*|"
                    + "javascript:|vbscript:)";

    private final ObjectMapper mapper;
    private final HttpServletRequest request;

    @Before(LoggingConfig.REST_CONTROLLER_BEANS_POINTCUT)
    public void validateXssForRequest(JoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            if (method.isAnnotationPresent(InternalOnly.class)) {
                log.info("[validateXssForRequest] class {} validate xss for method {} ignore", className, methodName);
                return;
            }

            log.info("[validateXssForRequest] class {} validate xss for method {} start", className, methodName);
            checkInputRequestValidXss(methodSignature, joinPoint.getArgs());
        } catch (BusinessLogicException e) {
            log.error(
                    "[validateXssForRequest] class {} validate xss for method {} input request invalid",
                    className,
                    methodName);
            throw e;
        } finally {
            stopWatch.stop();
            long totalTimeMillis = stopWatch.getTotalTimeMillis();
            log.debug(
                    "[validateXssForRequest] class {} method {} execute times {} ms",
                    className,
                    method,
                    totalTimeMillis);
            if (totalTimeMillis > 100) { // 100 milliseconds
                log.warn(
                        "[validateXssForRequest] class {} method {} slow check xss {} ms",
                        className,
                        method,
                        totalTimeMillis);
            }
        }
    }

    private void checkInputRequestValidXss(MethodSignature methodSignature, Object[] args) {
        // Advice code to check for XSS
        Class<?>[] parameterTypes = methodSignature.getParameterTypes();
        for (int i = 0; i < args.length; i++) {
            Class<?> clazz = parameterTypes[i];
            Object obj = args[i];
            if (Objects.isNull(obj) || !isTypeNeedToCheck(clazz, obj)) {
                continue;
            }
            if (obj instanceof String) {
                validateXssForString(obj);
            } else {
                validateXssForObject(obj);
            }
        }
    }

    private void validateXssForObject(Object value) {
        // cast object to string value
        String jsonValue = castToString(value);
        // validate xss for json value
        validateXssForString(jsonValue);
    }

    private void validateXssForString(Object value) {
        if (Boolean.TRUE.equals(isXSS((String) value))) {
            log.error("[validateXssForString] invalid xss with value {}", value);
            throw new BusinessLogicException(ErrorCode.XSS_REQUEST_INVALID);
        }
    }

    public boolean isTypeNeedToCheck(Class<?> clazz, Object obj) {
        String packageName = clazz.getCanonicalName();
        return obj instanceof Map
                || obj instanceof String
                || obj instanceof Collection
                || packageName.contains(PACKAGE_CONTAIN_VALUE);
    }

    private boolean isXSS(String value) {
        Pattern pattern = Pattern.compile(XSS_PATTERNS_REGEX);
        Matcher matcher = pattern.matcher(value);
        return matcher.find();
    }

    private String castToString(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (Exception e) {
            log.error("[XssValidationAspect][castToString] error {}", e.getMessage());
            return "";
        }
    }
}
