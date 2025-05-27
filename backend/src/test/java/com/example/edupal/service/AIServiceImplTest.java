package com.example.edupal.service;

import com.example.edupal.dto.request.QuestionRequest;
import com.example.edupal.dto.response.AnswerResponse;
import com.example.edupal.service.AIServiceImpl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class AIServiceImplTest {

    private MockWebServer mockWebServer;
    private AIServiceImpl aiService;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        aiService = new AIServiceImpl();
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    void validQuestionReturnsExpectedAnswer() throws Exception {
        mockWebServer.enqueue(new MockResponse().setBody("{\"header\":{\"code\":0,\"status\":2},\"payload\":{\"choices\":{\"text\":[{\"content\":\"Expected Answer\"}]}}}"));

        QuestionRequest questionRequest = new QuestionRequest();
        questionRequest.setQuestionContent("What is AI?");
        AnswerResponse response = aiService.askQuestion(questionRequest);

        assertEquals("Expected Answer", response.getAnswerContent());
    }

    void invalidApiKeyReturnsError() throws Exception {
        mockWebServer.enqueue(new MockResponse().setBody("{\"header\":{\"code\":401,\"status\":2},\"payload\":{}}"));

        QuestionRequest questionRequest = new QuestionRequest();
        questionRequest.setQuestionContent("What is AI?");
        Exception exception = assertThrows(Exception.class, () -> aiService.askQuestion(questionRequest));

        assertTrue(exception.getMessage().contains("401"));
    }

    void emptyQuestionReturnsError() throws Exception {
        QuestionRequest questionRequest = new QuestionRequest();
        questionRequest.setQuestionContent("");

        Exception exception = assertThrows(Exception.class, () -> aiService.askQuestion(questionRequest));

        assertTrue(exception.getMessage().contains("Invalid question content"));
    }

    void webSocketTimeoutThrowsException() throws Exception {
        mockWebServer.enqueue(new MockResponse().setBody("{\"header\":{\"code\":0,\"status\":1},\"payload\":{}}").setBodyDelay(10, TimeUnit.SECONDS));

        QuestionRequest questionRequest = new QuestionRequest();
        questionRequest.setQuestionContent("What is AI?");
        Exception exception = assertThrows(Exception.class, () -> aiService.askQuestion(questionRequest));

        assertTrue(exception.getMessage().contains("Timeout"));
    }
}