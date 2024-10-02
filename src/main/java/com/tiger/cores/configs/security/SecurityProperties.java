package com.tiger.cores.configs.security;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {
    private Boolean configCustom;
    private String[] publicEndpoints;
    private String[] ignorePermissions;

    @PostConstruct
    private void setDefaults() {
        if (configCustom == null) {
            configCustom = false; // Default value
        }
        if (publicEndpoints == null) {
            publicEndpoints = new String[] {"/internal/**"}; // Default value
        }
        if (ignorePermissions == null) {
            ignorePermissions = new String[] {"/v1/account/permissions"}; // Default value
        }
    }
}
