package com.tiger.cores.aops;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.tiger.cores.aops.annotations.PostPermissionEndpoint;

import lombok.extern.slf4j.Slf4j;

/**
 * Verify request in scope of User
 */
@Slf4j
@Aspect
@Component
public class PrePermissionEndpointAspect extends AbstractAspect {

    @AfterReturning("@annotation(postPermissionEndpoint)")
    public void postPermissionExecute(final JoinPoint joinPoint, PostPermissionEndpoint postPermissionEndpoint)
            throws Throwable {
        log.info("[postPermissionExecute] has been called for method {}", getMethodNames(joinPoint));
    }
}
