package com.tiger.cores.configs.logging;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LogbackConfiguration {

    @Value("${spring.application.name:unknown-app}")
    private String appName;

    @PostConstruct
    public void configureLogback() {
        // Gán giá trị vào Logback
        ch.qos.logback.classic.LoggerContext context =
                (ch.qos.logback.classic.LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
        context.putProperty("APPLICATION_NAME", appName);
    }
}
