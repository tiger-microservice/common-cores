package com.tiger.cores.encryptors.configs;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tiger.cores.encryptors.converter.SupportEncryptedHttpMessageConverter;
import com.tiger.cores.encryptors.interceptors.SecureEndpointInterceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class EncryptorConfig implements WebMvcConfigurer {

    final HttpServletRequest httpServletRequest;
    final SecureEndpointInterceptor secureEndpointInterceptor;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // add first priority
        converters.add(0, new SupportEncryptedHttpMessageConverter(httpServletRequest));

        log.info("Registered converters in order:");
        for (int i = 0; i < converters.size(); i++) {
            HttpMessageConverter<?> converter = converters.get(i);
            log.info(
                    "{}. {} - Supported media types: {}",
                    i,
                    converter.getClass().getSimpleName(),
                    converter.getSupportedMediaTypes());
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(secureEndpointInterceptor).addPathPatterns("/**");
    }
}
