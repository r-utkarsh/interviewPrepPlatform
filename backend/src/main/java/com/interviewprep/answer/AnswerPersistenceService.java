/**
 * FILE: backend/src/main/java/com/interviewprep/answer/AnswerPersistenceService.java
 *
 * WHAT IT DOES:
 *   Saves one validated grade, enforces the one-answer rule, and updates the session's stored overall score.
 *
 * ROLE IN THE PROJECT:
 *   Provides a short transactional write boundary after AnswerService receives the external AI result.
 *
 * IF YOU REMOVE THIS FILE:
 *   Grading either loses its atomic database update or keeps a transaction open during an AI call.
 *
 * DEPENDS ON: SessionRepository, AnswerRepository, SessionSummaryCalculator, SessionResponseMapper, and GradedAnswer.
 * USED BY: AnswerService.
 *
 * KEY FUNCTIONS:
 *   - saveGrade(questionId, sessionId, answerText, grade, score): stores one answer and recalculates its session score.
 */
package com.interviewprep.answer;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.interviewprep.ai.GradedAnswer;
import com.interviewprep.common.DuplicateAnswerException;
import com.interviewprep.common.ResourceNotFoundException;
import com.interviewprep.question.Question;
import com.interviewprep.session.InterviewSession;
import com.interviewprep.session.SessionRepository;
import com.interviewprep.session.SessionResponseMapper;
import com.interviewprep.session.SessionSummaryCalculator;
import com.interviewprep.session.SessionSummaryData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerPersistenceService {

    private final AnswerRepository answerRepository;
    private final SessionRepository sessionRepository;
    private final SessionSummaryCalculator summaryCalculator;
    private final SessionResponseMapper responseMapper;

    public AnswerPersistenceService(
            AnswerRepository answerRepository,
            SessionRepository sessionRepository,
            SessionSummaryCalculator summaryCalculator,
            SessionResponseMapper responseMapper
    ) {
        this.answerRepository = answerRepository;
        this.sessionRepository = sessionRepository;
        this.summaryCalculator = summaryCalculator;
        this.responseMapper = responseMapper;
    }

    @Transactional
    public GradeResponse saveGrade(
            UUID questionId,
            UUID sessionId,
            String answerText,
            GradedAnswer grade,
            BigDecimal score
    ) {
        if (answerRepository.existsByQuestion_Id(questionId)) {
            throw new DuplicateAnswerException();
        }

        InterviewSession session = sessionRepository.findDetailedById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session"));
        Question question = session.getQuestions().stream()
                .filter(candidate -> candidate.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Question"));
        if (question.getAnswer() != null) {
            throw new DuplicateAnswerException();
        }

        Answer answer = new Answer(
                UUID.randomUUID(),
                question,
                answerText.trim(),
                score,
                responseMapper.toFeedbackJson(grade),
                Instant.now()
        );
        // The question is already managed in this transaction. Keep its inverse association
        // pointed at the managed instance returned by save so cascading cannot schedule a
        // second insert for the same assigned UUID.
        Answer savedAnswer = answerRepository.save(answer);
        question.setAnswer(savedAnswer);

        SessionSummaryData summary = summaryCalculator.calculate(session);
        session.setOverallScore(summary.overallScore());
        return responseMapper.toGradeResponse(question, savedAnswer);
    }
}
