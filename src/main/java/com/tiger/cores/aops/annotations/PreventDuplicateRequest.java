package com.tiger.cores.aops.annotations;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface PreventDuplicateRequest {

    long timeoutSeconds() default 2;
}
