package com.tiger.cores.aops;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.tiger.cores.aops.annotations.ConvertTimeZone;
import com.tiger.cores.dtos.responses.ApiResponse;
import com.tiger.cores.filters.TimezoneFilter;

@Aspect
@Component
public class TimeZoneAspect {

    @Around("execution(* com.tiger.*.controllers.*.*..*(..))")
    public Object beforeAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        if (result != null) {
            if (result instanceof ApiResponse<?>) {
                ApiResponse response = (ApiResponse) result;
                Object data = response.getData();
                if (data != null) {
                    processTimeZoneConversion(data);
                }
            }
        }
        return result;
    }

    private void processTimeZoneConversion(Object obj) throws IllegalAccessException {
        if (obj == null) return;

        Class<?> clazz = obj.getClass();
        if (isWrapperType(clazz)) return;

        if (obj instanceof Collection) {
            for (Object element : (Collection<?>) obj) {
                processTimeZoneConversion(element);
            }
        } else if (obj instanceof Map) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
                processTimeZoneConversion(entry.getKey());
                processTimeZoneConversion(entry.getValue());
            }
        } else if (obj instanceof Page) {
            for (Object element : ((Page<?>) obj).getContent()) {
                processTimeZoneConversion(element);
            }
        } else {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(obj);

                if (field.isAnnotationPresent(ConvertTimeZone.class) && value instanceof LocalDateTime localDateTime) {
                    ConvertTimeZone annotation = field.getAnnotation(ConvertTimeZone.class);
                    String clientTimeZone = TimezoneFilter.getTimeZone();

                    ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of(annotation.fromZone()));
                    ZonedDateTime convertedDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of(clientTimeZone));
                    field.set(obj, convertedDateTime.toLocalDateTime());

                } else if (value != null && !field.getType().isPrimitive() && !isWrapperType(field.getType())) {
                    // Nếu field là một object không phải primitive type thì tiếp tục xử lý đệ quy
                    processTimeZoneConversion(value);
                }
            }
        }
    }

    private boolean isWrapperType(Class<?> clazz) {
        var packageName = clazz.getCanonicalName();
        return clazz.equals(Boolean.class)
                || clazz.equals(Integer.class)
                || clazz.equals(Character.class)
                || clazz.equals(Byte.class)
                || clazz.equals(Short.class)
                || clazz.equals(Double.class)
                || clazz.equals(Long.class)
                || clazz.equals(Float.class)
                || clazz.equals(String.class)
                || (!packageName.contains("vn.tiger") && !packageName.contains("com.tiger"));
    }
}
