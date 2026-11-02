/**
 * FILE: backend/src/main/java/com/interviewprep/ai/GradedAnswer.java
 *
 * WHAT IT DOES:
 *   Carries validated rubric evidence and written feedback for one submitted answer.
 *
 * ROLE IN THE PROJECT:
 *   Keeps provider-specific JSON parsing inside the AI layer while services receive typed grading data.
 *
 * IF YOU REMOVE THIS FILE:
 *   AnswerService and Rubric cannot receive structured feedback from LlmClient.
 *
 * DEPENDS ON: RubricScores and Java collections.
 * USED BY: LlmClient, OpenAiLlmClient, AnswerService, and AnswerPersistenceService.
 *
 * KEY FUNCTIONS:
 *   - Record accessors return criterion scores, strengths, gaps, and a model answer.
 */
package com.interviewprep.ai;

import java.util.List;

public record GradedAnswer(RubricScores scores, List<String> strengths, List<String> gaps, String modelAnswer) {
}
