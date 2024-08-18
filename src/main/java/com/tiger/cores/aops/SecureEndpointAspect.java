package com.tiger.cores.aops;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiger.cores.aops.annotations.SecureEndpoint;
import com.tiger.cores.constants.AppConstants;
import com.tiger.cores.dtos.responses.ApiResponse;
import com.tiger.cores.entities.MasterConfig;
import com.tiger.cores.exceptions.BusinessLogicException;
import com.tiger.cores.exceptions.ErrorCode;
import com.tiger.cores.repositories.ConfigRepository;
import com.tiger.cores.services.impl.RedisService;
import com.tiger.cores.services.impl.SecureService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SecureEndpointAspect {

    private final RedisService redisService;
    private final HttpServletRequest httpRequest;
    private final SecureService secureService;
    private final ObjectMapper objectMapper;
    private final ConfigRepository configRepository;

    @Around(value = "@annotation(secure) && args(request)", argNames = "joinPoint,secure,request")
    public Object secureEndpoint(ProceedingJoinPoint joinPoint, SecureEndpoint secure, Object request)
            throws Throwable {

        MasterConfig config = configRepository.findByConfigName(AppConstants.FLAG_SECURE);

        if (config == null || "OFF".equals(config.getConfigValue())) {
            return processNormal(joinPoint, request, secure);
        } else {
            return processSecure(joinPoint, request, secure);
        }
    }

    private ApiResponse<?> processSecure(ProceedingJoinPoint joinPoint, Object request, SecureEndpoint secure)
            throws Throwable {

        String appKey = httpRequest.getHeader(AppConstants.APP_TRANSACTION_KEY);
        Object aesKey = redisService.get(appKey);
        if (aesKey == null) {
            throw new BusinessLogicException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        // Decrypt request body
        String decryptedData = secureService.decryptBody(aesKey.toString(), request.toString(), appKey);
        // Convert request body to POJO type
        Object convertedObject = objectMapper.readValue(decryptedData, secure.pojoType());
        // Call main business
        Object result = joinPoint.proceed(new Object[] {convertedObject});
        return ApiResponse.responseOK(secureService.encryptBody(aesKey.toString(), result, appKey));
    }

    private Object processNormal(ProceedingJoinPoint joinPoint, Object request, SecureEndpoint secure)
            throws Throwable {
        // Convert request body to POJO type
        Object convertedObject = objectMapper.convertValue(request, secure.pojoType());
        return joinPoint.proceed(new Object[] {convertedObject});
    }
}
