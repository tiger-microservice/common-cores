package com.tiger.cores.configs.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiger.cores.configs.locale.Translator;
import com.tiger.cores.dtos.responses.ApiResponse;
import com.tiger.cores.exceptions.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    final Translator translator;

    JwtAuthenticationEntryPoint(ObjectMapper objectMapper, Translator translator) {
        this.objectMapper = objectMapper;
        this.translator = translator;
    }

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<?> apiResponse = ApiResponse.responseError(
                errorCode.getHttpStatusCode().value(),
                errorCode.getMessageCode(),
                this.translator.toMessage(errorCode.getMessageCode()));

        // handler response format when request unauthorized
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}
