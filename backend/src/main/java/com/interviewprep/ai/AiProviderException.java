/**
 * FILE: backend/src/main/java/com/interviewprep/ai/AiProviderException.java
 *
 * WHAT IT DOES:
 *   Signals a timeout, rate limit, or other failed provider call after bounded retries.
 *
 * ROLE IN THE PROJECT:
 *   Prevents provider-specific failure details from leaving the AI layer.
 *
 * IF YOU REMOVE THIS FILE:
 *   Provider implementation errors leak into services and API responses.
 *
 * DEPENDS ON: Java runtime exceptions.
 * USED BY: OpenAiLlmClient and ApiExceptionHandler.
 *
 * KEY FUNCTIONS:
 *   - Constructor: wraps a safe internal provider-failure cause.
 */
package com.interviewprep.ai;

public class AiProviderException extends RuntimeException {

    public AiProviderException(Throwable cause) {
        super("AI provider call failed.", cause);
    }
}
