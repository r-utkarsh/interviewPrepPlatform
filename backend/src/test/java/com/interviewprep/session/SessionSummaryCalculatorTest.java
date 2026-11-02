/**
 * FILE: backend/src/test/java/com/interviewprep/session/SessionSummaryCalculatorTest.java
 *
 * WHAT IT DOES:
 *   Verifies session score averaging, empty-summary behavior, and alphabetically ordered weak topics.
 *
 * ROLE IN THE PROJECT:
 *   Protects deterministic summary rules from accidental changes.
 *
 * IF YOU REMOVE THIS FILE:
 *   Summary behavior for partial and weak-topic results can regress without a focused unit test.
 *
 * DEPENDS ON: SessionSummaryCalculator, JPA entities, JUnit 5, and AssertJ.
 * USED BY: Maven's backend test suite.
 *
 * KEY FUNCTIONS:
 *   - returnsEmptySummaryWhenNoAnswersExist(): checks the pre-grading result.
 *   - averagesAnswersAndOrdersWeakTopics(): checks score calculation and topic order.
 */
package com.interviewprep.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.interviewprep.answer.Answer;
import com.interviewprep.question.Question;
import org.junit.jupiter.api.Test;

class SessionSummaryCalculatorTest {

    private final SessionSummaryCalculator calculator = new SessionSummaryCalculator();

    @Test
    void returnsEmptySummaryWhenNoAnswersExist() {
        InterviewSession session = new InterviewSession(UUID.randomUUID(), "Developer", "Build software", Instant.now());
        session.addQuestion(new Question(UUID.randomUUID(), "Question", "Java", "easy", 0));

        SessionSummaryData summary = calculator.calculate(session);

        assertThat(summary.overallScore()).isNull();
        assertThat(summary.weakTopics()).isEmpty();
        assertThat(summary.gradedAnswerCount()).isZero();
    }

    @Test
    void averagesAnswersAndOrdersWeakTopics() {
        InterviewSession session = new InterviewSession(UUID.randomUUID(), "Developer", "Build software", Instant.now());
        Question javaQuestion = new Question(UUID.randomUUID(), "Java question", "Java", "medium", 0);
        Question apiQuestion = new Question(UUID.randomUUID(), "API question", "APIs", "medium", 1);
        Question testingQuestion = new Question(UUID.randomUUID(), "Testing question", "Testing", "easy", 2);
        session.addQuestion(javaQuestion);
        session.addQuestion(apiQuestion);
        session.addQuestion(testingQuestion);

        javaQuestion.setAnswer(answerFor(javaQuestion, "2.00"));
        apiQuestion.setAnswer(answerFor(apiQuestion, "1.00"));
        testingQuestion.setAnswer(answerFor(testingQuestion, "4.00"));

        SessionSummaryData summary = calculator.calculate(session);

        assertThat(summary.overallScore()).isEqualByComparingTo("2.33");
        assertThat(summary.weakTopics()).containsExactly("APIs", "Java");
        assertThat(summary.gradedAnswerCount()).isEqualTo(3);
    }

    private Answer answerFor(Question question, String score) {
        return new Answer(
                UUID.randomUUID(),
                question,
                "Answer text",
                new BigDecimal(score),
                "{}",
                Instant.now()
        );
    }
}
