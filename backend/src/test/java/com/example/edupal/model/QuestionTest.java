package com.example.edupal.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.UUID;

class QuestionTest {

    private Question question;

    @BeforeEach
    void setUp() {
        // 初始化 Question 对象
        question = new Question();
        question.setQuestionContent("What is your name?");
        question.setQuestionSubject("General");
        question.setQuestionType(1);
        question.setIsAnswered(0);
        question.setStudentId("S001");
        question.setQuestionTime(new Date());
    }

    @Test
    void testGenerateUUID() {
        // 测试 generateUUID 是否生成有效的 UUID
        question.generateUUID();
        assertNotNull(question.getQuestionId());
        assertTrue(UUID.fromString(question.getQuestionId()).toString().matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
    }

    @Test
    void testGetQuestionId() {
        question.generateUUID();
        assertNotNull(question.getQuestionId());
    }

    @Test
    void testGetQuestionContent() {
        assertEquals("What is your name?", question.getQuestionContent());
    }

    @Test
    void testGetQuestionSubject() {
        assertEquals("General", question.getQuestionSubject());
    }

    @Test
    void testGetQuestionType() {
        assertEquals(1, question.getQuestionType().intValue());
    }

    @Test
    void testGetIsAnswered() {
        assertEquals(0, question.getIsAnswered().intValue());
    }

    @Test
    void testGetStudentId() {
        assertEquals("S001", question.getStudentId());
    }

    @Test
    void testGetQuestionTime() {
        assertNotNull(question.getQuestionTime());
    }

    @Test
    void testSetQuestionContent() {
        question.setQuestionContent("Where do you live?");
        assertEquals("Where do you live?", question.getQuestionContent());
    }

    @Test
    void testSetQuestionSubject() {
        question.setQuestionSubject("Geography");
        assertEquals("Geography", question.getQuestionSubject());
    }

    @Test
    void testSetQuestionType() {
        question.setQuestionType(2);
        assertEquals(2, question.getQuestionType().intValue());
    }

    @Test
    void testSetIsAnswered() {
        question.setIsAnswered(1);
        assertEquals(1, question.getIsAnswered().intValue());
    }

    @Test
    void testSetStudentId() {
        question.setStudentId("S002");
        assertEquals("S002", question.getStudentId());
    }

    @Test
    void testSetQuestionTime() {
        Date newQuestionTime = new Date();
        question.setQuestionTime(newQuestionTime);
        assertEquals(newQuestionTime, question.getQuestionTime());
    }
}