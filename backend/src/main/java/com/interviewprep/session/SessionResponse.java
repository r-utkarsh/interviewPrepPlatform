/**
 * FILE: backend/src/main/java/com/interviewprep/session/SessionResponse.java
 *
 * WHAT IT DOES:
 *   Defines the full API view of one saved interview session.
 *
 * ROLE IN THE PROJECT:
 *   Is the response contract for session creation and GET /api/sessions/{id}.
 *
 * IF YOU REMOVE THIS FILE:
 *   SessionController has no typed response that includes session metadata and ordered questions.
 *
 * DEPENDS ON: QuestionResponse, UUID, BigDecimal, Java time, and Java collections.
 * USED BY: SessionController, SessionService, SessionPersistenceService, and SessionResponseMapper.
 *
 * KEY FUNCTIONS:
 *   - Record accessors return the session fields and questions.
 */
package com.interviewprep.session;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.interviewprep.question.QuestionResponse;

public record SessionResponse(
        UUID id,
        String jobTitle,
        String jobDescription,
        Instant createdAt,
        BigDecimal overallScore,
        List<QuestionResponse> questions
) {
}
