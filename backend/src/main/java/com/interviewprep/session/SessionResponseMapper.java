/**
 * FILE: backend/src/main/java/com/interviewprep/session/SessionResponseMapper.java
 *
 * WHAT IT DOES:
 *   Converts JPA session data and validated AI grades into the backend's response records and feedback JSON.
 *
 * ROLE IN THE PROJECT:
 *   Keeps mapping and JSON serialization out of controllers and business-rule services.
 *
 * IF YOU REMOVE THIS FILE:
 *   Services must duplicate conversion logic and cannot consistently serialize answer feedback.
 *
 * DEPENDS ON: ObjectMapper, session/question/answer entities, API records, and GradedAnswer.
 * USED BY: SessionPersistenceService, SessionService, and AnswerPersistenceService.
 *
 * KEY FUNCTIONS:
 *   - toSessionResponse(session): maps a complete saved session.
 *   - toFeedbackJson(grade): serializes validated feedback for PostgreSQL JSONB storage.
 *   - toGradeResponse(question, answer): maps an answer endpoint response.
 */
package com.interviewprep.session;

import java.util.List;

import com.interviewprep.ai.GradedAnswer;
import com.interviewprep.answer.Answer;
import com.interviewprep.answer.AnswerFeedbackResponse;
import com.interviewprep.answer.AnswerResponse;
import com.interviewprep.answer.GradeResponse;
import com.interviewprep.question.Question;
import com.interviewprep.question.QuestionResponse;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class SessionResponseMapper {

    private final ObjectMapper objectMapper;

    public SessionResponseMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public SessionResponse toSessionResponse(InterviewSession session) {
        List<QuestionResponse> questions = session.getQuestions().stream().map(this::toQuestionResponse).toList();
        return new SessionResponse(
                session.getId(),
                session.getJobTitle(),
                session.getJobDescription(),
                session.getCreatedAt(),
                session.getOverallScore(),
                questions
        );
    }

    public GradeResponse toGradeResponse(Question question, Answer answer) {
        return new GradeResponse(
                question.getId(),
                answer.getId(),
                answer.getScore(),
                toFeedbackResponse(answer),
                answer.getCreatedAt()
        );
    }

    public String toFeedbackJson(GradedAnswer grade) {
        try {
            return objectMapper.writeValueAsString(toFeedbackResponse(grade));
        }
        catch (JacksonException exception) {
            throw new IllegalStateException("Validated feedback could not be serialized.", exception);
        }
    }

    private QuestionResponse toQuestionResponse(Question question) {
        AnswerResponse answer = question.getAnswer() == null ? null : toAnswerResponse(question.getAnswer());
        return new QuestionResponse(
                question.getId(),
                question.getText(),
                question.getTopic(),
                question.getDifficulty(),
                question.getOrderIndex(),
                answer
        );
    }

    private AnswerResponse toAnswerResponse(Answer answer) {
        return new AnswerResponse(
                answer.getId(),
                answer.getUserText(),
                answer.getScore(),
                toFeedbackResponse(answer),
                answer.getCreatedAt()
        );
    }

    private AnswerFeedbackResponse toFeedbackResponse(GradedAnswer grade) {
        return new AnswerFeedbackResponse(
                grade.scores().correctness(),
                grade.scores().depth(),
                grade.scores().clarity(),
                grade.scores().examples(),
                grade.strengths(),
                grade.gaps(),
                grade.modelAnswer()
        );
    }

    private AnswerFeedbackResponse toFeedbackResponse(Answer answer) {
        try {
            return objectMapper.readValue(answer.getFeedbackJson(), AnswerFeedbackResponse.class);
        }
        catch (JacksonException exception) {
            throw new IllegalStateException("Stored answer feedback is invalid.", exception);
        }
    }
}
