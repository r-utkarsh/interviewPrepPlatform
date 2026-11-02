/**
 * FILE: backend/src/main/java/com/interviewprep/common/RequestBodyTooLargeException.java
 *
 * WHAT IT DOES:
 *   Signals that a streamed HTTP request exceeded the configured body-size limit.
 *
 * ROLE IN THE PROJECT:
 *   Lets the request filter and global exception handler distinguish oversized bodies from malformed JSON.
 *
 * IF YOU REMOVE THIS FILE:
 *   Oversized streamed bodies may be reported as generic malformed requests.
 *
 * DEPENDS ON: Java I/O.
 * USED BY: RequestSizeFilter and ApiExceptionHandler.
 *
 * KEY FUNCTIONS:
 *   - Constructor: creates a safe internal signal without retaining the request body.
 */
package com.interviewprep.common;

import java.io.IOException;

public class RequestBodyTooLargeException extends IOException {

    public RequestBodyTooLargeException() {
        super("Request body exceeded the configured limit.");
    }
}
