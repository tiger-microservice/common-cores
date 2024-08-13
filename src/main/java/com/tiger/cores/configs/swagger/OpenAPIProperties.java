package com.tiger.cores.configs.swagger;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.openapi")
public class OpenAPIProperties {
    private String title;
    private String version;
    private String description;
    private String termsOfService;
    private String contactName;
    private String contactEmail;
    private String licenseName;
    private String licenseUrl;
    private String summary;

    @PostConstruct
    private void setDefaults() {
        if (title == null) {
            title = "Ecommerce APi"; // Default value
        }
        if (version == null) {
            version = "v1.0"; // Default value
        }
        if (description == null) {
            description = "Description of the API"; // Default value
        }
        if (termsOfService == null) {
            termsOfService = "http://swagger.io/terms/"; // Default value
        }
        if (contactName == null) {
            contactName = "Ta Duy Hoang"; // Default value
        }
        if (contactEmail == null) {
            contactEmail = "duyhoangptit@gmail.com"; // Default value
        }
        if (licenseName == null) {
            licenseName = "Apache 2.0"; // Default value
        }
        if (licenseUrl == null) {
            licenseUrl = "http://springdoc.org"; // Default value
        }
        if (summary == null) {
            summary = "summary"; // Default value
        }
    }
}
