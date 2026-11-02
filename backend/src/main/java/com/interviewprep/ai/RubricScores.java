/**
 * FILE: backend/src/main/java/com/interviewprep/ai/RubricScores.java
 *
 * WHAT IT DOES:
 *   Holds the four 1-to-5 criterion scores returned by the AI layer.
 *
 * ROLE IN THE PROJECT:
 *   Makes the grading rubric explicit so the application computes its own final score.
 *
 * IF YOU REMOVE THIS FILE:
 *   Grading feedback loses the structured inputs needed by Rubric.
 *
 * DEPENDS ON: Java records.
 * USED BY: GradedAnswer, Rubric, OpenAiLlmClient, AnswerPersistenceService, and API DTOs.
 *
 * KEY FUNCTIONS:
 *   - Record accessors return correctness, depth, clarity, and examples scores.
 */
package com.interviewprep.ai;

public record RubricScores(int correctness, int depth, int clarity, int examples) {
}
