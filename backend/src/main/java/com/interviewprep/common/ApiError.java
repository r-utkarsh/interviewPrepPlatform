/**
 * FILE: backend/src/main/java/com/interviewprep/common/ApiError.java
 *
 * WHAT IT DOES:
 *   Defines the safe JSON shape returned when an API request cannot be completed.
 *
 * ROLE IN THE PROJECT:
 *   Prevents controllers and exception handlers from exposing stack traces, SQL messages, or provider responses.
 *
 * IF YOU REMOVE THIS FILE:
 *   Error responses lose their shared, documented contract.
 *
 * DEPENDS ON: Java time.
 * USED BY: ApiExceptionHandler and RequestSizeFilter.
 *
 * KEY FUNCTIONS:
 *   - Record accessors expose the safe code, message, request ID, and timestamp.
 */
package com.interviewprep.common;

import java.time.Instant;

public record ApiError(String errorCode, String message, String requestId, Instant timestamp) {
}
