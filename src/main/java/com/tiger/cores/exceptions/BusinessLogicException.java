package com.tiger.cores.exceptions;

import lombok.Getter;

@Getter
public class BusinessLogicException extends RuntimeException {

    private final BaseError errorCode;

    public BusinessLogicException(BaseError errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
