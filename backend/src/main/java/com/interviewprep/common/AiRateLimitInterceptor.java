/**
 * FILE: backend/src/main/java/com/interviewprep/common/AiRateLimitInterceptor.java
 *
 * WHAT IT DOES:
 *   Applies the local AI-call quota before question generation or answer grading begins.
 *
 * ROLE IN THE PROJECT:
 *   Keeps provider usage control at the HTTP boundary and avoids trusting forwarded client-address headers.
 *
 * IF YOU REMOVE THIS FILE:
 *   AI endpoints no longer enforce the configured per-client call limit.
 *
 * DEPENDS ON: InMemoryRateLimiter and Spring MVC interception.
 * USED BY: WebConfiguration.
 *
 * KEY FUNCTIONS:
 *   - preHandle(): permits non-POST requests and limits direct-client POST requests.
 */
package com.interviewprep.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AiRateLimitInterceptor implements HandlerInterceptor {

    private final InMemoryRateLimiter rateLimiter;

    public AiRateLimitInterceptor(InMemoryRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!"POST".equals(request.getMethod())) {
            return true;
        }

        if (!rateLimiter.tryAcquire(request.getRemoteAddr())) {
            throw new RateLimitExceededException();
        }
        return true;
    }
}
