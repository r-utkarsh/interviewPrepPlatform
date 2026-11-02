/**
 * FILE: backend/src/test/java/com/interviewprep/SessionApiIntegrationTest.java
 *
 * WHAT IT DOES:
 *   Exercises session creation, answer grading, summary calculation, validation, and duplicate-answer handling through HTTP and PostgreSQL.
 *
 * ROLE IN THE PROJECT:
 *   Verifies that the controller, service, repository, Flyway migration, and database layers work together with a mocked LlmClient.
 *
 * IF YOU REMOVE THIS FILE:
 *   The backend loses its end-to-end API/database proof and regressions can escape unit tests.
 *
 * DEPENDS ON: Testcontainers PostgreSQL, Spring Boot test support, MockMvc, Mockito, and the backend API classes.
 * USED BY: Maven's backend test suite.
 *
 * KEY FUNCTIONS:
 *   - createsGradesAndSummarizesSession(): checks the main backend flow with a mocked LlmClient.
 *   - rejectsInvalidSessionRequest(): checks request validation returns a safe error.
 *   - rejectsOversizedRequestBody(): checks the HTTP body-size guard rejects oversized input before processing it.
 */
package com.interviewprep;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import com.interviewprep.ai.GeneratedQuestion;
import com.interviewprep.ai.GradedAnswer;
import com.interviewprep.ai.LlmClient;
import com.interviewprep.ai.RubricScores;
import com.interviewprep.session.SessionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(SessionApiIntegrationTest.AiTestConfiguration.class)
class SessionApiIntegrationTest {

    @Container
    private static final PostgreSQLContainer DATABASE = new PostgreSQLContainer("postgres:18.6-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LlmClient llmClient;

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", DATABASE::getJdbcUrl);
        registry.add("spring.datasource.username", DATABASE::getUsername);
        registry.add("spring.datasource.password", DATABASE::getPassword);
        registry.add("spring.ai.model.chat", () -> "none");
    }

    @Test
    void createsGradesAndSummarizesSession() throws Exception {
        when(llmClient.generateQuestions(anyString(), anyString())).thenReturn(generatedQuestions());

        MvcResult createResult = mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" + "\"jobTitle\":\"Backend Developer\",\"jobDescription\":\"Build Java APIs\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.questions.length()").value(5))
                .andReturn();
        SessionResponse session = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                SessionResponse.class
        );
        UUID questionId = session.questions().getFirst().id();

        when(llmClient.gradeAnswer(anyString(), anyString(), anyString())).thenReturn(new GradedAnswer(
                new RubricScores(2, 2, 1, 2),
                List.of("Mentions a relevant Java concept."),
                List.of("Needs a more complete explanation."),
                "A strong answer explains the JVM and gives a concrete example."
        ));

        mockMvc.perform(post("/api/questions/{id}/answer", questionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"answerText\":\"The JVM runs Java.\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.score").value(1.75))
                .andExpect(jsonPath("$.feedback.correctness").value(2));

        mockMvc.perform(get("/api/sessions/{id}/summary", session.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overallScore").value(1.75))
                .andExpect(jsonPath("$.weakTopics[0]").value("Java"))
                .andExpect(jsonPath("$.gradedAnswerCount").value(1));

        mockMvc.perform(post("/api/questions/{id}/answer", questionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"answerText\":\"Second answer.\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("ANSWER_ALREADY_SUBMITTED"));
    }

    @Test
    void rejectsInvalidSessionRequest() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"))
                .andReturn();

        assertThat(result.getResponse().getHeader("X-Request-Id")).isNotBlank();
    }

    @Test
    void rejectsOversizedRequestBody() throws Exception {
        String oversizedDescription = "x".repeat(131_072);
        String payload = "{\"jobTitle\":\"Developer\",\"jobDescription\":\"" + oversizedDescription + "\"}";

        mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isPayloadTooLarge())
                .andExpect(jsonPath("$.errorCode").value("PAYLOAD_TOO_LARGE"))
                .andExpect(jsonPath("$.requestId").isNotEmpty());
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class AiTestConfiguration {

        @Bean
        @Primary
        LlmClient testLlmClient() {
            return mock(LlmClient.class);
        }
    }

    private List<GeneratedQuestion> generatedQuestions() {
        return List.of(
                new GeneratedQuestion("Java", "easy", "Explain the JVM."),
                new GeneratedQuestion("Spring", "medium", "Explain dependency injection."),
                new GeneratedQuestion("SQL", "medium", "Explain an index."),
                new GeneratedQuestion("Testing", "easy", "Describe a unit test."),
                new GeneratedQuestion("APIs", "hard", "How do you design a resilient API?")
        );
    }
}
