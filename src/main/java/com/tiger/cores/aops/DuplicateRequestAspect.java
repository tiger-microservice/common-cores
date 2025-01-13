package com.tiger.cores.aops;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.tiger.common.utils.ObjectMapperUtil;
import com.tiger.cores.aops.annotations.PreventDuplicateRequest;
import com.tiger.cores.dtos.RequestDataDto;
import com.tiger.cores.exceptions.DuplicateRequestException;
import com.tiger.cores.exceptions.ErrorCode;
import com.tiger.cores.services.CacheService;
import com.tiger.cores.utils.HashUtil;
import com.tiger.cores.utils.JsonUtil;
import com.tiger.cores.utils.UserInfoUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@DependsOn("redissonClient")
public class DuplicateRequestAspect {

    private final HttpServletRequest request;
    private final CacheService cacheService;
    private final RedissonClient redissonClient;

    @Around("@annotation(preventDuplicateRequest)")
    public Object preventDuplicateRequest(
            ProceedingJoinPoint joinPoint, PreventDuplicateRequest preventDuplicateRequest) throws Throwable {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String userId = UserInfoUtil.getUserInfo().getEmail();
        String requestKey = generateRequestKey(joinPoint, userId);

        // Sử dụng distributed lock để đảm bảo atomic operation
        RLock lock = redissonClient.getLock("lock:" + requestKey);
        // Chờ tối đa 2 giây để lấy lock
        boolean isLocked = lock.tryLock(preventDuplicateRequest.timeoutSeconds(), TimeUnit.SECONDS);

        if (!isLocked) {
            log.error("Locked with key {}", requestKey);
            throw new DuplicateRequestException(ErrorCode.DUPLICATE_REQUEST);
        }

        Object o = cacheService.get(requestKey);

        if (o != null) {
            RequestDataDto requestDataDto = ObjectMapperUtil.castToObject((String) o, RequestDataDto.class);
            long secondsBetween = ChronoUnit.SECONDS.between(requestDataDto.getTimestamp(), LocalDateTime.now());

            if (secondsBetween < preventDuplicateRequest.timeoutSeconds()) {
                log.error("Locked with key {}", requestKey);
                throw new DuplicateRequestException(
                        ErrorCode.DUPLICATE_REQUEST, (preventDuplicateRequest.timeoutSeconds() - secondsBetween));
            }
        }

        // Lưu request mới vào Redis
        RequestDataDto newRequest = new RequestDataDto(
                userId, request.getRequestURI(), JsonUtil.castToString(joinPoint.getArgs()), LocalDateTime.now());

        cacheService.put(
                requestKey, ObjectMapperUtil.castToString(newRequest), preventDuplicateRequest.timeoutSeconds());

        // Thực thi method
        return joinPoint.proceed();
    }

    private String generateRequestKey(ProceedingJoinPoint joinPoint, String userId) {
        String valueKey = userId + request.getRequestURI() + JsonUtil.castToString(joinPoint.getArgs());
        return HashUtil.hashValue(valueKey) + "";
    }
}
