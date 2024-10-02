package com.tiger.cores.encryptors.configs;

import com.tiger.cores.encryptors.converter.SupportEncryptedHttpMessageConverter;
import com.tiger.cores.encryptors.interceptors.SecureEndpointInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class EncryptorConfig implements WebMvcConfigurer {

    final SupportEncryptedHttpMessageConverter supportEncryptedHttpMessageConverter;
    final SecureEndpointInterceptor secureEndpointInterceptor;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(supportEncryptedHttpMessageConverter);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(secureEndpointInterceptor).addPathPatterns("/**");
    }
}
