package com.example.edupal.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    private Student student;

    @BeforeEach
    void setUp() {
        // 初始化 Student 对象
        student = new Student();
        student.setStudentId("S001");
        student.setStudentName("John Doe");
        student.setStudentGender(1); // 假设 1 代表男性，0 代表女性
        student.setStudentClass("Class A");
    }

    @Test
    void testGetStudentId() {
        assertEquals("S001", student.getStudentId());
    }

    @Test
    void testGetStudentName() {
        assertEquals("John Doe", student.getStudentName());
    }

    @Test
    void testGetStudentGender() {
        assertEquals(1, student.getStudentGender().intValue());
    }

    @Test
    void testGetStudentClass() {
        assertEquals("Class A", student.getStudentClass());
    }

    @Test
    void testSetStudentId() {
        student.setStudentId("S002");
        assertEquals("S002", student.getStudentId());
    }

    @Test
    void testSetStudentName() {
        student.setStudentName("Jane Doe");
        assertEquals("Jane Doe", student.getStudentName());
    }

    @Test
    void testSetStudentGender() {
        student.setStudentGender(0); // 假设 0 代表女性
        assertEquals(0, student.getStudentGender().intValue());
    }

    @Test
    void testSetStudentClass() {
        student.setStudentClass("Class B");
        assertEquals("Class B", student.getStudentClass());
    }
}