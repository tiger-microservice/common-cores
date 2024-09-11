package com.tiger.cores.aops.annotations;

import java.lang.annotation.*;

import com.tiger.cores.constants.enums.CheckPointType;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(PostPermissionCheckpoints.class)
@Documented
public @interface PostPermissionCheckpoint {
    CheckPointType type();

    String value() default "";
}
