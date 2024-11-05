package com.tiger.cores.aops.annotations;

import java.lang.annotation.*;

import com.tiger.cores.constants.enums.VersionControlType;
import com.tiger.cores.entities.VersionAuditEntity;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface VersionControl {
    String entityType() default "";

    String key() default "";

    Class<? extends VersionAuditEntity> entityClass();

    VersionControlType type() default VersionControlType.GET;
}
