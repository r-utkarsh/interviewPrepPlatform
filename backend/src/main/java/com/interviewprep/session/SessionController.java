/**
 * FILE: backend/src/main/java/com/interviewprep/session/SessionController.java
 *
 * WHAT IT DOES:
 *   Exposes HTTP endpoints that create, load, and summarize interview sessions.
 *
 * ROLE IN THE PROJECT:
 *   Is the thin HTTP layer over SessionService; it validates request JSON and selects HTTP status codes.
 *
 * IF YOU REMOVE THIS FILE:
 *   Clients cannot call the session creation, retrieval, or summary API paths.
 *
 * DEPENDS ON: SessionService, Bean Validation, Spring MVC, and response records.
 * USED BY: Browser clients and API integration tests.
 *
 * KEY FUNCTIONS:
 *   - createSession(request): handles POST /api/sessions.
 *   - getSession(id): handles GET /api/sessions/{id}.
 *   - getSummary(id): handles GET /api/sessions/{id}/summary.
 */
package com.interviewprep.session;

import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<SessionResponse> createSession(@Valid @RequestBody CreateSessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.createSession(request));
    }

    @GetMapping("/{id}")
    public SessionResponse getSession(@PathVariable UUID id) {
        return sessionService.getSession(id);
    }

    @GetMapping("/{id}/summary")
    public SessionSummaryResponse getSummary(@PathVariable UUID id) {
        return sessionService.getSummary(id);
    }
}
