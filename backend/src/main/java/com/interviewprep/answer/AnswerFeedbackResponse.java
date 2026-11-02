/**
 * FILE: backend/src/main/java/com/interviewprep/answer/AnswerFeedbackResponse.java
 *
 * WHAT IT DOES:
 *   Defines the validated feedback returned after the application scores an answer.
 *
 * ROLE IN THE PROJECT:
 *   Is both the stable API feedback shape and the object serialized into answer.feedback_json.
 *
 * IF YOU REMOVE THIS FILE:
 *   Answer feedback cannot be stored and returned as one structured contract.
 *
 * DEPENDS ON: Java collections.
 * USED BY: SessionResponseMapper, AnswerPersistenceService, AnswerResponse, GradeResponse, and frontend API clients.
 *
 * KEY FUNCTIONS:
 *   - Record accessors return criterion scores, strengths, gaps, and model answer text.
 */
package com.interviewprep.answer;

import java.util.List;

public record AnswerFeedbackResponse(
        int correctness,
        int depth,
        int clarity,
        int examples,
        List<String> strengths,
        List<String> gaps,
        String modelAnswer
) {
}
