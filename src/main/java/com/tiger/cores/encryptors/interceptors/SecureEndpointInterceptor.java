package com.tiger.cores.encryptors.interceptors;

import com.tiger.cores.aops.annotations.SecureEndpoint;
import com.tiger.cores.encryptors.constants.HttpRequestAttributeConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

@Component
public class SecureEndpointInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            SecureEndpoint secureEndpoint = handlerMethod.getMethodAnnotation(SecureEndpoint.class);
            if (Objects.nonNull(secureEndpoint)) {
                request.setAttribute(HttpRequestAttributeConstants.SECURED_ANNOTATION_ATTR, secureEndpoint);
                request.setAttribute(
                        HttpRequestAttributeConstants.SECURED_IS_REQUEST_ENCRYPTED,
                        secureEndpoint.enableEncryptRequest());
                request.setAttribute(
                        HttpRequestAttributeConstants.SECURED_IS_RESPONSE_ENCRYPTED,
                        secureEndpoint.enableEncryptResponse());
            }
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
