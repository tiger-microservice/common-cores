package com.tiger.cores.aops.annotations;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RateLimiter {

    long timeWindowSeconds() default 0;

    int limit() default 3;

    String key() default "";
}
