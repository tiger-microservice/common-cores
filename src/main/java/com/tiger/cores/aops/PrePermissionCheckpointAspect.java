package com.tiger.cores.aops;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.tiger.cores.aops.annotations.PostPermissionCheckpoint;

import lombok.extern.slf4j.Slf4j;

/**
 * Verify request in scope of User
 */
@Slf4j
@Aspect
@Component
public class PrePermissionCheckpointAspect extends AbstractAspect {

    @AfterReturning("@annotation(postPermissionCheckpoint)")
    public void postPermissionExecute(final JoinPoint joinPoint, PostPermissionCheckpoint postPermissionCheckpoint)
            throws Throwable {
        log.info("[postPermissionExecute] has been called for method {}", getMethodNames(joinPoint));
    }
}
