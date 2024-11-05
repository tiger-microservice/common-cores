package com.tiger.cores.configs.redission;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.redisson")
public class RedissonProperties {
    private String address;
    private String password;
    private int connectionPoolSize;
    private int idleConnectionTimeout;
    private int pingTimeout;
    private int timeout;

    // address: "redis://127.0.0.1:6379" # Change to your Redis server address
    //  password: "your_password" # Optional, if your Redis server requires a password
    //  connectionPoolSize: 10
    //  idleConnectionTimeout: 10000
    //  pingTimeout: 1000
    //  timeout: 3000

    @PostConstruct
    private void setDefaults() {
        if (address == null) {
            address = "redis://localhost:6379"; // Default value
        }
        if (connectionPoolSize <= 0) {
            connectionPoolSize = 10000;
        }
        if (idleConnectionTimeout <= 0) {
            idleConnectionTimeout = 10;
        }
        if (pingTimeout <= 0) {
            pingTimeout = 1000;
        }
        if (timeout <= 0) {
            timeout = 3000;
        }
    }
}
