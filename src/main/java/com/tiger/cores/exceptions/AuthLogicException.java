package com.tiger.cores.exceptions;

import lombok.Getter;

@Getter
public class AuthLogicException extends RuntimeException {

    private final BaseError errorCode;

    public AuthLogicException(BaseError errorCode) {
        super(errorCode.getMessageCode());
        this.errorCode = errorCode;
    }
}
