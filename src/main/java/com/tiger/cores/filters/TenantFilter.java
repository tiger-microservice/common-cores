package com.tiger.cores.filters;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tiger.cores.configs.databases.TenantContext;
import com.tiger.cores.constants.AppConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(3)
@Component
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String tenantId = request.getHeader(AppConstants.APP_TENANT_ID);
        log.info("[TenantFilter] url {} tenantId {}", request.getContextPath(), tenantId);
        TenantContext.setCurrentTenant(tenantId);

        try {
            // Continue the filter chain
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
