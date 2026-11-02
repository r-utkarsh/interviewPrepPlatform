/**
 * FILE: backend/src/main/java/com/interviewprep/config/ApplicationProperties.java
 *
 * WHAT IT DOES:
 *   Binds and validates the non-secret application settings from application.yml.
 *
 * ROLE IN THE PROJECT:
 *   Gives configuration classes, filters, and the rate limiter typed settings instead of scattered strings.
 *
 * IF YOU REMOVE THIS FILE:
 *   CORS, request-size limits, rate limits, and AI retry settings cannot be configured safely.
 *
 * DEPENDS ON: application.yml and Spring Boot configuration-property binding.
 * USED BY: SecurityConfiguration, RequestSizeFilter, InMemoryRateLimiter, and OpenAiLlmClient.
 *
 * KEY FUNCTIONS:
 *   - RequestSettings: exposes the largest accepted API request body.
 *   - RateLimitSettings: exposes the local AI-call quota and client-cache bound.
 *   - AiSettings: exposes bounded provider retry behavior.
 */
package com.interviewprep.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public record ApplicationProperties(
        @NotBlank String frontendOrigin,
        @Valid RequestSettings request,
        @Valid RateLimitSettings rateLimit,
        @Valid AiSettings ai
) {

    public record RequestSettings(@Min(1024) long maxBodyBytes) {
    }

    public record RateLimitSettings(@Min(1) int requestsPerMinute, @Min(1) int maxClients) {
    }

    public record AiSettings(
            @Min(1) int providerAttempts,
            @Min(1) int formatAttempts,
            @Min(1) long retryInitialDelayMilliseconds
    ) {
    }
}
