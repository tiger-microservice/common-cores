package com.tiger.cores.aops.annotations;

import java.lang.annotation.*;

import com.tiger.cores.constants.enums.CheckPointType;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(PrePermissionCheckpoints.class)
@Documented
public @interface PrePermissionCheckpoint {
    CheckPointType[] types() default CheckPointType.ALL;

    String value() default "";

    boolean ignoreIfNull() default false;
}
