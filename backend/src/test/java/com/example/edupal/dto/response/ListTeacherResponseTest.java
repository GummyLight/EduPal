package com.example.edupal.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

class ListTeacherResponseTest {

    private ListTeacherResponse listTeacherResponse;

    @BeforeEach
    void setUp() {
        // 初始化 ListTeacherResponse 对象
        List<ListTeacherResponse.TeacherDetail> teachers = Arrays.asList(
                new ListTeacherResponse.TeacherDetail("T001", "John Doe", "Mathematics", new String[]{"Class A", "Class B"}),
                new ListTeacherResponse.TeacherDetail("T002", "Jane Doe", "Science", new String[]{"Class C"})
        );
        listTeacherResponse = new ListTeacherResponse("success", "Teachers retrieved successfully", 2, teachers);
    }

    @Test
    void testGetStatus() {
        assertEquals("success", listTeacherResponse.getStatus());
    }

    @Test
    void testGetMessage() {
        assertEquals("Teachers retrieved successfully", listTeacherResponse.getMessage());
    }

    @Test
    void testGetTeacherNum() {
        assertEquals(2, listTeacherResponse.getTeacherNum().intValue());
    }

    @Test
    void testGetTeachers() {
        List<ListTeacherResponse.TeacherDetail> teachers = listTeacherResponse.getTeachers();
        assertNotNull(teachers);
        assertEquals(2, teachers.size());

        ListTeacherResponse.TeacherDetail teacher1 = teachers.get(0);
        assertEquals("T001", teacher1.getTeacherId());
        assertEquals("John Doe", teacher1.getTeacherName());
        assertEquals("Mathematics", teacher1.getTeachingSubject());
        assertArrayEquals(new String[]{"Class A", "Class B"}, teacher1.getTeacherClass());

        ListTeacherResponse.TeacherDetail teacher2 = teachers.get(1);
        assertEquals("T002", teacher2.getTeacherId());
        assertEquals("Jane Doe", teacher2.getTeacherName());
        assertEquals("Science", teacher2.getTeachingSubject());
        assertArrayEquals(new String[]{"Class C"}, teacher2.getTeacherClass());
    }
}