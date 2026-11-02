/**
 * FILE: backend/src/main/java/com/interviewprep/answer/SubmitAnswerRequest.java
 *
 * WHAT IT DOES:
 *   Defines and validates the JSON body accepted when a user submits an answer for grading.
 *
 * ROLE IN THE PROJECT:
 *   Keeps HTTP input validation separate from answer persistence and provider calls.
 *
 * IF YOU REMOVE THIS FILE:
 *   AnswerController has no typed, size-limited request body for answer grading.
 *
 * DEPENDS ON: Bean Validation.
 * USED BY: AnswerController and AnswerService.
 *
 * KEY FUNCTIONS:
 *   - Record accessor returns the validated answer text.
 */
package com.interviewprep.answer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubmitAnswerRequest(@NotBlank @Size(max = 8_000) String answerText) {
}
