package com.tiger.cores;

import java.lang.annotation.*;

import org.springframework.context.annotation.Import;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({CommonCoreConfig.class})
public @interface EnableCommonCore {}
