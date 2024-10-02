package com.tiger.cores.aops;

import com.tiger.cores.aops.annotations.Sanitize;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Slf4j
@Aspect
@Component
public class SanitizeAspect {

    private final PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

    @Around("@annotation(sanitize)")
    public Object sanitizeInput(ProceedingJoinPoint joinPoint, Sanitize sanitize) throws Throwable {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            sanitizeFields(arg);
        }
        return joinPoint.proceed(args);
    }

    private void sanitizeFields(Object obj) {
        if (obj == null) return;

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value != null) {
                    if (field.getType().equals(String.class) && field.isAnnotationPresent(Sanitize.class)) {
                        field.set(obj, policy.sanitize((String) value));
                    } else if (!field.getType().isPrimitive()) {
                        sanitizeFields(value);
                    }
                }
            } catch (IllegalAccessException e) {
                // Handle exception
                log.error("[sanitizeFields] has error {}", e.getMessage(), e);
            }
        }
    }
}
