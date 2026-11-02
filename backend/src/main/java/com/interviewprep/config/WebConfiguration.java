/**
 * FILE: backend/src/main/java/com/interviewprep/config/WebConfiguration.java
 *
 * WHAT IT DOES:
 *   Attaches the AI rate-limit interceptor to the two endpoints that make provider calls.
 *
 * ROLE IN THE PROJECT:
 *   Keeps AI quota protection at the HTTP boundary instead of mixing it into business services.
 *
 * IF YOU REMOVE THIS FILE:
 *   Session creation and answer grading lose their per-client local rate limits.
 *
 * DEPENDS ON: AiRateLimitInterceptor and Spring MVC.
 * USED BY: Spring MVC at backend startup.
 *
 * KEY FUNCTIONS:
 *   - addInterceptors(registry): registers the interceptor only on AI-triggering paths.
 */
package com.interviewprep.config;

import com.interviewprep.common.AiRateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final AiRateLimitInterceptor aiRateLimitInterceptor;

    public WebConfiguration(AiRateLimitInterceptor aiRateLimitInterceptor) {
        this.aiRateLimitInterceptor = aiRateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(aiRateLimitInterceptor)
                .addPathPatterns("/api/sessions", "/api/questions/*/answer");
    }
}
