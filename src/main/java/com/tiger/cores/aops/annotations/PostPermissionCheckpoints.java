package com.tiger.cores.aops.annotations;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface PostPermissionCheckpoints {
    PostPermissionCheckpoint[] value();
}
