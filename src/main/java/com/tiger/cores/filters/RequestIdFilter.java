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
        String requestId = request.getHeader(AppConstants.APP_REQUEST_ID);
        String requestIdService = UUID.randomUUID().toString();

        if (requestId == null) {
            // Add the request ID to the response header
            requestId = requestIdService;
        } else {
            requestId = requestId + " " + requestIdService;
        }

        MDC.put(AppConstants.MDC_CORRELATION_ID, requestIdService);

        response.addHeader(AppConstants.APP_REQUEST_ID, requestId);
    }
}
