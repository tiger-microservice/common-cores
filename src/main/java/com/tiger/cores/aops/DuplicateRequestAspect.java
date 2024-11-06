package com.tiger.cores.aops;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.tiger.common.utils.ObjectMapperUtil;
import com.tiger.cores.aops.annotations.PreventDuplicateRequest;
import com.tiger.cores.dtos.RequestDataDto;
import com.tiger.cores.exceptions.DuplicateRequestException;
import com.tiger.cores.exceptions.ErrorCode;
import com.tiger.cores.services.CacheService;
import com.tiger.cores.utils.UserInfoUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@ConditionalOnProperty(name = "app.redisson.config.enable", havingValue = "true")
@RequiredArgsConstructor
public class DuplicateRequestAspect {

    private final CacheService cacheService;
    private final RedissonClient redissonClient;

    @Around("@annotation(preventDuplicateRequest)")
    public Object preventDuplicateRequest(
            ProceedingJoinPoint joinPoint, PreventDuplicateRequest preventDuplicateRequest) throws Throwable {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String userId = UserInfoUtil.getUserInfo().getEmail();
        String requestKey = generateRequestKey(request, userId);

        // Sử dụng distributed lock để đảm bảo atomic operation
        RLock lock = null;
        try {
            lock = redissonClient.getLock("lock:" + requestKey);
            // Chờ tối đa 2 giây để lấy lock
            boolean isLocked = lock.tryLock(preventDuplicateRequest.timeoutSeconds(), TimeUnit.SECONDS);

            if (!isLocked) {
                throw new DuplicateRequestException(ErrorCode.DUPLICATE_REQUEST);
            }

            Object o = cacheService.get(requestKey);

            if (o != null) {
                RequestDataDto requestDataDto = ObjectMapperUtil.castToObject((String) o, RequestDataDto.class);
                long secondsBetween = ChronoUnit.SECONDS.between(requestDataDto.getTimestamp(), LocalDateTime.now());

                if (secondsBetween < preventDuplicateRequest.timeoutSeconds()) {
                    throw new DuplicateRequestException(
                            ErrorCode.DUPLICATE_REQUEST, (preventDuplicateRequest.timeoutSeconds() - secondsBetween));
                }
            }

            // Lưu request mới vào Redis
            RequestDataDto newRequest =
                    new RequestDataDto(userId, request.getRequestURI(), getRequestBody(request), LocalDateTime.now());

            cacheService.put(
                    requestKey, ObjectMapperUtil.castToString(newRequest), preventDuplicateRequest.timeoutSeconds());

            // Thực thi method
            return joinPoint.proceed();

        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private String generateRequestKey(HttpServletRequest request, String userId) {
        // Tạo key duy nhất dựa trên userId, URI và request body
        return userId + ":" + request.getRequestURI() + ":" + getRequestBody(request);
    }

    private String getRequestBody(HttpServletRequest request) {
        try {
            // Cache lại reques t body vì nó chỉ có thể đọc một lần
            String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            // Wrap request để có thể đọc lại body
            request = new ContentCachingRequestWrapper(request);
            return requestBody;
        } catch (IOException e) {
            log.error("Error reading request body", e);
            return "";
        }
    }
}
