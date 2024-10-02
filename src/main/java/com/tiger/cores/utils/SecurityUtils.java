package com.tiger.cores.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Map;

public final class SecurityUtils {

    public static String getClaim(String claimName) {
        Map<String, Object> claims = getClaims();

        if (claims != null && claims.containsKey(claimName)) {
            return claims.get(claimName).toString();
        }

        throw new IllegalArgumentException("Claim not found: " + claimName);
    }

    public static Map<String, Object> getClaims() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new IllegalStateException("No authentication information found");
        }

        if (authentication instanceof JwtAuthenticationToken) {
            Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
            return jwt.getClaims();
        }

        throw new IllegalStateException("Authentication is not a JWT token");
    }
}
