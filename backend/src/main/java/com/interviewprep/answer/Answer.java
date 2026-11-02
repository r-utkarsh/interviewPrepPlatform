/**
 * FILE: backend/src/main/java/com/interviewprep/answer/Answer.java
 *
 * WHAT IT DOES:
 *   Maps one user answer, its computed score, and validated feedback JSON to the answer table.
 *
 * ROLE IN THE PROJECT:
 *   Persists the result of grading one question while the unique database constraint enforces one answer per question.
 *
 * IF YOU REMOVE THIS FILE:
 *   AnswerRepository and grading services cannot save or load answer feedback.
 *
 * DEPENDS ON: JPA, Question, UUID, and Java time.
 * USED BY: Question, AnswerRepository, AnswerPersistenceService, summary calculation, and response mapping.
 *
 * KEY FUNCTIONS:
 *   - Constructor: creates a persisted answer with application-computed scoring data.
 */
package com.interviewprep.answer;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.interviewprep.question.Question;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "answer")
public class Answer {

    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false, unique = true)
    private Question question;

    @Column(name = "user_text", nullable = false, columnDefinition = "text")
    private String userText;

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal score;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "feedback_json", nullable = false, columnDefinition = "jsonb")
    private String feedbackJson;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Answer() {
    }

    public Answer(UUID id, Question question, String userText, BigDecimal score, String feedbackJson, Instant createdAt) {
        this.id = id;
        this.question = question;
        this.userText = userText;
        this.score = score;
        this.feedbackJson = feedbackJson;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public Question getQuestion() {
        return question;
    }

    public String getUserText() {
        return userText;
    }

    public BigDecimal getScore() {
        return score;
    }

    public String getFeedbackJson() {
        return feedbackJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
