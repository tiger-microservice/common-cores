package com.tiger.cores;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({CommonCoreConfig.class})
public @interface EnableCommonCore {}
