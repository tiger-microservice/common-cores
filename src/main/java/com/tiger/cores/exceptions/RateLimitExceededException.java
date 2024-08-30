package com.tiger.cores.exceptions;

import lombok.Getter;

@Getter
public class RateLimitExceededException extends RuntimeException {
    private final BaseError errorCode;
    private final Object[] params;

    public RateLimitExceededException(BaseError errorCode, Object... params) {
        super(errorCode.getMessageCode());
        this.errorCode = errorCode;
        this.params = params;
    }
}
