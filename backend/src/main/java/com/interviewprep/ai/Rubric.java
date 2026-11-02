/**
 * FILE: backend/src/main/java/com/interviewprep/ai/Rubric.java
 *
 * WHAT IT DOES:
 *   Defines the four grading criteria and calculates the answer score from their validated values.
 *
 * ROLE IN THE PROJECT:
 *   Ensures the application, rather than the AI provider, owns the final interview score calculation.
 *
 * IF YOU REMOVE THIS FILE:
 *   Answer scoring becomes an unverified provider value and unit tests lose the rubric target.
 *
 * DEPENDS ON: RubricScores and Java BigDecimal math.
 * USED BY: AnswerService and RubricTest.
 *
 * KEY FUNCTIONS:
 *   - calculateScore(scores): averages the four rubric criteria to two decimal places.
 */
package com.interviewprep.ai;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

@Component
public class Rubric {

    private static final BigDecimal CRITERION_COUNT = BigDecimal.valueOf(4);

    public BigDecimal calculateScore(RubricScores scores) {
        int total = scores.correctness() + scores.depth() + scores.clarity() + scores.examples();
        return BigDecimal.valueOf(total).divide(CRITERION_COUNT, 2, RoundingMode.HALF_UP);
    }
}
