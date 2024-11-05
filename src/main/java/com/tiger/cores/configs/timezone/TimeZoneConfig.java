package com.tiger.cores.configs.timezone;

import java.util.TimeZone;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "app.timezone.config.enable", havingValue = "true", matchIfMissing = true)
public class TimeZoneConfig {

    @Value("${app.timezone.value:UTC}")
    private String timeZone;

    @PostConstruct
    public void init() {
        log.info("[TimeZoneConfig][init] timezone {}", timeZone);
        // Setting Spring Boot SetTimeZone
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    }
}
