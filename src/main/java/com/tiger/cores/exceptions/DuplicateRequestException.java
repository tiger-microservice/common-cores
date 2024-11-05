package com.tiger.cores.exceptions;

import lombok.Getter;

@Getter
public class DuplicateRequestException extends RuntimeException {

    private final String message;
    private final BaseError errorCode;
    private final Object[] params;

    public DuplicateRequestException(BaseError errorCode, Object... params) {
        super(errorCode.getMessageCode());
        this.errorCode = errorCode;
        this.params = params;
        this.message = null;
    }

    public DuplicateRequestException(String message) {
        super(message);
        this.message = message;
        this.errorCode = null;
        this.params = null;
    }
}
