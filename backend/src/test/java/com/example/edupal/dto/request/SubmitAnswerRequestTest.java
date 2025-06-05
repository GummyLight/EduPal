package com.example.edupal.dto.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubmitAnswerRequestTest {

    private SubmitAnswerRequest submitAnswerRequest;

    @BeforeEach
    void setUp() {
        // 初始化 SubmitAnswerRequest 对象
        submitAnswerRequest = new SubmitAnswerRequest();
        submitAnswerRequest.setQuizId("Q001");
        submitAnswerRequest.setStudentId("S001");
        submitAnswerRequest.setAnswerFileUrl("http://example.com/answer.pdf");
    }

    @Test
    void testGetQuizId() {
        assertEquals("Q001", submitAnswerRequest.getQuizId());
    }

    @Test
    void testGetStudentId() {
        assertEquals("S001", submitAnswerRequest.getStudentId());
    }

    @Test
    void testGetAnswerFileUrl() {
        assertEquals("http://example.com/answer.pdf", submitAnswerRequest.getAnswerFileUrl());
    }

    @Test
    void testSetQuizId() {
        submitAnswerRequest.setQuizId("Q002");
        assertEquals("Q002", submitAnswerRequest.getQuizId());
    }

    @Test
    void testSetStudentId() {
        submitAnswerRequest.setStudentId("S002");
        assertEquals("S002", submitAnswerRequest.getStudentId());
    }

    @Test
    void testSetAnswerFileUrl() {
        submitAnswerRequest.setAnswerFileUrl("http://example.com/new_answer.pdf");
        assertEquals("http://example.com/new_answer.pdf", submitAnswerRequest.getAnswerFileUrl());
    }
}