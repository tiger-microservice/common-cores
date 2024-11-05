package com.tiger.cores.encryptors.converter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tiger.cores.encryptors.constants.HttpRequestAttributeConstants;
import com.tiger.cores.utils.JsonUtil;

import io.micrometer.core.instrument.util.IOUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Support convert value
 */
@Slf4j
// @Component // add to global handler converter for all rest template
public class SupportEncryptedHttpMessageConverter extends AbstractGenericHttpMessageConverter<Object> {

    final HttpServletRequest httpServletRequest;

    public SupportEncryptedHttpMessageConverter(HttpServletRequest httpServletRequest) {
        super(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN);
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
        return this.hasAttribute(HttpRequestAttributeConstants.SECURED_IS_REQUEST_ENCRYPTED);
    }

    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        return this.hasAttribute(HttpRequestAttributeConstants.SECURED_IS_RESPONSE_ENCRYPTED);
    }

    @Override
    protected void writeInternal(Object o, Type type, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        log.info("[writeInternal] value {}", o);
        byte[] body = (o instanceof String) ? ((String) o).getBytes(StandardCharsets.UTF_8) : JsonUtil.toByteArray(o);
        outputMessage.getBody().write(body);
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedEncodingException();
    }

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        if (Objects.equals(type, String.class)) {
            return IOUtils.toString(inputMessage.getBody());
        }

        return JsonUtil.toObject(inputMessage.getBody(), new TypeReference<>() {
            @Override
            public Type getType() {
                return type;
            }
        });
    }

    private boolean hasAttribute(String httpAttribute) {
        var isEnableEncrypt = httpServletRequest.getAttribute(httpAttribute);
        return Objects.nonNull(isEnableEncrypt) && (Boolean) isEnableEncrypt;
    }
}
