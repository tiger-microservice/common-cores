package com.tiger.cores.configs.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OpenAPIConfig {

    public static final String BEARER_AUTH = "bearerAuth";

    final OpenAPIProperties openAPIProperties;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(openAPIProperties.getTitle())
                        .version(openAPIProperties.getVersion())
                        .description(openAPIProperties.getDescription())
                        .termsOfService(openAPIProperties.getTermsOfService())
                        .contact(new Contact()
                                .name(openAPIProperties.getContactName())
                                .email(openAPIProperties.getContactEmail()))
                        .license(new License()
                                .name(openAPIProperties.getLicenseName())
                                .url(openAPIProperties.getLicenseUrl())))
                // .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH)) // add require
                // security for all api
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(
                                BEARER_AUTH,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
