package com.example.edupal.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.UUID;

class AnswerTest {

    private Answer answer;

    @BeforeEach
    void setUp() {
        // 初始化 Answer 对象
        answer = new Answer();
        answer.setAnswerId(UUID.randomUUID().toString());
        answer.setQuestionId("Q001");
        answer.setAnswerContent("This is the answer content.");
        answer.setRelatedQuestion("Related question text.");
        answer.setAnswerType(1);
        answer.setTeacherId("T001");
        answer.setAnswerTime(new Date());
    }

    @Test
    void testGetAnswerId() {
        assertNotNull(answer.getAnswerId());
        assertEquals(36, answer.getAnswerId().length());
    }

    @Test
    void testGetQuestionId() {
        assertEquals("Q001", answer.getQuestionId());
    }

    @Test
    void testGetAnswerContent() {
        assertEquals("This is the answer content.", answer.getAnswerContent());
    }

    @Test
    void testGetRelatedQuestion() {
        assertEquals("Related question text.", answer.getRelatedQuestion());
    }

    @Test
    void testGetAnswerType() {
        assertEquals(1, answer.getAnswerType().intValue());
    }

    @Test
    void testGetTeacherId() {
        assertEquals("T001", answer.getTeacherId());
    }

    @Test
    void testGetAnswerTime() {
        assertNotNull(answer.getAnswerTime());
    }

    @Test
    void testSetAnswerId() {
        String newAnswerId = UUID.randomUUID().toString();
        answer.setAnswerId(newAnswerId);
        assertEquals(newAnswerId, answer.getAnswerId());
    }

    @Test
    void testSetQuestionId() {
        answer.setQuestionId("Q002");
        assertEquals("Q002", answer.getQuestionId());
    }

    @Test
    void testSetAnswerContent() {
        answer.setAnswerContent("Updated answer content.");
        assertEquals("Updated answer content.", answer.getAnswerContent());
    }

    @Test
    void testSetRelatedQuestion() {
        answer.setRelatedQuestion("Updated related question text.");
        assertEquals("Updated related question text.", answer.getRelatedQuestion());
    }

    @Test
    void testSetAnswerType() {
        answer.setAnswerType(2);
        assertEquals(2, answer.getAnswerType().intValue());
    }

    @Test
    void testSetTeacherId() {
        answer.setTeacherId("T002");
        assertEquals("T002", answer.getTeacherId());
    }

    @Test
    void testSetAnswerTime() {
        Date newAnswerTime = new Date();
        answer.setAnswerTime(newAnswerTime);
        assertEquals(newAnswerTime, answer.getAnswerTime());
    }
}