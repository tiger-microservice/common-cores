package com.tiger.cores.aops;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.tiger.cores.aops.annotations.SecureField;
import com.tiger.cores.services.impl.SecureService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SecureFieldAspect {

    @Around(value = "@annotation(secureField)  && args(request)")
    public Object secureMethod(ProceedingJoinPoint joinPoint, SecureField secureField, Object request)
            throws Throwable {

        String[] fieldsToEncrypt = secureField.input();
        String[] fieldsToDecrypt = secureField.output();
        // Encode request
        SecureService.encryptFields(request, fieldsToEncrypt);
        // Call main business
        Object result = joinPoint.proceed();
        // Decode response
        SecureService.decryptFields(result, fieldsToDecrypt);
        return result;
    }
}
