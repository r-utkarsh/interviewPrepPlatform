/**
 * FILE: backend/src/main/java/com/interviewprep/question/Question.java
 *
 * WHAT IT DOES:
 *   Maps one generated interview question to the question table and links it to one session and optional answer.
 *
 * ROLE IN THE PROJECT:
 *   Stores the ordered questions that a user answers during one interview session.
 *
 * IF YOU REMOVE THIS FILE:
 *   Generated questions cannot be persisted, loaded, or connected to answers.
 *
 * DEPENDS ON: JPA, InterviewSession, Answer, UUID, and Java time.
 * USED BY: InterviewSession, QuestionRepository, session services, answer services, and mappers.
 *
 * KEY FUNCTIONS:
 *   - setSession(session): attaches this question to its parent session.
 *   - setAnswer(answer): attaches the one allowed answer to this question.
 */
package com.interviewprep.question;

import java.util.UUID;

import com.interviewprep.answer.Answer;
import com.interviewprep.session.InterviewSession;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "question")
public class Question {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private InterviewSession session;

    @Column(nullable = false, columnDefinition = "text")
    private String text;

    @Column(nullable = false, length = 80)
    private String topic;

    @Column(nullable = false, length = 16)
    private String difficulty;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Answer answer;

    protected Question() {
    }

    public Question(UUID id, String text, String topic, String difficulty, int orderIndex) {
        this.id = id;
        this.text = text;
        this.topic = topic;
        this.difficulty = difficulty;
        this.orderIndex = orderIndex;
    }

    public void setSession(InterviewSession session) {
        this.session = session;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public UUID getId() {
        return id;
    }

    public InterviewSession getSession() {
        return session;
    }

    public String getText() {
        return text;
    }

    public String getTopic() {
        return topic;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public Answer getAnswer() {
        return answer;
    }
}
