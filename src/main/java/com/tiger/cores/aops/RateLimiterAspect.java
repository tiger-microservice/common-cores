package com.tiger.cores.aops;

import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.tiger.cores.aops.annotations.RateLimiter;
import com.tiger.cores.exceptions.ErrorCode;
import com.tiger.cores.exceptions.RateLimitExceededException;
import com.tiger.cores.services.impl.RedisRateLimiter;
import com.tiger.cores.utils.UserInfoUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimiterAspect extends AbstractAspect {

    final RedisRateLimiter redisRateLimiter;

    @Around("@annotation(rateLimiter)")
    public Object rateLimiterExecute(final ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) throws Throwable {
        try {
            String key = getKeyRateLimit(joinPoint, rateLimiter.key());
            log.info("[rateLimiterExecute] lock by key {}", key);

            if (redisRateLimiter.tryAcquire(key, rateLimiter.limit(), rateLimiter.timeWindowSeconds())) {
                return joinPoint.proceed();
            }

            throw new RateLimitExceededException(ErrorCode.RATE_LIMIT_ERROR);
        } catch (Throwable e) {
            log.error("[rateLimiterExecute] error {}", e.getMessage(), e);
            throw e;
        }
    }

    private String getKeyRateLimit(ProceedingJoinPoint joinPoint, String value) {
        if (Strings.isBlank(value)) {
            // create key default
            if (UserInfoUtil.getAccountUser() != null) {
                value = UserInfoUtil.getAccountUser().getName();
            }
            value = generateKey(joinPoint, value);
        } else {
            // parser expression
            value = parserKey(joinPoint, value);
        }
        return value;
    }

    private String parserKey(ProceedingJoinPoint joinPoint, String keyExpression) {
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();

        // Đặt các tham số của phương thức vào EvaluationContext theo tên biến
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        // Đánh giá SpEL expression
        var valueObject = parser.parseExpression(keyExpression).getValue(context);

        return valueObject == null ? null : valueObject.toString();
    }

    private String generateKey(ProceedingJoinPoint joinPoint, String key) {
        String signature = joinPoint.getSignature().toShortString();
        return key + "_" + signature;
    }
}
