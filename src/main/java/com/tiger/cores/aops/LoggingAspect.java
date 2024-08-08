package com.tiger.cores.aops;

import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("within(com.tiger..*) && execution(* com.tiger.*.services.*.*(..)))")
    public void serviceLayerPointcut() {}

    // https://stackoverflow.com/questions/58392441/configure-pointcut-for-spring-jpa-repository-methods
    @Pointcut("within(com.tiger..*) && execution(* org.springframework.data.jpa.repository.JpaRepository+.*(..))))")
    public void repositoryLayerPointcut() {}

    @Pointcut("within(com.tiger..*) && execution(* com.tiger.*.controllers.*.*.*.*(..)))")
    public void controllerLayerPointcut() {}

    @Around("serviceLayerPointcut()")
    public Object measureMethodServiceExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        Object proceed = joinPoint.proceed();
        long end = System.nanoTime();
        String methodName = joinPoint.getSignature().getName();
        log.info(
                "[Service] Execution of " + methodName + " took " + TimeUnit.NANOSECONDS.toMillis(end - start) + " ms");
        return proceed;
    }

    @Around("repositoryLayerPointcut()")
    public Object measureMethodRepositoryExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        Object proceed = joinPoint.proceed();
        long end = System.nanoTime();
        String methodName = joinPoint.getSignature().getName();
        log.info("[Repository] Execution of " + methodName + " took " + TimeUnit.NANOSECONDS.toMillis(end - start)
                + " ms");
        return proceed;
    }

    @Around("controllerLayerPointcut()")
    public Object measureMethodControllerExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        Object proceed = joinPoint.proceed();
        long end = System.nanoTime();
        String methodName = joinPoint.getSignature().getName();
        log.info("[Controller] Execution of " + methodName + " took " + TimeUnit.NANOSECONDS.toMillis(end - start)
                + " ms");
        return proceed;
    }
}
