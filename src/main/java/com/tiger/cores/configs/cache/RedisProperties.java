package com.tiger.cores.configs.cache;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.redis")
public class RedisProperties {
    private String hostName;
    private int port;
    private String password;

    @PostConstruct
    private void setDefaults() {
        if (hostName == null) {
            hostName = "localhost"; // Default value
        }
        if (port == 0) {
            port = 6379; // Default value
        }
        if (password == null) {
            password = ""; // Default value
        }
    }
}
