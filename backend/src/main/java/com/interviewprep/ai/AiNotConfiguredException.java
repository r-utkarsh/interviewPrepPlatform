/**
 * FILE: backend/src/main/java/com/interviewprep/ai/AiNotConfiguredException.java
 *
 * WHAT IT DOES:
 *   Signals that the required backend-only AI environment variables have not been configured.
 *
 * ROLE IN THE PROJECT:
 *   Lets the app start safely without a provider key while giving API users a clear configuration error.
 *
 * IF YOU REMOVE THIS FILE:
 *   Missing AI configuration becomes a provider or null-pointer error.
 *
 * DEPENDS ON: Java runtime exceptions.
 * USED BY: OpenAiLlmClient and ApiExceptionHandler.
 *
 * KEY FUNCTIONS:
 *   - Constructor: creates the missing-provider-configuration signal.
 */
package com.interviewprep.ai;

public class AiNotConfiguredException extends RuntimeException {

    public AiNotConfiguredException() {
        super("AI provider configuration is missing.");
    }
}
