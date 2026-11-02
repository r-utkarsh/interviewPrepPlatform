/**
 * FILE: backend/src/main/java/com/interviewprep/ai/LlmClient.java
 *
 * WHAT IT DOES:
 *   Defines the provider-independent AI operations used to generate questions and grade one answer.
 *
 * ROLE IN THE PROJECT:
 *   Keeps controllers and services independent of OpenAI-compatible client details and makes tests easy to mock.
 *
 * IF YOU REMOVE THIS FILE:
 *   Backend services would depend directly on a provider SDK and cannot use a simple fake in tests.
 *
 * DEPENDS ON: GeneratedQuestion and GradedAnswer.
 * USED BY: SessionService, AnswerService, OpenAiLlmClient, and backend tests.
 *
 * KEY FUNCTIONS:
 *   - generateQuestions(jobTitle, jobDescription): returns validated AI question data.
 *   - gradeAnswer(questionText, topic, answerText): returns validated rubric evidence and feedback.
 */
package com.interviewprep.ai;

import java.util.List;

public interface LlmClient {

    List<GeneratedQuestion> generateQuestions(String jobTitle, String jobDescription);

    GradedAnswer gradeAnswer(String questionText, String topic, String answerText);
}
