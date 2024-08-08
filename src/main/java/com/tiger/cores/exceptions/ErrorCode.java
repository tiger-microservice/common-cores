package com.tiger.cores.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_KEY(400, "Invalid key", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND(404, "Resource not found", HttpStatus.NOT_FOUND),

    USER_NOT_EXISTED(404, "User not existed", HttpStatus.FORBIDDEN),
    USERNAME_INVALID(403, "Username invalid format rule", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(401, "Unauthorized", HttpStatus.UNAUTHORIZED),
    UNCATEGORIZED_EXCEPTION(500, "Server error", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_INVALID(400, "Email max length invalid", HttpStatus.BAD_REQUEST),
    EMAIL_FORMAT_INVALID(400, "Email invalid format", HttpStatus.BAD_REQUEST),
    EMAIL_EMPTY_INVALID(400, "Email is not empty", HttpStatus.BAD_REQUEST),
    PASSWORD_MAXLENGTH_INVALID(400, "Password max length invalid", HttpStatus.BAD_REQUEST),

    // permission
    PERMISSION_CODE_INVALID(400, "Permission code invalid", HttpStatus.BAD_REQUEST),
    PERMISSION_NAME_MAXLENGTH_INVALID(400, "Permission name max length invalid", HttpStatus.BAD_REQUEST),
    PERMISSION_DESCRIPTION_MAXLENGTH_INVALID(400, "Permission description max length invalid", HttpStatus.BAD_REQUEST),

    // role
    ROLE_NAME_MAXLENGTH_INVALID(400, "Role name max length invalid", HttpStatus.BAD_REQUEST),
    ROLE_DESCRIPTION_MAXLENGTH_INVALID(400, "Role description max length invalid", HttpStatus.BAD_REQUEST),
    ROLE_CODE_INVALID(400, "Role code invalid", HttpStatus.BAD_REQUEST),

    // account user
    USER_EXIST_INVALID(400, "Username exists", HttpStatus.BAD_REQUEST);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
