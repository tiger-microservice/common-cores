package com.tiger.cores.aops.annotations;

import java.lang.annotation.*;

import com.tiger.cores.constants.enums.CheckPointType;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(PrePermissionEndpoints.class)
@Documented
public @interface PrePermissionEndpoint {
    CheckPointType[] types() default CheckPointType.ALL;

    String value() default "";

    boolean ignoreIfNull() default false;
}
