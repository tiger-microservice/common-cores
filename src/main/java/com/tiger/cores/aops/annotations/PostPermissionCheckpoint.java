package com.tiger.cores.aops.annotations;

import com.tiger.cores.constants.enums.CheckPointType;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(PostPermissionCheckpoints.class)
@Documented
public @interface PostPermissionCheckpoint {
    CheckPointType type();

    String value() default "";
}
