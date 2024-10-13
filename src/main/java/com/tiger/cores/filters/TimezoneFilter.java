package com.tiger.cores.filters;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tiger.cores.constants.AppConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(2)
@Component
public class TimezoneFilter extends OncePerRequestFilter {

    private static final ThreadLocal<String> timeZoneContext = new ThreadLocal<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String timeZone = request.getHeader(AppConstants.APP_TIME_ZONE);
        log.info("url {} timeZone {}", request.getContextPath(), timeZone);
        timeZoneContext.set(timeZone);

        try {
            // Continue the filter chain
            filterChain.doFilter(request, response);
        } finally {
            clear();
        }
    }

    public static String getTimeZone() {
        return timeZoneContext.get();
    }

    public static void clear() {
        timeZoneContext.remove();
    }
}
