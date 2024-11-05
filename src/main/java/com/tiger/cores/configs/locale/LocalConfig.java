package com.tiger.cores.configs.locale;

import java.util.Locale;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(
        value = "app.locale.config.enable",
        havingValue = "true", // Nếu giá trị app.redisson.config  = true thì Bean mới được khởi tạo
        matchIfMissing = true) // matchIFMissing là giá trị mặc định nếu không tìm thấy property app.redisson.config
public class LocalConfig {

    private final LocalProperties localProperties;

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US);
        return localeResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    @Bean
    public ResourceBundleMessageSource bundleMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(localProperties.getBaseNames());
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600);
        messageSource.setDefaultLocale(Locale.of(localProperties.getDefaultLocale()));
        messageSource.setAlwaysUseMessageFormat(localProperties.getAlwaysUseMessageFormat());
        messageSource.setUseCodeAsDefaultMessage(localProperties.getUseCodeAsDefault());
        return messageSource;
    }
}
