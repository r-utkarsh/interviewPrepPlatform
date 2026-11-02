/**
 * FILE: backend/src/test/java/com/interviewprep/ai/RubricTest.java
 *
 * WHAT IT DOES:
 *   Verifies that the application calculates answer scores from the four rubric criteria.
 *
 * ROLE IN THE PROJECT:
 *   Protects the rule that the AI provider cannot supply the final answer score.
 *
 * IF YOU REMOVE THIS FILE:
 *   Changes to rubric averaging can pass without a focused unit test.
 *
 * DEPENDS ON: Rubric, RubricScores, JUnit 5, and AssertJ.
 * USED BY: Maven's backend test suite.
 *
 * KEY FUNCTIONS:
 *   - calculatesAverageFromAllFourCriteria(): verifies two-decimal averaging.
 */
package com.interviewprep.ai;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class RubricTest {

    private final Rubric rubric = new Rubric();

    @Test
    void calculatesAverageFromAllFourCriteria() {
        BigDecimal score = rubric.calculateScore(new RubricScores(5, 4, 3, 2));

        assertThat(score).isEqualByComparingTo("3.50");
    }
}
