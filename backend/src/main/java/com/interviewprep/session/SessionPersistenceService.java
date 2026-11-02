/**
 * FILE: backend/src/main/java/com/interviewprep/session/SessionPersistenceService.java
 *
 * WHAT IT DOES:
 *   Saves a generated session and its questions in one short database transaction.
 *
 * ROLE IN THE PROJECT:
 *   Separates database writes from the slower external AI call in SessionService.
 *
 * IF YOU REMOVE THIS FILE:
 *   Session creation either loses its transactional write boundary or keeps a transaction open during AI calls.
 *
 * DEPENDS ON: SessionRepository, SessionResponseMapper, CreateSessionRequest, and GeneratedQuestion.
 * USED BY: SessionService.
 *
 * KEY FUNCTIONS:
 *   - saveGeneratedSession(request, questions): persists one session and its ordered generated questions.
 */
package com.interviewprep.session;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.interviewprep.ai.GeneratedQuestion;
import com.interviewprep.question.Question;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionPersistenceService {

    private final SessionRepository sessionRepository;
    private final SessionResponseMapper responseMapper;

    public SessionPersistenceService(SessionRepository sessionRepository, SessionResponseMapper responseMapper) {
        this.sessionRepository = sessionRepository;
        this.responseMapper = responseMapper;
    }

    @Transactional
    public SessionResponse saveGeneratedSession(CreateSessionRequest request, List<GeneratedQuestion> generatedQuestions) {
        InterviewSession session = new InterviewSession(
                UUID.randomUUID(),
                request.jobTitle().trim(),
                request.jobDescription().trim(),
                Instant.now()
        );
        for (int index = 0; index < generatedQuestions.size(); index++) {
            GeneratedQuestion generatedQuestion = generatedQuestions.get(index);
            session.addQuestion(new Question(
                    UUID.randomUUID(),
                    generatedQuestion.text(),
                    generatedQuestion.topic(),
                    generatedQuestion.difficulty(),
                    index
            ));
        }

        return responseMapper.toSessionResponse(sessionRepository.save(session));
    }
}
