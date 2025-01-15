package com.tiger.cores.filters;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tiger.cores.constants.AppConstants;

@Order(1)
@Component
public class RequestIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Generate a unique request ID
            addRequestId(request, response);

            // Continue the filter chain
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(AppConstants.MDC_CORRELATION_ID);
        }
    }

    private void addRequestId(HttpServletRequest request, HttpServletResponse response) {
        // Generate a unique request ID
        String gatewayRequestId = request.getHeader(AppConstants.APP_REQUEST_ID);
        String requestIdService = getValueUUID();

        if (gatewayRequestId == null) {
            // Add the request ID to the response header
            gatewayRequestId = requestIdService;
        } else {
            gatewayRequestId = gatewayRequestId + " " + requestIdService;
        }

        MDC.put(AppConstants.MDC_CORRELATION_ID, gatewayRequestId);

        response.addHeader(AppConstants.APP_REQUEST_ID, gatewayRequestId);
    }

    private String getValueUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("-", "");
    }
}
