package com.tiger.cores.exceptions;

import lombok.Getter;

@Getter
public class BusinessLogicException extends RuntimeException {

    private final BaseError errorCode;
    private final Object[] params;

    public BusinessLogicException(BaseError errorCode, Object... params) {
        super(errorCode.getMessageCode());
        this.errorCode = errorCode;
        this.params = params;
    }
}
