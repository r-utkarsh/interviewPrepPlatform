/**
 * FILE: backend/src/main/java/com/interviewprep/question/QuestionRepository.java
 *
 * WHAT IT DOES:
 *   Provides JPA access to one generated question by UUID.
 *
 * ROLE IN THE PROJECT:
 *   Lets grading load the question text and topic without treating client input as trusted.
 *
 * IF YOU REMOVE THIS FILE:
 *   AnswerService cannot find a persisted question before calling the AI provider.
 *
 * DEPENDS ON: Spring Data JPA and Question.
 * USED BY: AnswerService and AnswerPersistenceService.
 *
 * KEY FUNCTIONS:
 *   - Inherited findById(id): loads a question by its random UUID.
 */
package com.interviewprep.question;

import java.util.UUID;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends JpaRepository<Question, UUID> {

    @EntityGraph(attributePaths = "session")
    @Query("select question from Question question where question.id = :id")
    Optional<Question> findWithSessionById(@Param("id") UUID id);
}
