package com.tiger.cores.aops;

import com.tiger.cores.aops.annotations.CacheClear;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class CacheClearAspect extends AbstractAspect {

    @Before("@annotation(cacheClear)")
    public void postPermission(JoinPoint joinPoint, CacheClear cacheClear) throws Throwable {
        log.info("[postPermissionExecute] has been called for method {}", getMethodNames(joinPoint));
    }
}
