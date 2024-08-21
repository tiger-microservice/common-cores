package com.tiger.cores.encryptors.securities;

import java.util.List;

import org.springframework.stereotype.Component;

import com.tiger.cores.exceptions.ErrorCode;
import com.tiger.cores.exceptions.SecureLogicException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EncryptorHandlerFactory {

    private final List<EncryptorHandler> handlers;

    public EncryptorHandler getEncryptorHandler(Class<?> handlerClass) {
        return this.handlers.stream()
                .filter(item -> item.getClass().equals(handlerClass))
                .findFirst()
                .orElseThrow(() -> new SecureLogicException(ErrorCode.SECURE_INVALID));
    }
}
