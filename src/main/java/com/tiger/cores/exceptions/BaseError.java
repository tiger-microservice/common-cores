package com.tiger.cores.exceptions;

import org.springframework.http.HttpStatusCode;

public interface BaseError {
    String getMessageCode();

    HttpStatusCode getHttpStatusCode();
}
