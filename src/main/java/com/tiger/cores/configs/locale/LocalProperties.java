package com.tiger.cores.configs.locale;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.locale")
public class LocalProperties {
    private String[] baseNames;
    private String defaultLocale;
    private Boolean alwaysUseMessageFormat;
    private Boolean useCodeAsDefault;

    @PostConstruct
    private void setDefaults() {
        if (baseNames == null) {
            baseNames = new String[] {"i18n/messages", "i18n/core-messages"}; // Default value
        }
        if (defaultLocale == null) {
            defaultLocale = "en"; // English
        }
        if (alwaysUseMessageFormat == null) {
            alwaysUseMessageFormat = true; // English
        }
        if (useCodeAsDefault == null) {
            useCodeAsDefault = true;
            ; // English
        }
    }
}
