package com.tiger.cores.exceptions;

import lombok.Getter;

@Getter
public class SecureLogicException extends RuntimeException {

    private final BaseError errorCode;

    public SecureLogicException(BaseError errorCode) {
        super(errorCode.getMessageCode());
        this.errorCode = errorCode;
    }
}
