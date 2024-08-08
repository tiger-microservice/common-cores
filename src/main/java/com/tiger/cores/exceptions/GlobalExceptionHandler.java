package com.tiger.cores.exceptions;

import static org.springframework.http.HttpStatus.LOCKED;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.Objects;

import jakarta.validation.ConstraintViolation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tiger.common.utils.MessageUtils;
import com.tiger.cores.configs.locale.Translator;
import com.tiger.cores.dtos.responses.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final Translator translator;

    @ExceptionHandler(value = RateLimitExceededException.class)
    ResponseEntity<ApiResponse<Object>> handlingRateLimitExceededException(RateLimitExceededException exception) {
        return ResponseEntity.status(LOCKED).body(ApiResponse.responseError(LOCKED.value(), exception.getMessage()));
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<Object>> handlingAccessDeniedException(AccessDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.responseError(HttpStatus.FORBIDDEN.value(), exception.getMessage()));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Object>> handlingMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {
        String enumKey = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String, Object> attributes = null;

        try {
            errorCode = ErrorCode.valueOf(enumKey);

            var constraintViolation =
                    exception.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);
            attributes = constraintViolation.getConstraintDescriptor().getAttributes();
            log.info(attributes.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return ResponseEntity.badRequest()
                .body(ApiResponse.responseError(
                        errorCode.getCode(),
                        Objects.nonNull(attributes)
                                ? MessageUtils.mapAttributes(errorCode.getMessage(), attributes)
                                : errorCode.getMessage()));
    }

    @ExceptionHandler(value = BusinessLogicException.class)
    ResponseEntity<ApiResponse<Object>> handlingBusinessLogicException(BusinessLogicException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.responseError(errorCode.getCode(), translator.toMessage(errorCode.getMessage())));
    }

    @ExceptionHandler(value = AuthLogicException.class)
    ResponseEntity<ApiResponse<Object>> handlingAuthLogicException(AuthLogicException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.responseError(errorCode.getCode(), translator.toMessage(exception.getMessage())));
    }

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<Object>> handlingException(Exception exception) {
        log.error("[handlingException] error {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.responseError(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
    }
}
