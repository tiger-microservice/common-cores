package com.tiger.cores.configs.logging;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.log.function")
public class LoggingProperties {
    private Boolean enable;
    private String[] markKeys;

    @PostConstruct
    private void setDefaults() {
        if (enable == null) {
            enable = true; // Default value
        }
        if (markKeys == null) {
            markKeys = new String[] {"otp", "accessToken", "refreshToken", "password", "credential", "token"
            }; // Default value
        }
    }
}
