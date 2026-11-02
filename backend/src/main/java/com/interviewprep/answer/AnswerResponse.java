/**
 * FILE: backend/src/main/java/com/interviewprep/answer/AnswerResponse.java
 *
 * WHAT IT DOES:
 *   Defines the answer data embedded in a loaded interview session.
 *
 * ROLE IN THE PROJECT:
 *   Keeps persisted answer entities out of the API contract.
 *
 * IF YOU REMOVE THIS FILE:
 *   Session responses cannot show saved answer text, scoring, and feedback.
 *
 * DEPENDS ON: AnswerFeedbackResponse, UUID, BigDecimal, and Java time.
 * USED BY: QuestionResponse and SessionResponseMapper.
 *
 * KEY FUNCTIONS:
 *   - Record accessors return the saved answer values.
 */
package com.interviewprep.answer;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AnswerResponse(
        UUID id,
        String userText,
        BigDecimal score,
        AnswerFeedbackResponse feedback,
        Instant createdAt
) {
}
