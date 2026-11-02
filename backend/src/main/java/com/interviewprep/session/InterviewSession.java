/**
 * FILE: backend/src/main/java/com/interviewprep/session/InterviewSession.java
 *
 * WHAT IT DOES:
 *   Maps one saved interview session and its ordered questions to the interview_session table.
 *
 * ROLE IN THE PROJECT:
 *   It is the aggregate root that connects a job description, generated questions, and the saved overall score.
 *
 * IF YOU REMOVE THIS FILE:
 *   SessionRepository, session services, and JPA cannot create or retrieve interview sessions.
 *
 * DEPENDS ON: JPA, Question, UUID, and Java time.
 * USED BY: SessionRepository, SessionPersistenceService, SessionService, AnswerPersistenceService, and mappers.
 *
 * KEY FUNCTIONS:
 *   - addQuestion(question): attaches one ordered question to this session.
 *   - setOverallScore(score): saves the application's computed session average.
 */
package com.interviewprep.session;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.interviewprep.question.Question;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "interview_session")
public class InterviewSession {

    @Id
    private UUID id;

    @Column(name = "job_title", nullable = false, length = 120)
    private String jobTitle;

    @Column(name = "job_description", nullable = false, columnDefinition = "text")
    private String jobDescription;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "overall_score", precision = 3, scale = 2)
    private BigDecimal overallScore;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<Question> questions = new ArrayList<>();

    protected InterviewSession() {
    }

    public InterviewSession(UUID id, String jobTitle, String jobDescription, Instant createdAt) {
        this.id = id;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.createdAt = createdAt;
    }

    public void addQuestion(Question question) {
        question.setSession(this);
        questions.add(question);
    }

    public void setOverallScore(BigDecimal overallScore) {
        this.overallScore = overallScore;
    }

    public UUID getId() {
        return id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public BigDecimal getOverallScore() {
        return overallScore;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
