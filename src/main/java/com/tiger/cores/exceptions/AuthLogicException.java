package com.tiger.cores.exceptions;

import lombok.Getter;

@Getter
public class AuthLogicException extends RuntimeException {

    private final BaseError errorCode;
    private final Object[] params;

    public AuthLogicException(BaseError errorCode, Object... params) {
        super(errorCode.getMessageCode());
        this.errorCode = errorCode;
        this.params = params;
    }
}
