package com.tiger.cores.exceptions;

import lombok.Getter;

@Getter
public class AuthLogicException extends RuntimeException {

    private final ErrorCode errorCode;

    public AuthLogicException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
