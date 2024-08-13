package com.tiger.cores.configs.security;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {
    private Boolean configCustom;
    private String[] publicEndpoints;

    @PostConstruct
    private void setDefaults() {
        if (configCustom == null) {
            configCustom = false; // Default value
        }
        if (publicEndpoints == null) {
            publicEndpoints = new String[] {"/internal/**"}; // Default value
        }
    }
}
