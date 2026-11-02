/**
 * FILE: backend/src/main/java/com/interviewprep/answer/AnswerController.java
 *
 * WHAT IT DOES:
 *   Exposes the HTTP endpoint that accepts and grades one answer for a saved question.
 *
 * ROLE IN THE PROJECT:
 *   Is the thin HTTP layer over AnswerService; it validates answer JSON and returns the grading result.
 *
 * IF YOU REMOVE THIS FILE:
 *   Clients cannot submit answers through POST /api/questions/{id}/answer.
 *
 * DEPENDS ON: AnswerService, Bean Validation, Spring MVC, and GradeResponse.
 * USED BY: Browser clients and API integration tests.
 *
 * KEY FUNCTIONS:
 *   - submitAnswer(id, request): handles POST /api/questions/{id}/answer.
 */
package com.interviewprep.answer;

import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questions")
public class AnswerController {

    private final AnswerService answerService;

    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping("/{id}/answer")
    public ResponseEntity<GradeResponse> submitAnswer(@PathVariable UUID id, @Valid @RequestBody SubmitAnswerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(answerService.submitAnswer(id, request));
    }
}
