package com.tiger.cores.aops;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiger.cores.aops.annotations.SecureEndpoint;
import com.tiger.cores.dtos.requests.BaseRequest;
import com.tiger.cores.dtos.responses.ApiResponse;
import com.tiger.cores.exceptions.BusinessLogicException;
import com.tiger.cores.exceptions.ErrorCode;
import com.tiger.cores.services.RedisService;
import com.tiger.cores.services.SecureService;

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

    @Around(value = "@annotation(secure) && args(request)", argNames = "joinPoint,secure,request")
    public Object secureEndpoint(ProceedingJoinPoint joinPoint, SecureEndpoint secure, BaseRequest<Object> request)
            throws Throwable {

        boolean isSecure = Boolean.parseBoolean(httpRequest.getHeader("iss"));
        if (isSecure) {
            return processSecure(joinPoint, request, secure);
        } else {
            return processNormal(joinPoint, request, secure);
        }
    }

    private ApiResponse<?> processSecure(
            ProceedingJoinPoint joinPoint, BaseRequest<Object> request, SecureEndpoint secure) throws Throwable {
        String sgId = httpRequest.getHeader("sgId");
        Object aesKey = redisService.get(sgId);
        if (aesKey == null) {
            throw new BusinessLogicException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        // Decrypt request body
        String decryptedData =
                secureService.decryptBody(aesKey.toString(), request.getData().toString(), sgId);
        // Convert request body to POJO type
        Object convertedObject = objectMapper.readValue(decryptedData, secure.pojoType());
        // Call main business
        Object result = joinPoint.proceed(
                new Object[] {BaseRequest.builder().data(convertedObject).build()});
        return ApiResponse.responseOK(secureService.encryptBody(aesKey.toString(), result, sgId));
    }

    private Object processNormal(ProceedingJoinPoint joinPoint, BaseRequest<Object> request, SecureEndpoint secure)
            throws Throwable {
        // Convert request body to POJO type
        Object convertedObject = objectMapper.convertValue(request.getData(), secure.pojoType());
        return joinPoint.proceed(new Object[] {BaseRequest.builder().data(convertedObject).build()});
    }
}
