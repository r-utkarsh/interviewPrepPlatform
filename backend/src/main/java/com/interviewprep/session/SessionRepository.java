/**
 * FILE: backend/src/main/java/com/interviewprep/session/SessionRepository.java
 *
 * WHAT IT DOES:
 *   Loads and saves interview sessions, including their ordered questions and answers when requested.
 *
 * ROLE IN THE PROJECT:
 *   Is the persistence boundary for session-level services.
 *
 * IF YOU REMOVE THIS FILE:
 *   Session services cannot store generated sessions or retrieve a complete session view.
 *
 * DEPENDS ON: Spring Data JPA and InterviewSession.
 * USED BY: SessionPersistenceService, SessionService, AnswerPersistenceService, and AnswerService.
 *
 * KEY FUNCTIONS:
 *   - findDetailedById(id): fetches one session with its question and answer graph.
 */
package com.interviewprep.session;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SessionRepository extends JpaRepository<InterviewSession, UUID> {

    @EntityGraph(attributePaths = {"questions", "questions.answer"})
    @Query("select session from InterviewSession session where session.id = :id")
    Optional<InterviewSession> findDetailedById(@Param("id") UUID id);
}
