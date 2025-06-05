package com.example.edupal.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AnswerResponseTest {

    private AnswerResponse answerResponse;

    @BeforeEach
    void setUp() {
        // 初始化 AnswerResponse 对象
        answerResponse = new AnswerResponse();
        answerResponse.setAnswerId("A001");
        answerResponse.setQuestionId("Q001");
        answerResponse.setAnswerContent("This is the answer content.");
    }

    @Test
    void testGetAnswerId() {
        assertEquals("A001", answerResponse.getAnswerId());
    }

    @Test
    void testGetQuestionId() {
        assertEquals("Q001", answerResponse.getQuestionId());
    }

    @Test
    void testGetAnswerContent() {
        assertEquals("This is the answer content.", answerResponse.getAnswerContent());
    }

    @Test
    void testSetAnswerId() {
        answerResponse.setAnswerId("A002");
        assertEquals("A002", answerResponse.getAnswerId());
    }

    @Test
    void testSetQuestionId() {
        answerResponse.setQuestionId("Q002");
        assertEquals("Q002", answerResponse.getQuestionId());
    }

    @Test
    void testSetAnswerContent() {
        answerResponse.setAnswerContent("New answer content.");
        assertEquals("New answer content.", answerResponse.getAnswerContent());
    }
}