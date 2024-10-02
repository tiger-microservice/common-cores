package com.tiger.cores.aops;

import com.tiger.cores.aops.annotations.PrePermissionCheckpoint;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * Verify data response in scope of User
 */
@Slf4j
@Aspect
public class PostPermissionCheckpointAspect extends AbstractAspect {

    @Before("@annotation(prePermissionCheckpoint)")
    public void prePermissionExecute(final JoinPoint joinPoint, PrePermissionCheckpoint prePermissionCheckpoint)
            throws Throwable {
        log.info("[prePermissionExecute] has been called for method {}", getMethodNames(joinPoint));
    }
}
