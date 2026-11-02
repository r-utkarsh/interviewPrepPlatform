/**
 * FILE: backend/src/main/java/com/interviewprep/ai/AiOutputValidationException.java
 *
 * WHAT IT DOES:
 *   Signals that model output was missing, malformed, or outside the strict response schema.
 *
 * ROLE IN THE PROJECT:
 *   Stops untrusted model text from becoming database rows or UI feedback without validation.
 *
 * IF YOU REMOVE THIS FILE:
 *   Invalid model output is harder to distinguish from provider transport failures.
 *
 * DEPENDS ON: Java runtime exceptions.
 * USED BY: OpenAiLlmClient and ApiExceptionHandler.
 *
 * KEY FUNCTIONS:
 *   - Constructor: creates the invalid-output signal without retaining provider text.
 */
package com.interviewprep.ai;

public class AiOutputValidationException extends RuntimeException {

    public AiOutputValidationException() {
        super("AI output did not match the required schema.");
    }
}
