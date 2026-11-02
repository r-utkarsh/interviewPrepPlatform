/**
 * FILE: backend/src/main/java/com/interviewprep/session/SessionSummaryCalculator.java
 *
 * WHAT IT DOES:
 *   Computes a session average and alphabetically ordered weak topics from saved answer scores.
 *
 * ROLE IN THE PROJECT:
 *   Makes summary rules deterministic and independent of AI-provided overall scores.
 *
 * IF YOU REMOVE THIS FILE:
 *   Session averages and weak-topic results would be duplicated or delegated to the AI provider.
 *
 * DEPENDS ON: InterviewSession, Question, Answer, and BigDecimal math.
 * USED BY: SessionService, AnswerPersistenceService, and SessionSummaryCalculatorTest.
 *
 * KEY FUNCTIONS:
 *   - calculate(session): averages graded answers and finds topics below 3/5.
 */
package com.interviewprep.session;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.interviewprep.answer.Answer;
import com.interviewprep.question.Question;
import org.springframework.stereotype.Component;

@Component
public class SessionSummaryCalculator {

    private static final BigDecimal WEAK_TOPIC_THRESHOLD = BigDecimal.valueOf(3);

    public SessionSummaryData calculate(InterviewSession session) {
        List<Question> questions = session.getQuestions();
        List<Answer> answers = questions.stream()
                .map(Question::getAnswer)
                .filter(answer -> answer != null)
                .toList();

        if (answers.isEmpty()) {
            return new SessionSummaryData(null, List.of(), 0);
        }

        BigDecimal total = answers.stream()
                .map(Answer::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal overallScore = total.divide(BigDecimal.valueOf(answers.size()), 2, RoundingMode.HALF_UP);

        Map<String, List<BigDecimal>> scoresByTopic = new TreeMap<>();
        for (Question question : questions) {
            Answer answer = question.getAnswer();
            if (answer != null) {
                scoresByTopic.computeIfAbsent(question.getTopic(), ignored -> new ArrayList<>()).add(answer.getScore());
            }
        }

        List<String> weakTopics = scoresByTopic.entrySet().stream()
                .filter(entry -> average(entry.getValue()).compareTo(WEAK_TOPIC_THRESHOLD) < 0)
                .map(Map.Entry::getKey)
                .toList();
        return new SessionSummaryData(overallScore, weakTopics, answers.size());
    }

    private BigDecimal average(List<BigDecimal> scores) {
        BigDecimal total = scores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(scores.size()), 2, RoundingMode.HALF_UP);
    }
}
