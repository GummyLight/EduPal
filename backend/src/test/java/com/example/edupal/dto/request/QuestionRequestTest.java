package com.example.edupal.dto.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuestionRequestTest {

    private QuestionRequest questionRequest;

    @BeforeEach
    void setUp() {
        // 初始化 QuestionRequest 对象
        questionRequest = new QuestionRequest();
        questionRequest.setStudentId("student123");
        questionRequest.setQuestionContent("What is your name?");
        questionRequest.setQuestionType(0);
        questionRequest.setQuestionSubject("General");
        questionRequest.setQuestionId("Q001");
    }

    @Test
    void testGetStudentId() {
        assertEquals("student123", questionRequest.getStudentId());
    }

    @Test
    void testGetQuestionContent() {
        assertEquals("What is your name?", questionRequest.getQuestionContent());
    }

    @Test
    void testGetQuestionType() {
        assertEquals(0, questionRequest.getQuestionType().intValue());
    }

    @Test
    void testGetQuestionSubject() {
        assertEquals("General", questionRequest.getQuestionSubject());
    }

    @Test
    void testGetQuestionId() {
        assertEquals("Q001", questionRequest.getQuestionId());
    }

    @Test
    void testSetStudentId() {
        questionRequest.setStudentId("student456");
        assertEquals("student456", questionRequest.getStudentId());
    }

    @Test
    void testSetQuestionContent() {
        questionRequest.setQuestionContent("Where do you live?");
        assertEquals("Where do you live?", questionRequest.getQuestionContent());
    }

    @Test
    void testSetQuestionType() {
        questionRequest.setQuestionType(1);
        assertEquals(1, questionRequest.getQuestionType().intValue());
    }

    @Test
    void testSetQuestionSubject() {
        questionRequest.setQuestionSubject("Mathematics");
        assertEquals("Mathematics", questionRequest.getQuestionSubject());
    }

    @Test
    void testSetQuestionId() {
        questionRequest.setQuestionId("Q002");
        assertEquals("Q002", questionRequest.getQuestionId());
    }
}