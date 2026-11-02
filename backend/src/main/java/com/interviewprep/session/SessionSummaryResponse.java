/**
 * FILE: backend/src/main/java/com/interviewprep/session/SessionSummaryResponse.java
 *
 * WHAT IT DOES:
 *   Defines the API view of the computed score and weak topics for one interview session.
 *
 * ROLE IN THE PROJECT:
 *   Is the response contract for GET /api/sessions/{id}/summary.
 *
 * IF YOU REMOVE THIS FILE:
 *   SessionController cannot return a typed summary response.
 *
 * DEPENDS ON: UUID, BigDecimal, and Java collections.
 * USED BY: SessionController and SessionService.
 *
 * KEY FUNCTIONS:
 *   - Record accessors return score, weak topics, and answer-count information.
 */
package com.interviewprep.session;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record SessionSummaryResponse(
        UUID sessionId,
        BigDecimal overallScore,
        List<String> weakTopics,
        int gradedAnswerCount,
        int totalQuestionCount
) {
}
