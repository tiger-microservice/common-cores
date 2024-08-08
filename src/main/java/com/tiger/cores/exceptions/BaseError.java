package com.tiger.cores.exceptions;

import org.springframework.http.HttpStatusCode;

public interface BaseError {
    int getCode();

    String getMessage();

    HttpStatusCode getHttpStatusCode();
}
