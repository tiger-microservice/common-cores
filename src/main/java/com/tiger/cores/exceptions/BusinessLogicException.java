package com.tiger.cores.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class BusinessLogicException extends RuntimeException {

    private final String message;
    private final BaseError errorCode;
    private final Object[] params;

    public BusinessLogicException(BaseError errorCode, Object... params) {
        super(errorCode.getMessageCode());
        this.errorCode = errorCode;
        this.params = params;
        this.message = null;
    }

    public BusinessLogicException(String message) {
        super(message);
        this.message = message;
        this.errorCode = null;
        this.params = null;
    }
}
