/**
 * FILE: backend/src/main/java/com/interviewprep/answer/GradeResponse.java
 *
 * WHAT IT DOES:
 *   Defines the focused response returned immediately after a question answer is graded.
 *
 * ROLE IN THE PROJECT:
 *   Gives the answer endpoint a contract that includes the question identity, computed score, and feedback.
 *
 * IF YOU REMOVE THIS FILE:
 *   POST /api/questions/{id}/answer has no typed response contract.
 *
 * DEPENDS ON: AnswerFeedbackResponse, UUID, BigDecimal, and Java time.
 * USED BY: AnswerController, AnswerService, and AnswerPersistenceService.
 *
 * KEY FUNCTIONS:
 *   - Record accessors return the saved grading result.
 */
package com.interviewprep.answer;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record GradeResponse(
        UUID questionId,
        UUID answerId,
        BigDecimal score,
        AnswerFeedbackResponse feedback,
        Instant createdAt
) {
}
