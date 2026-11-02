/**
 * FILE: backend/src/main/java/com/interviewprep/common/ResourceNotFoundException.java
 *
 * WHAT IT DOES:
 *   Signals that a requested session or question does not exist.
 *
 * ROLE IN THE PROJECT:
 *   Keeps lookup failures separate from internal server errors.
 *
 * IF YOU REMOVE THIS FILE:
 *   Missing resources need ad-hoc controller responses or become generic failures.
 *
 * DEPENDS ON: Java runtime exceptions.
 * USED BY: SessionService, AnswerService, and ApiExceptionHandler.
 *
 * KEY FUNCTIONS:
 *   - Constructor: stores the internal resource description without returning it to clients.
 */
package com.interviewprep.common;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource) {
        super(resource + " was not found.");
    }
}
