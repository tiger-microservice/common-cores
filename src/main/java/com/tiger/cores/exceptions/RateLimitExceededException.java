package com.tiger.cores.exceptions;

import lombok.Getter;

@Getter
public class RateLimitExceededException extends RuntimeException {
    private final BaseError errorCode;

    public RateLimitExceededException(BaseError errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
