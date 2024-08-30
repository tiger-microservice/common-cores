package com.tiger.cores.exceptions;

import lombok.Getter;

@Getter
public class SecureLogicException extends RuntimeException {

    private final BaseError errorCode;
    private final Object[] params;

    public SecureLogicException(BaseError errorCode, Object... params) {
        super(errorCode.getMessageCode());
        this.errorCode = errorCode;
        this.params = params;
    }
}
