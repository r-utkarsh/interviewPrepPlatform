/**
 * FILE: backend/src/main/java/com/interviewprep/question/QuestionResponse.java
 *
 * WHAT IT DOES:
 *   Defines one ordered question and its optional answer in the session API response.
 *
 * ROLE IN THE PROJECT:
 *   Keeps the API independent from the Question JPA entity while returning all needed interview data.
 *
 * IF YOU REMOVE THIS FILE:
 *   SessionResponse cannot provide typed question data to API clients.
 *
 * DEPENDS ON: AnswerResponse and UUID.
 * USED BY: SessionResponse and SessionResponseMapper.
 *
 * KEY FUNCTIONS:
 *   - Record accessors return question fields and its optional answer.
 */
package com.interviewprep.question;

import java.util.UUID;

import com.interviewprep.answer.AnswerResponse;

public record QuestionResponse(
        UUID id,
        String text,
        String topic,
        String difficulty,
        int orderIndex,
        AnswerResponse answer
) {
}
