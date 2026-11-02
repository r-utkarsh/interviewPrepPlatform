/**
 * FILE: backend/src/main/java/com/interviewprep/common/ApiExceptionHandler.java
 *
 * WHAT IT DOES:
 *   Converts validation, domain, AI, and unexpected failures into safe JSON API errors.
 *
 * ROLE IN THE PROJECT:
 *   Ensures clients never receive stack traces, SQL errors, secrets, or raw AI-provider errors.
 *
 * IF YOU REMOVE THIS FILE:
 *   Controllers return inconsistent failures and framework error details may reach clients.
 *
 * DEPENDS ON: Domain exceptions, Spring MVC, and RequestIdFilter.
 * USED BY: Spring MVC for exceptions raised by API requests.
 *
 * KEY FUNCTIONS:
 *   - Each @ExceptionHandler method maps one failure category to a safe HTTP response.
 *   - error(): creates the shared ApiError payload with the current request ID.
 */
package com.interviewprep.common;

import java.time.Instant;

import com.interviewprep.ai.AiNotConfiguredException;
import com.interviewprep.ai.AiOutputValidationException;
import com.interviewprep.ai.AiProviderException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        FieldError fieldError = exception.getBindingResult().getFieldError();
        String message = fieldError == null ? "Request values are invalid." : "One or more request values are invalid.";
        return response(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", message, request);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ApiError> handleUnreadableRequest(Exception exception, HttpServletRequest request) {
        if (hasCause(exception, RequestBodyTooLargeException.class)) {
            return response(HttpStatus.PAYLOAD_TOO_LARGE, "PAYLOAD_TOO_LARGE", "Request body is too large.", request);
        }
        return response(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "Request JSON is invalid.", request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException exception, HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, "NOT_FOUND", "The requested resource was not found.", request);
    }

    @ExceptionHandler({DuplicateAnswerException.class, DataIntegrityViolationException.class})
    ResponseEntity<ApiError> handleConflict(RuntimeException exception, HttpServletRequest request) {
        return response(HttpStatus.CONFLICT, "ANSWER_ALREADY_SUBMITTED", "This question already has an answer.", request);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    ResponseEntity<ApiError> handleRateLimit(RateLimitExceededException exception, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, "60")
                .body(error("RATE_LIMITED", "Too many AI requests. Please try again shortly.", request));
    }

    @ExceptionHandler(AiNotConfiguredException.class)
    ResponseEntity<ApiError> handleAiNotConfigured(AiNotConfiguredException exception, HttpServletRequest request) {
        return response(HttpStatus.SERVICE_UNAVAILABLE, "AI_NOT_CONFIGURED", "The AI provider is not configured.", request);
    }

    @ExceptionHandler(AiOutputValidationException.class)
    ResponseEntity<ApiError> handleInvalidAiOutput(AiOutputValidationException exception, HttpServletRequest request) {
        return response(HttpStatus.SERVICE_UNAVAILABLE, "AI_RESPONSE_INVALID", "The AI provider returned an invalid response. Please try again.", request);
    }

    @ExceptionHandler(AiProviderException.class)
    ResponseEntity<ApiError> handleAiProvider(AiProviderException exception, HttpServletRequest request) {
        return response(HttpStatus.SERVICE_UNAVAILABLE, "AI_UNAVAILABLE", "The AI provider is busy. Please try again.", request);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> handleUnexpected(Exception exception, HttpServletRequest request) {
        LOGGER.error("Unexpected request failure: {}", exception.getClass().getSimpleName());
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "The request could not be completed.", request);
    }

    private ResponseEntity<ApiError> response(HttpStatus status, String code, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(error(code, message, request));
    }

    private ApiError error(String code, String message, HttpServletRequest request) {
        return new ApiError(code, message, RequestIdFilter.getRequestId(request), Instant.now());
    }

    private boolean hasCause(Throwable throwable, Class<? extends Throwable> causeType) {
        Throwable current = throwable;
        while (current != null) {
            if (causeType.isInstance(current)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
