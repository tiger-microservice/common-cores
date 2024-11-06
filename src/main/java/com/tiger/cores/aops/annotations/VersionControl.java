package com.tiger.cores.aops.annotations;

import java.lang.annotation.*;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tiger.cores.constants.enums.VersionControlType;

/**
 * claim_id::version
 * etIfAbsent(key="claim_id_100", value="locked_by:'user-1', locked_at: 22/10/2024 10:05:05")
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface VersionControl {
    String objectIdKey() default "";

    String objectVersionKey() default "";

    Class<? extends JpaRepository> repositoryClass() default JpaRepository.class;

    VersionControlType type() default VersionControlType.GET;
}
