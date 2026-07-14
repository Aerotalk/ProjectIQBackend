package com.grivetyglobals.invoiceiq.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        long startTime = System.currentTimeMillis();
        
        // Log the incoming request
        log.info("📥 Incoming Request: {} {} | Client IP: {}", 
                request.getMethod(), 
                request.getRequestURI(), 
                request.getRemoteAddr());

        try {
            // Proceed with the request
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            
            // Log the response
            if (response.getStatus() >= 400 && response.getStatus() < 500) {
                log.warn("📤 Outgoing Response: {} | Status: {} | Time: {}ms", 
                        request.getRequestURI(), 
                        response.getStatus(), 
                        duration);
            } else if (response.getStatus() >= 500) {
                log.error("📤 Outgoing Response: {} | Status: {} | Time: {}ms", 
                        request.getRequestURI(), 
                        response.getStatus(), 
                        duration);
            } else {
                log.info("📤 Outgoing Response: {} | Status: {} | Time: {}ms", 
                        request.getRequestURI(), 
                        response.getStatus(), 
                        duration);
            }
        }
    }
}
