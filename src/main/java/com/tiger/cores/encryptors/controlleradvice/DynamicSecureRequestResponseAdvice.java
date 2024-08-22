package com.tiger.cores.encryptors.controlleradvice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonInputMessage;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiger.cores.aops.annotations.SecureEndpoint;
import com.tiger.cores.encryptors.constants.HttpRequestAttributeConstants;
import com.tiger.cores.encryptors.securities.EncryptorHandler;
import com.tiger.cores.encryptors.securities.EncryptorHandlerFactory;
import com.tiger.cores.encryptors.securities.impl.AESRequestResponseHandler;
import com.tiger.cores.exceptions.ErrorCode;
import com.tiger.cores.exceptions.SecureLogicException;
import com.tiger.cores.utils.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class DynamicSecureRequestResponseAdvice extends RequestBodyAdviceAdapter implements ResponseBodyAdvice<Object> {

    final ObjectMapper objectMapper;
    final HttpServletRequest httpServletRequest;
    final HttpServletResponse httpServletResponse;
    final EncryptorHandlerFactory encryptorHandlerFactory;

    private static final List<MediaType> ALLOW_REQUEST_MEDIA_TYPES = Arrays.asList(
            MediaType.APPLICATION_JSON,
            MediaType.TEXT_PLAIN,
            new MediaType("application", "*+json", StandardCharsets.UTF_8),
            new MediaType("text", "*+plan", StandardCharsets.UTF_8));

    private static final List<MediaType> ALLOW_RESPONSE_MEDIA_TYPES = Arrays.asList(
            MediaType.APPLICATION_JSON,
            MediaType.TEXT_PLAIN,
            MediaType.TEXT_HTML,
            MediaType.TEXT_XML,
            new MediaType("application", "*+json", StandardCharsets.UTF_8),
            new MediaType("text", "plain", StandardCharsets.UTF_8));

    @Override
    public boolean supports(
            MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        SecureEndpoint secureEndpoint = getSecureEndpoint();
        if (Objects.isNull(secureEndpoint)) {
            return false;
        }

        return secureEndpoint.enableEncryptRequest();
    }

    @Override
    public HttpInputMessage beforeBodyRead(
            HttpInputMessage inputMessage,
            MethodParameter parameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType)
            throws IOException {
        // check header content type
        HttpHeaders httpHeaders = inputMessage.getHeaders();
        if (Objects.isNull(httpHeaders)) {
            throw new SecureLogicException(ErrorCode.SECURE_INVALID);
        }

        if (ALLOW_REQUEST_MEDIA_TYPES.stream()
                .noneMatch(mediaType -> mediaType.isCompatibleWith(httpHeaders.getContentType()))) {
            return inputMessage;
        }

        SecureEndpoint secureEndpoint = getSecureEndpoint();
        if (Objects.isNull(secureEndpoint)) {
            return inputMessage;
        }

        EncryptorHandler encryptorHandler = encryptorHandlerFactory.getEncryptorHandler(secureEndpoint.handler());
        byte[] body = StreamUtils.copyToByteArray(inputMessage.getBody());
        String bodyRaw = new String(body);

        if (StringUtils.isBlank(bodyRaw)) {
            return inputMessage;
        }

        String decryptRequestStr = encryptorHandler.decrypt(bodyRaw);
        log.info("[beforeBodyRead] value {}", decryptRequestStr);
        byte[] data = StringUtils.isNoneBlank(decryptRequestStr) ? decryptRequestStr.getBytes() : new byte[] {};

        return new MappingJacksonInputMessage(new ByteArrayInputStream(data), httpHeaders);
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        SecureEndpoint secureEndpoint = getSecureEndpoint();
        if (Objects.isNull(secureEndpoint)) {
            return false;
        }

        return secureEndpoint.enableEncryptResponse()
                && Arrays.stream(secureEndpoint.ignoreResponseEncryptionForStatuses())
                        .filter(Objects::nonNull)
                        .noneMatch(httpStatus -> Objects.equals(httpStatus.value(), httpServletResponse.getStatus()));
    }

    private SecureEndpoint getSecureEndpoint() {
        Object annotationObject =
                httpServletRequest.getAttribute(HttpRequestAttributeConstants.SECURED_ANNOTATION_ATTR);
        if (Objects.isNull(annotationObject)) {
            return null;
        }

        return ((SecureEndpoint) annotationObject);
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {
        // check header content type
        if (ALLOW_RESPONSE_MEDIA_TYPES.stream().noneMatch(mediaType -> mediaType.isCompatibleWith(selectedContentType))
                || Objects.isNull(body)) {
            return body;
        }

        SecureEndpoint secureEndpoint = getSecureEndpoint();
        EncryptorHandler encryptorHandler = encryptorHandlerFactory.getEncryptorHandler(secureEndpoint.handler());
        String rawResponse = this.getRawResponse(body);

        var encrypted = encryptorHandler.encrypt(rawResponse);
        if (encrypted instanceof String
                && Boolean.FALSE.equals(encryptorHandler instanceof AESRequestResponseHandler)) {
            response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
        }
        if (encrypted instanceof String
                && JsonUtil.isValidJsonFormat((String) encrypted)
                && encryptorHandler instanceof AESRequestResponseHandler) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        }

        return encrypted;
    }

    private String getRawResponse(Object response) {
        if (Objects.isNull(response)) {
            return null;
        }

        if (response instanceof String) {
            return (String) response;
        }

        return JsonUtil.castToString(response);
    }
}
