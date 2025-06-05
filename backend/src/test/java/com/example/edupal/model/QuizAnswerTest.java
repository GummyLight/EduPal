package com.example.edupal.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class QuizAnswerTest {

    private QuizAnswer quizAnswer;

    @BeforeEach
    void setUp() {
        // 初始化 QuizAnswer 对象
        quizAnswer = new QuizAnswer();
        quizAnswer.setAnswerId(1);
        quizAnswer.setQuizId(101);
        quizAnswer.setStudentId("student123");
        quizAnswer.setAnswerContent("Test Answer Content");
        quizAnswer.setScore(50);
        quizAnswer.setFeedback("Test Feedback");
        quizAnswer.setIsGraded(1);
        quizAnswer.setSubmitTime(new Date());
        quizAnswer.setGradeTime(new Date());
        quizAnswer.setIsDeleted(0);
    }

    @Test
    void testGetAnswerId() {
        assertEquals(1, quizAnswer.getAnswerId().intValue());
    }

    @Test
    void testGetQuizId() {
        assertEquals(101, quizAnswer.getQuizId().intValue());
    }

    @Test
    void testGetStudentId() {
        assertEquals("student123", quizAnswer.getStudentId());
    }

    @Test
    void testGetAnswerContent() {
        assertEquals("Test Answer Content", quizAnswer.getAnswerContent());
    }

    @Test
    void testGetScore() {
        assertEquals(50, quizAnswer.getScore().intValue());
    }

    @Test
    void testGetFeedback() {
        assertEquals("Test Feedback", quizAnswer.getFeedback());
    }

    @Test
    void testGetIsGraded() {
        assertEquals(1, quizAnswer.getIsGraded().intValue());
    }

    @Test
    void testGetSubmitTime() {
        assertNotNull(quizAnswer.getSubmitTime());
    }

    @Test
    void testGetGradeTime() {
        assertNotNull(quizAnswer.getGradeTime());
    }

    @Test
    void testGetIsDeleted() {
        assertEquals(0, quizAnswer.getIsDeleted().intValue());
    }

    @Test
    void testSetAnswerId() {
        quizAnswer.setAnswerId(2);
        assertEquals(2, quizAnswer.getAnswerId().intValue());
    }

    @Test
    void testSetQuizId() {
        quizAnswer.setQuizId(102);
        assertEquals(102, quizAnswer.getQuizId().intValue());
    }

    @Test
    void testSetStudentId() {
        quizAnswer.setStudentId("student456");
        assertEquals("student456", quizAnswer.getStudentId());
    }

    @Test
    void testSetAnswerContent() {
        quizAnswer.setAnswerContent("New Answer Content");
        assertEquals("New Answer Content", quizAnswer.getAnswerContent());
    }

    @Test
    void testSetScore() {
        quizAnswer.setScore(75);
        assertEquals(75, quizAnswer.getScore().intValue());
    }

    @Test
    void testSetFeedback() {
        quizAnswer.setFeedback("New Feedback");
        assertEquals("New Feedback", quizAnswer.getFeedback());
    }

    @Test
    void testSetIsGraded() {
        quizAnswer.setIsGraded(0);
        assertEquals(0, quizAnswer.getIsGraded().intValue());
    }

    @Test
    void testSetSubmitTime() {
        Date newSubmitTime = new Date();
        quizAnswer.setSubmitTime(newSubmitTime);
        assertEquals(newSubmitTime, quizAnswer.getSubmitTime());
    }

    @Test
    void testSetGradeTime() {
        Date newGradeTime = new Date();
        quizAnswer.setGradeTime(newGradeTime);
        assertEquals(newGradeTime, quizAnswer.getGradeTime());
    }

    @Test
    void testSetIsDeleted() {
        quizAnswer.setIsDeleted(1);
        assertEquals(1, quizAnswer.getIsDeleted().intValue());
    }
}