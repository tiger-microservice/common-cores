package com.tiger.cores.aops;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

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
    private final ObjectMapper mapper;
    private static final String PACKAGE_CONTAIN_VALUE = ".tiger.";
    private static final Pattern[] XSS_PATTERNS = {
        // HTML Tags (script, iframe, meta, xml, etc.)
        Pattern.compile("<(\\s*)script(.*?)>(.*?)</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<(\\s*)iframe(.*?)>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<(\\s*)meta(.*?)>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<(\\s*)xml(.*?)>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<(\\s*)xss(.*?)>", Pattern.CASE_INSENSITIVE),
        // JavaScript Event Handlers (onload, onclick, etc.)
        Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("onclick(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("onmouseover(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("onerror(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // JavaScript Protocols
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
        // Data URIs
        Pattern.compile("data:", Pattern.CASE_INSENSITIVE),
        // Inline CSS with JavaScript
        Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("url\\(javascript:(.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
    };

    final HttpServletRequest request;

    @Before(LoggingConfig.REST_CONTROLLER_BEANS_POINTCUT)
    public void validateXssForRequest(JoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
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
        for (Pattern scriptPattern : XSS_PATTERNS) {
            if (scriptPattern.matcher(value).find()) {
                return true;
            }
        }
        return false;
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
