/**
 * FILE: backend/src/main/java/com/interviewprep/common/RequestIdFilter.java
 *
 * WHAT IT DOES:
 *   Generates one opaque request ID per HTTP request and returns it in a response header.
 *
 * ROLE IN THE PROJECT:
 *   Lets users report an error safely without logging job descriptions, answers, or secrets.
 *
 * IF YOU REMOVE THIS FILE:
 *   Safe API errors no longer include a request ID that can be used for diagnosis.
 *
 * DEPENDS ON: Spring's OncePerRequestFilter and the servlet API.
 * USED BY: Every backend HTTP request and ApiExceptionHandler.
 *
 * KEY FUNCTIONS:
 *   - doFilterInternal(): creates and stores the request ID before the request reaches MVC.
 *   - getRequestId(request): reads the generated ID for a safe error response.
 */
package com.interviewprep.common;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

    public static final String ATTRIBUTE_NAME = RequestIdFilter.class.getName() + ".requestId";
    public static final String HEADER_NAME = "X-Request-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        request.setAttribute(ATTRIBUTE_NAME, requestId);
        response.setHeader(HEADER_NAME, requestId);
        filterChain.doFilter(request, response);
    }

    public static String getRequestId(HttpServletRequest request) {
        Object value = request.getAttribute(ATTRIBUTE_NAME);
        return value instanceof String requestId ? requestId : "unavailable";
    }
}
