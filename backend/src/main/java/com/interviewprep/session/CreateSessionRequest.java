/**
 * FILE: backend/src/main/java/com/interviewprep/session/CreateSessionRequest.java
 *
 * WHAT IT DOES:
 *   Defines and validates the JSON body accepted when a client starts an interview session.
 *
 * ROLE IN THE PROJECT:
 *   Keeps HTTP input validation separate from the session entity and service layer.
 *
 * IF YOU REMOVE THIS FILE:
 *   SessionController has no typed, validated request body for POST /api/sessions.
 *
 * DEPENDS ON: Bean Validation.
 * USED BY: SessionController and SessionService.
 *
 * KEY FUNCTIONS:
 *   - Record accessors return the validated job title and description.
 */
package com.interviewprep.session;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSessionRequest(
        @NotBlank @Size(max = 120) String jobTitle,
        @NotBlank @Size(max = 10_000) String jobDescription
) {
}
