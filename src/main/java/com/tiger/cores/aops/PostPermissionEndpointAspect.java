package com.tiger.cores.aops;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.tiger.cores.aops.annotations.PrePermissionEndpoint;

import lombok.extern.slf4j.Slf4j;

/**
 * Verify data response in scope of User
 */
@Slf4j
@Aspect
public class PostPermissionEndpointAspect extends AbstractAspect {

    @Before("@annotation(prePermissionEndpoint)")
    public void prePermissionExecute(final JoinPoint joinPoint, PrePermissionEndpoint prePermissionEndpoint)
            throws Throwable {
        log.info("[prePermissionExecute] has been called for method {}", getMethodNames(joinPoint));
    }
}
