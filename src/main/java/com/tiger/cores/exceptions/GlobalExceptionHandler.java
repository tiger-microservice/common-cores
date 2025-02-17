package com.tiger.cores.exceptions;

import static org.springframework.http.HttpStatus.LOCKED;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.Objects;

import jakarta.validation.ConstraintViolation;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tiger.cores.configs.locale.Translator;
import com.tiger.cores.dtos.responses.ApiResponse;
import com.tiger.cores.utils.MessageUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final Translator translator;

    @ExceptionHandler(value = StaleDataException.class)
    ResponseEntity<ApiResponse<Object>> handlingStaleDataException(StaleDataException exception) {
        BaseError errorCode = exception.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatusCode().value())
                .body(ApiResponse.responseError(
                        errorCode.getHttpStatusCode().value(),
                        errorCode.getMessageCode(),
                        translator.toMessage(errorCode.getMessageCode(), exception.getParams())));
    }

    @ExceptionHandler(value = DuplicateRequestException.class)
    ResponseEntity<ApiResponse<Object>> handlingDuplicateRequestException(DuplicateRequestException exception) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ApiResponse.responseError(
                        HttpStatus.TOO_MANY_REQUESTS.value(),
                        HttpStatus.TOO_MANY_REQUESTS.name(),
                        exception.getMessage()));
    }

    @ExceptionHandler(value = RateLimitExceededException.class)
    ResponseEntity<ApiResponse<Object>> handlingRateLimitExceededException(RateLimitExceededException exception) {
        return ResponseEntity.status(LOCKED)
                .body(ApiResponse.responseError(LOCKED.value(), LOCKED.name(), exception.getMessage()));
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<Object>> handlingAccessDeniedException(AccessDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.responseError(
                        HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.name(), exception.getMessage()));
    }

    @ExceptionHandler(value = OptimisticLockingFailureException.class)
    ResponseEntity<ApiResponse<Object>> handlingAccessDeniedException(OptimisticLockingFailureException exception) {
        return handlingException(exception);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Object>> handlingMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {
        String enumKey = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        BaseError errorCode = ErrorCode.INVALID_KEY;
        Map<String, Object> attributes = null;

        try {
            errorCode = ErrorCode.valueOf(enumKey);

            var constraintViolation =
                    exception.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);
            attributes = constraintViolation.getConstraintDescriptor().getAttributes();
            log.info(attributes.toString());
        } catch (Exception e) {
            log.error("[handlingMethodArgumentNotValidException] error {}", e.getMessage(), e);
        }

        return ResponseEntity.badRequest()
                .body(ApiResponse.responseError(
                        errorCode.getHttpStatusCode().value(),
                        errorCode.getMessageCode(),
                        Objects.nonNull(attributes)
                                ? MessageUtils.mapAttributes(errorCode.getMessageCode(), attributes)
                                : errorCode.getMessageCode()));
    }

    @ExceptionHandler(value = BusinessLogicException.class)
    ResponseEntity<ApiResponse<Object>> handlingBusinessLogicException(BusinessLogicException exception) {
        BaseError errorCode = exception.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatusCode().value())
                .body(ApiResponse.responseError(
                        errorCode.getHttpStatusCode().value(),
                        errorCode.getMessageCode(),
                        translator.toMessage(errorCode.getMessageCode(), exception.getParams())));
    }

    @ExceptionHandler(value = SecureLogicException.class)
    ResponseEntity<ApiResponse<Object>> handlingSecureLogicException(SecureLogicException exception) {
        BaseError errorCode = exception.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatusCode().value())
                .body(ApiResponse.responseError(
                        errorCode.getHttpStatusCode().value(),
                        errorCode.getMessageCode(),
                        translator.toMessage(errorCode.getMessageCode(), exception.getParams())));
    }

    @ExceptionHandler(value = AuthLogicException.class)
    ResponseEntity<ApiResponse<Object>> handlingAuthLogicException(AuthLogicException exception) {
        BaseError errorCode = exception.getErrorCode();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.responseError(
                        errorCode.getHttpStatusCode().value(),
                        errorCode.getMessageCode(),
                        translator.toMessage(errorCode.getMessageCode(), exception.getParams())));
    }

    @ExceptionHandler(value = AuthorizationDeniedException.class)
    ResponseEntity<ApiResponse<Object>> handlingAuthorizationDeniedException(AuthorizationDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.responseError(
                        HttpStatus.FORBIDDEN.value(),
                        HttpStatus.FORBIDDEN.name(),
                        HttpStatus.FORBIDDEN.getReasonPhrase()));
    }

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<Object>> handlingException(Exception exception) {
        log.error("[handlingException] error {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.responseError(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.name(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
    }
}
