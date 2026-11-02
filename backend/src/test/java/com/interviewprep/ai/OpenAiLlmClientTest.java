/**
 * FILE: backend/src/test/java/com/interviewprep/ai/OpenAiLlmClientTest.java
 *
 * WHAT IT DOES:
 *   Verifies defensive parsing, code-fence removal, invalid-output retry, and provider retry for the AI adapter.
 *
 * ROLE IN THE PROJECT:
 *   Proves the AI boundary rejects unsafe model data without sending any request to a real provider.
 *
 * IF YOU REMOVE THIS FILE:
 *   Model-output validation and retry behavior can regress without a focused test.
 *
 * DEPENDS ON: OpenAiLlmClient, Spring AI model classes, Mockito, JUnit 5, and Jackson.
 * USED BY: Maven's backend test suite.
 *
 * KEY FUNCTIONS:
 *   - acceptsCodeFencedValidQuestionJson(): checks defensive fence removal.
 *   - retriesMalformedQuestionOutputOnce(): checks the bounded response-format retry.
 *   - retriesOneProviderFailure(): checks the bounded transport retry.
 *   - rejectsOutOfRangeGrade(): checks that invalid rubric scores never reach the service layer.
 */
package com.interviewprep.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.interviewprep.config.ApplicationProperties;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import tools.jackson.databind.ObjectMapper;

class OpenAiLlmClientTest {

    @Test
    void acceptsCodeFencedValidQuestionJson() {
        ChatModel chatModel = mock(ChatModel.class);
        when(chatModel.call(any(Prompt.class))).thenReturn(response("""
                ```json
                [
                  {"topic":"Java","difficulty":"easy","text":"Explain the JVM."},
                  {"topic":"Java","difficulty":"medium","text":"Explain garbage collection."},
                  {"topic":"Spring","difficulty":"easy","text":"What is dependency injection?"},
                  {"topic":"SQL","difficulty":"medium","text":"Explain an index."},
                  {"topic":"Testing","difficulty":"hard","text":"How would you test a service?"}
                ]
                ```
                """));

        List<GeneratedQuestion> questions = client(chatModel, 2, 2).generateQuestions("Java Developer", "Build APIs");

        assertThat(questions).hasSize(5);
        assertThat(questions.getFirst().topic()).isEqualTo("Java");
    }

    @Test
    void retriesMalformedQuestionOutputOnce() {
        ChatModel chatModel = mock(ChatModel.class);
        when(chatModel.call(any(Prompt.class)))
                .thenReturn(response("not json"))
                .thenReturn(response(validQuestionsJson()));

        List<GeneratedQuestion> questions = client(chatModel, 1, 2).generateQuestions("Developer", "Build APIs");

        assertThat(questions).hasSize(5);
        verify(chatModel, times(2)).call(any(Prompt.class));
    }

    @Test
    void retriesOneProviderFailure() {
        ChatModel chatModel = mock(ChatModel.class);
        when(chatModel.call(any(Prompt.class)))
                .thenThrow(new IllegalStateException("provider unavailable"))
                .thenReturn(response(validQuestionsJson()));

        List<GeneratedQuestion> questions = client(chatModel, 2, 1).generateQuestions("Developer", "Build APIs");

        assertThat(questions).hasSize(5);
        verify(chatModel, times(2)).call(any(Prompt.class));
    }

    @Test
    void rejectsOutOfRangeGrade() {
        ChatModel chatModel = mock(ChatModel.class);
        when(chatModel.call(any(Prompt.class))).thenReturn(response("""
                {
                  "correctness": 6,
                  "depth": 2,
                  "clarity": 2,
                  "examples": 2,
                  "strengths": ["Mentions the JVM."],
                  "gaps": ["Needs an example."],
                  "modelAnswer": "The JVM executes Java bytecode."
                }
                """));

        assertThatThrownBy(() -> client(chatModel, 1, 1).gradeAnswer("What is the JVM?", "Java", "It runs Java."))
                .isInstanceOf(AiOutputValidationException.class);
    }

    private OpenAiLlmClient client(ChatModel chatModel, int providerAttempts, int formatAttempts) {
        StaticListableBeanFactory factory = new StaticListableBeanFactory();
        factory.addBean("chatModel", chatModel);
        ApplicationProperties properties = new ApplicationProperties(
                "http://127.0.0.1:3000",
                new ApplicationProperties.RequestSettings(131_072),
                new ApplicationProperties.RateLimitSettings(10, 100),
                new ApplicationProperties.AiSettings(providerAttempts, formatAttempts, 1)
        );
        return new OpenAiLlmClient(
                factory.getBeanProvider(ChatModel.class),
                new ObjectMapper(),
                properties,
                "test-configuration"
        );
    }

    private ChatResponse response(String text) {
        return new ChatResponse(List.of(new Generation(new AssistantMessage(text))));
    }

    private String validQuestionsJson() {
        return """
                [
                  {"topic":"Java","difficulty":"easy","text":"Explain the JVM."},
                  {"topic":"Java","difficulty":"medium","text":"Explain garbage collection."},
                  {"topic":"Spring","difficulty":"easy","text":"What is dependency injection?"},
                  {"topic":"SQL","difficulty":"medium","text":"Explain an index."},
                  {"topic":"Testing","difficulty":"hard","text":"How would you test a service?"}
                ]
                """;
    }
}
