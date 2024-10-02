package com.tiger.cores.dtos.responses;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ApiResponse<T> {

    private int status;
    private T data;
    private String messageCode;
    private String message;

    public static <T> ApiResponse<T> responseOK(T data) {
        return ApiResponse.<T>builder().status(HttpStatus.OK.value()).data(data).build();
    }

    public static <T> ApiResponse<T> responseOK() {
        return ApiResponse.<T>builder().status(HttpStatus.OK.value()).build();
    }

    public static <T> ApiResponse<T> responseError(int status, String messageCode, String message) {
        return ApiResponse.<T>builder()
                .status(status)
                .messageCode(messageCode)
                .message(message)
                .build();
    }
}
