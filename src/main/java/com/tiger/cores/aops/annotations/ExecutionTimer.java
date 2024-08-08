package com.tiger.cores.aops.annotations;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ExecutionTimer {
    int maxThreshold() default 100;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
