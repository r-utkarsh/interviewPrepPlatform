/**
 * FILE: backend/src/main/java/com/interviewprep/common/DuplicateAnswerException.java
 *
 * WHAT IT DOES:
 *   Signals an attempt to submit a second answer for the same question.
 *
 * ROLE IN THE PROJECT:
 *   Matches the unique database rule with an explicit HTTP conflict response.
 *
 * IF YOU REMOVE THIS FILE:
 *   Repeat submissions become a generic persistence error.
 *
 * DEPENDS ON: Java runtime exceptions.
 * USED BY: AnswerPersistenceService and ApiExceptionHandler.
 *
 * KEY FUNCTIONS:
 *   - Constructor: creates the duplicate-answer signal.
 */
package com.interviewprep.common;

public class DuplicateAnswerException extends RuntimeException {

    public DuplicateAnswerException() {
        super("An answer already exists for this question.");
    }
}
