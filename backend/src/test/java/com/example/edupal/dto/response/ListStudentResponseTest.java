package com.example.edupal.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

class ListStudentResponseTest {

    private ListStudentResponse listStudentResponse;

    @BeforeEach
    void setUp() {
        // 初始化 ListStudentResponse 对象
        List<ListStudentResponse.StudentDetail> students = Arrays.asList(
                new ListStudentResponse.StudentDetail("S001", "John Doe", 1, "Class A"),
                new ListStudentResponse.StudentDetail("S002", "Jane Doe", 0, "Class B")
        );
        listStudentResponse = new ListStudentResponse("success", "Students retrieved successfully", 2, students);
    }

    @Test
    void testGetStatus() {
        assertEquals("success", listStudentResponse.getStatus());
    }

    @Test
    void testGetMessage() {
        assertEquals("Students retrieved successfully", listStudentResponse.getMessage());
    }

    @Test
    void testGetStudentNum() {
        assertEquals(2, listStudentResponse.getStudentNum().intValue());
    }

    @Test
    void testGetStudents() {
        List<ListStudentResponse.StudentDetail> students = listStudentResponse.getStudents();
        assertNotNull(students);
        assertEquals(2, students.size());

        ListStudentResponse.StudentDetail student1 = students.get(0);
        assertEquals("S001", student1.getStudentId());
        assertEquals("John Doe", student1.getStudentName());
        assertEquals(1, student1.getStudentGender().intValue());
        assertEquals("Class A", student1.getStudentClass());

        ListStudentResponse.StudentDetail student2 = students.get(1);
        assertEquals("S002", student2.getStudentId());
        assertEquals("Jane Doe", student2.getStudentName());
        assertEquals(0, student2.getStudentGender().intValue());
        assertEquals("Class B", student2.getStudentClass());
    }
}