/**
 * FILE: backend/src/main/java/com/interviewprep/answer/AnswerService.java
 *
 * WHAT IT DOES:
 *   Loads a question, calls the provider-independent AI client to grade user text, calculates the application score, and saves it.
 *
 * ROLE IN THE PROJECT:
 *   Holds the grading workflow while keeping the external AI call outside the database transaction.
 *
 * IF YOU REMOVE THIS FILE:
 *   POST /api/questions/{id}/answer has no business service to grade and persist a submission.
 *
 * DEPENDS ON: QuestionRepository, AnswerRepository, LlmClient, Rubric, and AnswerPersistenceService.
 * USED BY: AnswerController.
 *
 * KEY FUNCTIONS:
 *   - submitAnswer(questionId, request): grades one first-time answer and saves the computed result.
 */
package com.interviewprep.answer;

import java.math.BigDecimal;
import java.util.UUID;

import com.interviewprep.ai.GradedAnswer;
import com.interviewprep.ai.LlmClient;
import com.interviewprep.ai.Rubric;
import com.interviewprep.common.DuplicateAnswerException;
import com.interviewprep.common.ResourceNotFoundException;
import com.interviewprep.question.Question;
import com.interviewprep.question.QuestionRepository;
import org.springframework.stereotype.Service;

@Service
public class AnswerService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final LlmClient llmClient;
    private final Rubric rubric;
    private final AnswerPersistenceService persistenceService;

    public AnswerService(
            QuestionRepository questionRepository,
            AnswerRepository answerRepository,
            LlmClient llmClient,
            Rubric rubric,
            AnswerPersistenceService persistenceService
    ) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.llmClient = llmClient;
        this.rubric = rubric;
        this.persistenceService = persistenceService;
    }

    public GradeResponse submitAnswer(UUID questionId, SubmitAnswerRequest request) {
        Question question = questionRepository.findWithSessionById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question"));
        if (answerRepository.existsByQuestion_Id(questionId)) {
            throw new DuplicateAnswerException();
        }

        String answerText = request.answerText().trim();
        GradedAnswer grade = llmClient.gradeAnswer(question.getText(), question.getTopic(), answerText);
        BigDecimal score = rubric.calculateScore(grade.scores());
        return persistenceService.saveGrade(questionId, question.getSession().getId(), answerText, grade, score);
    }
}
