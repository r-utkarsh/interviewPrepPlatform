/**
 * FILE: backend/src/main/java/com/interviewprep/session/SessionService.java
 *
 * WHAT IT DOES:
 *   Coordinates question generation, session persistence, session retrieval, and code-computed summaries.
 *
 * ROLE IN THE PROJECT:
 *   Holds the business flow for the session API while controllers only handle HTTP transport.
 *
 * IF YOU REMOVE THIS FILE:
 *   POST and GET session endpoints lose the service that calls AI, loads data, and computes summaries.
 *
 * DEPENDS ON: LlmClient, SessionPersistenceService, SessionRepository, SessionSummaryCalculator, and SessionResponseMapper.
 * USED BY: SessionController.
 *
 * KEY FUNCTIONS:
 *   - createSession(request): generates questions outside a database transaction and then saves the session.
 *   - getSession(id): loads one complete saved session.
 *   - getSummary(id): calculates score and weak topics from saved answers.
 */
package com.interviewprep.session;

import java.util.UUID;

import com.interviewprep.ai.LlmClient;
import com.interviewprep.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionService {

    private final LlmClient llmClient;
    private final SessionPersistenceService persistenceService;
    private final SessionRepository sessionRepository;
    private final SessionSummaryCalculator summaryCalculator;
    private final SessionResponseMapper responseMapper;

    public SessionService(
            LlmClient llmClient,
            SessionPersistenceService persistenceService,
            SessionRepository sessionRepository,
            SessionSummaryCalculator summaryCalculator,
            SessionResponseMapper responseMapper
    ) {
        this.llmClient = llmClient;
        this.persistenceService = persistenceService;
        this.sessionRepository = sessionRepository;
        this.summaryCalculator = summaryCalculator;
        this.responseMapper = responseMapper;
    }

    public SessionResponse createSession(CreateSessionRequest request) {
        return persistenceService.saveGeneratedSession(
                request,
                llmClient.generateQuestions(request.jobTitle().trim(), request.jobDescription().trim())
        );
    }

    @Transactional(readOnly = true)
    public SessionResponse getSession(UUID sessionId) {
        return responseMapper.toSessionResponse(loadDetailedSession(sessionId));
    }

    @Transactional(readOnly = true)
    public SessionSummaryResponse getSummary(UUID sessionId) {
        InterviewSession session = loadDetailedSession(sessionId);
        SessionSummaryData summary = summaryCalculator.calculate(session);
        return new SessionSummaryResponse(
                session.getId(),
                summary.overallScore(),
                summary.weakTopics(),
                summary.gradedAnswerCount(),
                session.getQuestions().size()
        );
    }

    private InterviewSession loadDetailedSession(UUID sessionId) {
        return sessionRepository.findDetailedById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session"));
    }
}
