package com.tiger.cores.aops.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.http.HttpStatus;

import com.tiger.cores.encryptors.securities.EncryptorHandler;
import com.tiger.cores.encryptors.securities.impl.AESRequestResponseHandler;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SecureEndpoint {

    boolean enableEncryptRequest() default true;

    boolean enableEncryptResponse() default true;

    HttpStatus[] ignoreResponseEncryptionForStatuses() default {};

    Class<? extends EncryptorHandler> handler() default AESRequestResponseHandler.class;
}
