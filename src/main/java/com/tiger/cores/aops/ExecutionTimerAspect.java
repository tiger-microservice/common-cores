package com.tiger.cores.aops;

import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.tiger.cores.aops.annotations.ExecutionTimer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class ExecutionTimerAspect extends AbstractAspect {

    @Around("@annotation(executionTimer)")
    public Object executionTimer(ProceedingJoinPoint joinPoint, ExecutionTimer executionTimer) throws Throwable {
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            Object proceed = joinPoint.proceed();

            stopWatch.stop();

            long executionTime = stopWatch.getTotalTimeMillis();

            TimeUnit timeUnit = executionTimer.timeUnit();
            // convert time by timeUnit
            switch (timeUnit) {
                case MILLISECONDS:
                    break;
                case MINUTES:
                    executionTime = executionTime / 60000;
                    break;
                default:
                    executionTime = executionTime / 1000;
                    break;
            }

            // check max threshold
            int maxThresholdTime = executionTimer.maxThreshold();
            if (maxThresholdTime > 0 && executionTime > maxThresholdTime) {
                log.warn(
                        "[executionTimer] {} method was executed in {} {} which was higher than expected Max Threshold of {} {}",
                        joinPoint.getSignature(),
                        executionTime,
                        timeUnit,
                        maxThresholdTime,
                        timeUnit);
            } else {
                log.info(
                        "[executionTimer] {} method was executed in {} {}",
                        joinPoint.getSignature(),
                        executionTime,
                        timeUnit);
            }

            return proceed;
        } catch (Exception e) {
            log.error(
                    "[executionTimer] There was an error while calculating method execution time for {}",
                    joinPoint.getSignature(),
                    e);
            return null;
        }
    }
}
