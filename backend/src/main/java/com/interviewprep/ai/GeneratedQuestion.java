/**
 * FILE: backend/src/main/java/com/interviewprep/ai/GeneratedQuestion.java
 *
 * WHAT IT DOES:
 *   Carries one validated question produced by the AI layer.
 *
 * ROLE IN THE PROJECT:
 *   Separates provider output from the session entity and response DTOs.
 *
 * IF YOU REMOVE THIS FILE:
 *   LlmClient cannot pass structured question data to SessionService.
 *
 * DEPENDS ON: Java records.
 * USED BY: LlmClient, OpenAiLlmClient, SessionService, and SessionPersistenceService.
 *
 * KEY FUNCTIONS:
 *   - Record accessors return the topic, difficulty, and question text.
 */
package com.interviewprep.ai;

public record GeneratedQuestion(String topic, String difficulty, String text) {
}
