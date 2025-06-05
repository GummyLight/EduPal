package com.example.edupal.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TeacherTest {

    private Teacher teacher;

    @BeforeEach
    void setUp() {
        // 初始化 Teacher 对象
        teacher = new Teacher("T001", "John Smith", "Mathematics", "Class A", "Class B");
    }

    @Test
    void testGetTeacherId() {
        assertEquals("T001", teacher.getTeacherId());
    }

    @Test
    void testGetTeacherName() {
        assertEquals("John Smith", teacher.getTeacherName());
    }

    @Test
    void testGetTeachingSubject() {
        assertEquals("Mathematics", teacher.getTeachingSubject());
    }

    @Test
    void testGetClass1() {
        assertEquals("Class A", teacher.getClass1());
    }

    @Test
    void testGetClass2() {
        assertEquals("Class B", teacher.getClass2());
    }

    @Test
    void testSetTeacherId() {
        teacher.setTeacherId("T002");
        assertEquals("T002", teacher.getTeacherId());
    }

    @Test
    void testSetTeacherName() {
        teacher.setTeacherName("Jane Doe");
        assertEquals("Jane Doe", teacher.getTeacherName());
    }

    @Test
    void testSetTeachingSubject() {
        teacher.setTeachingSubject("Science");
        assertEquals("Science", teacher.getTeachingSubject());
    }

    @Test
    void testSetClass1() {
        teacher.setClass1("Class C");
        assertEquals("Class C", teacher.getClass1());
    }

    @Test
    void testSetClass2() {
        teacher.setClass2("Class D");
        assertEquals("Class D", teacher.getClass2());
    }
}