package com.tiger.cores.configs.databases;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.tenant")
public class TenantProperties {

    private String defaultTenant;
    private List<String> tenants;
    private String[] packageToScans;
}