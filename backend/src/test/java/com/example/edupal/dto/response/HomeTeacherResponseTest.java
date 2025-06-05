package com.example.edupal.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

class HomeTeacherResponseTest {

    private HomeTeacherResponse homeTeacherResponse;

    @BeforeEach
    void setUp() {
        // 初始化 HomeTeacherResponse 对象
        List<String> classIds = Arrays.asList("Class1", "Class2");
        List<HomeTeacherResponse.StudentDetails> topStudents = Arrays.asList(
                new HomeTeacherResponse.StudentDetails("S001", "Top Student 1", 95.0, 5.0),
                new HomeTeacherResponse.StudentDetails("S002", "Top Student 2", 90.0, 3.0)
        );
        List<HomeTeacherResponse.StudentDetails> bottomStudents = Arrays.asList(
                new HomeTeacherResponse.StudentDetails("S003", "Bottom Student 1", 50.0, 10.0),
                new HomeTeacherResponse.StudentDetails("S004", "Bottom Student 2", 45.0, 15.0)
        );
        homeTeacherResponse = new HomeTeacherResponse(
                "success", "Data retrieved successfully", "username", "teacher", "userId123",
                5, 10, 15, classIds, topStudents, bottomStudents
        );
    }

    @Test
    void testGetStatus() {
        assertEquals("success", homeTeacherResponse.getStatus());
    }

    @Test
    void testGetMessage() {
        assertEquals("Data retrieved successfully", homeTeacherResponse.getMessage());
    }

    @Test
    void testGetUsername() {
        assertEquals("username", homeTeacherResponse.getUsername());
    }

    @Test
    void testGetUserType() {
        assertEquals("teacher", homeTeacherResponse.getUserType());
    }

    @Test
    void testGetUserId() {
        assertEquals("userId123", homeTeacherResponse.getUserId());
    }

    @Test
    void testGetUnfinishedQAs() {
        assertEquals(5, homeTeacherResponse.getUnfinishedQAs());
    }

    @Test
    void testGetUploadExercises() {
        assertEquals(10, homeTeacherResponse.getUploadExercises());
    }

    @Test
    void testGetUploadResources() {
        assertEquals(15, homeTeacherResponse.getUploadResources());
    }

    @Test
    void testGetClassIds() {
        List<String> classIds = homeTeacherResponse.getClassIds();
        assertNotNull(classIds);
        assertEquals(2, classIds.size());
        assertTrue(classIds.contains("Class1"));
        assertTrue(classIds.contains("Class2"));
    }

    @Test
    void testGetTopStudents() {
        List<HomeTeacherResponse.StudentDetails> topStudents = homeTeacherResponse.getTopStudents();
        assertNotNull(topStudents);
        assertEquals(2, topStudents.size());

        HomeTeacherResponse.StudentDetails student1 = topStudents.get(0);
        assertEquals("S001", student1.getStudentId());
        assertEquals("Top Student 1", student1.getStudentName());
        assertEquals(95.0, student1.getStudentAverageScore(), 0.001);
        assertEquals(5.0, student1.getStudentVariation(), 0.001);

        HomeTeacherResponse.StudentDetails student2 = topStudents.get(1);
        assertEquals("S002", student2.getStudentId());
        assertEquals("Top Student 2", student2.getStudentName());
        assertEquals(90.0, student2.getStudentAverageScore(), 0.001);
        assertEquals(3.0, student2.getStudentVariation(), 0.001);
    }

    @Test
    void testGetBottomStudents() {
        List<HomeTeacherResponse.StudentDetails> bottomStudents = homeTeacherResponse.getBottomStudents();
        assertNotNull(bottomStudents);
        assertEquals(2, bottomStudents.size());

        HomeTeacherResponse.StudentDetails student1 = bottomStudents.get(0);
        assertEquals("S003", student1.getStudentId());
        assertEquals("Bottom Student 1", student1.getStudentName());
        assertEquals(50.0, student1.getStudentAverageScore(), 0.001);
        assertEquals(10.0, student1.getStudentVariation(), 0.001);

        HomeTeacherResponse.StudentDetails student2 = bottomStudents.get(1);
        assertEquals("S004", student2.getStudentId());
        assertEquals("Bottom Student 2", student2.getStudentName());
        assertEquals(45.0, student2.getStudentAverageScore(), 0.001);
        assertEquals(15.0, student2.getStudentVariation(), 0.001);
    }
}