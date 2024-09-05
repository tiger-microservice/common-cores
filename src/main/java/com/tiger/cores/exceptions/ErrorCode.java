package com.tiger.cores.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode implements BaseError {
    INVALID_KEY("MCM00001", HttpStatus.UNAUTHORIZED),
    RESOURCE_NOT_FOUND("MCM00006", HttpStatus.NOT_FOUND),
    BEAN_NOT_DEFINED("MCM00002", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED("MCM00003", HttpStatus.UNAUTHORIZED),
    USER_NOT_EXISTED("MCM00008", HttpStatus.FORBIDDEN),
    RATE_LIMIT_ERROR("MCM00007", HttpStatus.BAD_REQUEST),
    SECURE_INVALID("MCM00004", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION("MCM00009", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_DONT_HAVE_PERMISSION("MCM00005", HttpStatus.FORBIDDEN),
    TOKEN_INVALID("MCM00010", HttpStatus.FORBIDDEN),
    MAX_TERMS_RETRY("MCM00011", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(String messageCode, HttpStatusCode statusCode) {
        this.messageCode = messageCode;
        this.statusCode = statusCode;
    }

    private String messageCode;
    private HttpStatusCode statusCode;

    @Override
    public HttpStatusCode getHttpStatusCode() {
        return this.statusCode;
    }
}
