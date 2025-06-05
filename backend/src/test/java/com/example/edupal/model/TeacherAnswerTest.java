package com.example.edupal.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

class TeacherAnswerTest {

    private TeacherAnswer teacherAnswer;

    @BeforeEach
    void setUp() {
        // 初始化 TeacherAnswer 对象
        teacherAnswer = new TeacherAnswer();
        teacherAnswer.setId(1);
        teacherAnswer.setTeacherId("T001");
        teacherAnswer.setStudentId("S001");
        teacherAnswer.setQuestionId("Q001");
        teacherAnswer.setAnswerId("A001");
        teacherAnswer.setTransTime(new Date());
        teacherAnswer.setAnswerTime(new Date());
    }

    @Test
    void testGetId() {
        assertEquals(1, teacherAnswer.getId().intValue());
    }

    @Test
    void testGetTeacherId() {
        assertEquals("T001", teacherAnswer.getTeacherId());
    }

    @Test
    void testGetStudentId() {
        assertEquals("S001", teacherAnswer.getStudentId());
    }

    @Test
    void testGetQuestionId() {
        assertEquals("Q001", teacherAnswer.getQuestionId());
    }

    @Test
    void testGetAnswerId() {
        assertEquals("A001", teacherAnswer.getAnswerId());
    }

    @Test
    void testGetTransTime() {
        assertNotNull(teacherAnswer.getTransTime());
    }

    @Test
    void testGetAnswerTime() {
        assertNotNull(teacherAnswer.getAnswerTime());
    }

    @Test
    void testSetId() {
        teacherAnswer.setId(2);
        assertEquals(2, teacherAnswer.getId().intValue());
    }

    @Test
    void testSetTeacherId() {
        teacherAnswer.setTeacherId("T002");
        assertEquals("T002", teacherAnswer.getTeacherId());
    }

    @Test
    void testSetStudentId() {
        teacherAnswer.setStudentId("S002");
        assertEquals("S002", teacherAnswer.getStudentId());
    }

    @Test
    void testSetQuestionId() {
        teacherAnswer.setQuestionId("Q002");
        assertEquals("Q002", teacherAnswer.getQuestionId());
    }

    @Test
    void testSetAnswerId() {
        teacherAnswer.setAnswerId("A002");
        assertEquals("A002", teacherAnswer.getAnswerId());
    }

    @Test
    void testSetTransTime() {
        Date newTransTime = new Date();
        teacherAnswer.setTransTime(newTransTime);
        assertEquals(newTransTime, teacherAnswer.getTransTime());
    }

    @Test
    void testSetAnswerTime() {
        Date newAnswerTime = new Date();
        teacherAnswer.setAnswerTime(newAnswerTime);
        assertEquals(newAnswerTime, teacherAnswer.getAnswerTime());
    }
}