package com.tiger.cores.filters;

import com.tiger.cores.constants.AppConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Locale;

@Order(3)
@Component
public class AcceptLanguageFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        LocaleContextHolder.setLocale(getLocale(request));

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }

    private Locale getLocale(HttpServletRequest request) {
        String localeStr = request.getHeader(AppConstants.ACCEPT_LANGUAGE);
        return Strings.isBlank(localeStr) ? Locale.US : Locale.of(localeStr);
    }
}
