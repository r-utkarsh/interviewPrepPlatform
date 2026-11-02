/**
 * FILE: backend/src/main/java/com/interviewprep/answer/AnswerRepository.java
 *
 * WHAT IT DOES:
 *   Provides JPA access to answer rows and checks whether a question is already answered.
 *
 * ROLE IN THE PROJECT:
 *   Supports the one-answer business rule before persistence while the database remains the final guard.
 *
 * IF YOU REMOVE THIS FILE:
 *   AnswerPersistenceService cannot save grades or check repeat submissions.
 *
 * DEPENDS ON: Spring Data JPA and Answer.
 * USED BY: AnswerPersistenceService.
 *
 * KEY FUNCTIONS:
 *   - existsByQuestion_Id(questionId): checks the one-answer-per-question rule.
 */
package com.interviewprep.answer;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {

    boolean existsByQuestion_Id(UUID questionId);
}
