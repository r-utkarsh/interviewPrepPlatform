/**
 * FILE: backend/src/main/java/com/interviewprep/session/SessionSummaryData.java
 *
 * WHAT IT DOES:
 *   Carries the internal result calculated from saved answer scores.
 *
 * ROLE IN THE PROJECT:
 *   Lets services reuse one calculation for persistence and summary responses.
 *
 * IF YOU REMOVE THIS FILE:
 *   SessionSummaryCalculator must return loosely related values or duplicate its result shape.
 *
 * DEPENDS ON: BigDecimal and Java collections.
 * USED BY: SessionSummaryCalculator, SessionService, and AnswerPersistenceService.
 *
 * KEY FUNCTIONS:
 *   - Record accessors return the calculated average, weak topics, and answer count.
 */
package com.interviewprep.session;

import java.math.BigDecimal;
import java.util.List;

public record SessionSummaryData(BigDecimal overallScore, List<String> weakTopics, int gradedAnswerCount) {
}
