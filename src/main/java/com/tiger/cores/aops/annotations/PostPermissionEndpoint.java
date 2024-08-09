package com.tiger.cores.aops.annotations;

import java.lang.annotation.*;

import com.tiger.cores.constants.enums.CheckPointType;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(PostPermissionEndpoints.class)
@Documented
public @interface PostPermissionEndpoint {
    CheckPointType type();

    String value() default "";
}
