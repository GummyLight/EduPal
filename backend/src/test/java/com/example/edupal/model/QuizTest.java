package com.example.edupal.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class QuizTest {

    private Quiz quiz;

    @BeforeEach
    void setUp() {
        // 初始化 Quiz 对象
        quiz = new Quiz();
        quiz.setQuizId(1);
        quiz.setTitle("Test Title");
        quiz.setSubject("Test Subject");
        quiz.setContentType("Test Content Type");
        quiz.setDifficulty("Test Difficulty");
        quiz.setKnowledgePoints("Test Knowledge Points");
        quiz.setDescription("Test Description");
        quiz.setTeacherId("Test Teacher ID");
        quiz.setClass1("Test Class 1");
        quiz.setClass2("Test Class 2");
        quiz.setCreateTime(new Date());
        quiz.setUpdatedTime(new Date());
        quiz.setDeadline(new Date());
        quiz.setTeacherName("Test Teacher Name");
    }

    @Test
    void testGetQuizId() {
        assertEquals(1, quiz.getQuizId().intValue());
    }

    @Test
    void testGetTitle() {
        assertEquals("Test Title", quiz.getTitle());
    }

    @Test
    void testGetSubject() {
        assertEquals("Test Subject", quiz.getSubject());
    }

    @Test
    void testGetContentType() {
        assertEquals("Test Content Type", quiz.getContentType());
    }

    @Test
    void testGetDifficulty() {
        assertEquals("Test Difficulty", quiz.getDifficulty());
    }

    @Test
    void testGetKnowledgePoints() {
        assertEquals("Test Knowledge Points", quiz.getKnowledgePoints());
    }

    @Test
    void testGetDescription() {
        assertEquals("Test Description", quiz.getDescription());
    }

    @Test
    void testGetTeacherId() {
        assertEquals("Test Teacher ID", quiz.getTeacherId());
    }

    @Test
    void testGetClass1() {
        assertEquals("Test Class 1", quiz.getClass1());
    }

    @Test
    void testGetClass2() {
        assertEquals("Test Class 2", quiz.getClass2());
    }

    @Test
    void testGetCreateTime() {
        assertNotNull(quiz.getCreateTime());
    }

    @Test
    void testGetUpdatedTime() {
        assertNotNull(quiz.getUpdatedTime());
    }

    @Test
    void testGetDeadline() {
        assertNotNull(quiz.getDeadline());
    }

    @Test
    void testGetTeacherName() {
        assertEquals("Test Teacher Name", quiz.getTeacherName());
    }

    @Test
    void testSetQuizId() {
        quiz.setQuizId(2);
        assertEquals(2, quiz.getQuizId().intValue());
    }

    @Test
    void testSetTitle() {
        quiz.setTitle("New Title");
        assertEquals("New Title", quiz.getTitle());
    }

    @Test
    void testSetSubject() {
        quiz.setSubject("New Subject");
        assertEquals("New Subject", quiz.getSubject());
    }

    @Test
    void testSetContentType() {
        quiz.setContentType("New Content Type");
        assertEquals("New Content Type", quiz.getContentType());
    }

    @Test
    void testSetDifficulty() {
        quiz.setDifficulty("New Difficulty");
        assertEquals("New Difficulty", quiz.getDifficulty());
    }

    @Test
    void testSetKnowledgePoints() {
        quiz.setKnowledgePoints("New Knowledge Points");
        assertEquals("New Knowledge Points", quiz.getKnowledgePoints());
    }

    @Test
    void testSetDescription() {
        quiz.setDescription("New Description");
        assertEquals("New Description", quiz.getDescription());
    }

    @Test
    void testSetTeacherId() {
        quiz.setTeacherId("New Teacher ID");
        assertEquals("New Teacher ID", quiz.getTeacherId());
    }

    @Test
    void testSetClass1() {
        quiz.setClass1("New Class 1");
        assertEquals("New Class 1", quiz.getClass1());
    }

    @Test
    void testSetClass2() {
        quiz.setClass2("New Class 2");
        assertEquals("New Class 2", quiz.getClass2());
    }

    @Test
    void testSetCreateTime() {
        Date newCreateTime = new Date();
        quiz.setCreateTime(newCreateTime);
        assertEquals(newCreateTime, quiz.getCreateTime());
    }

    @Test
    void testSetUpdatedTime() {
        Date newUpdatedTime = new Date();
        quiz.setUpdatedTime(newUpdatedTime);
        assertEquals(newUpdatedTime, quiz.getUpdatedTime());
    }

    @Test
    void testSetDeadline() {
        Date newDeadline = new Date();
        quiz.setDeadline(newDeadline);
        assertEquals(newDeadline, quiz.getDeadline());
    }

    @Test
    void testSetTeacherName() {
        quiz.setTeacherName("New Teacher Name");
        assertEquals("New Teacher Name", quiz.getTeacherName());
    }
}