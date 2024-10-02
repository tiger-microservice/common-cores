package com.tiger.cores.configs.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiger.cores.configs.locale.Translator;
import com.tiger.cores.constants.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

// refer guideline https://viblo.asia/p/spring-boot-huong-dan-tao-bean-co-dieu-kien-voi-atconditional-gDVK2270KLj
@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnProperty(
        value = "app.security.config-custom",
        havingValue = "false", // Nếu giá trị app.security.config.custom  = true thì Bean mới được khởi tạo
        matchIfMissing =
                false) // matchIFMissing là giá trị mặc định nếu không tìm thấy property app.security.config.custom
public class SecurityConfig {

    final Translator translator;
    final ObjectMapper objectMapper;
    final CustomJwtDecoder customJwtDecoder;
    final SecurityProperties securityProperties;

    @Value("${app.cross-origin:false}")
    private Boolean crossOrigin;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // setting api method post ko can auth
        httpSecurity.authorizeHttpRequests(request ->
                // permit all public endpoint
                request.requestMatchers(HttpMethod.POST, securityProperties.getPublicEndpoints())
                        .permitAll()
                        // permit all swagger
                        .requestMatchers("/v3/**", "/swagger-ui/**", "/actuator/*")
                        .permitAll()
                        // any request need to check auth
                        .anyRequest()
                        .authenticated());

        // setting oauth2 resource server
        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
                        .decoder(customJwtDecoder)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint(objectMapper, translator)));

        // HttpSecurity config cors
        log.info(crossOrigin ? "Enable cross origin" : "Off cross origin");
        if (Boolean.TRUE.equals(crossOrigin)) {
            httpSecurity.cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*"));
                configuration.setAllowedMethods(List.of("*"));
                configuration.setAllowedHeaders(List.of("*"));
                return configuration;
            }));
        }

        // setting session management
        httpSecurity.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // todo: off csrf
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(""); // setting default value prefix is empty
        // customer key authorizations
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimDelimiter(AppConstants.SPACE);
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(AppConstants.JwtKey.CUSTOM_KEY_SCOPE);

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
}
