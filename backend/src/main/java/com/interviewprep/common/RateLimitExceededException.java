/**
 * FILE: backend/src/main/java/com/interviewprep/common/RateLimitExceededException.java
 *
 * WHAT IT DOES:
 *   Signals that the configured local AI-call limit was reached.
 *
 * ROLE IN THE PROJECT:
 *   Gives the global exception handler a safe way to return HTTP 429.
 *
 * IF YOU REMOVE THIS FILE:
 *   The rate limiter cannot communicate a specific quota failure to clients.
 *
 * DEPENDS ON: Java runtime exceptions.
 * USED BY: AiRateLimitInterceptor and ApiExceptionHandler.
 *
 * KEY FUNCTIONS:
 *   - Constructor: creates the quota-exceeded signal.
 */
package com.interviewprep.common;

public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException() {
        super("AI rate limit reached.");
    }
}
