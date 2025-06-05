package com.example.edupal.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HomeAdminResponseTest {

    private HomeAdminResponse homeAdminResponse;

    @BeforeEach
    void setUp() {
        // 初始化 HomeAdminResponse 对象
        homeAdminResponse = new HomeAdminResponse(
                "success", "Data retrieved successfully",
                100, 50, 200,
                10, 20, 30,
                150, 500, 100,
                300
        );
    }

    @Test
    void testGetStatus() {
        assertEquals("success", homeAdminResponse.getStatus());
    }

    @Test
    void testGetMessage() {
        assertEquals("Data retrieved successfully", homeAdminResponse.getMessage());
    }

    @Test
    void testGetTotalUsers() {
        assertEquals(100, homeAdminResponse.getTotalUsers());
    }

    @Test
    void testGetTotalTeachers() {
        assertEquals(50, homeAdminResponse.getTotalTeachers());
    }

    @Test
    void testGetTotalStudents() {
        assertEquals(200, homeAdminResponse.getTotalStudents());
    }

    @Test
    void testGetTodayLoggedInTeachers() {
        assertEquals(10, homeAdminResponse.getTodayLoggedInTeachers());
    }

    @Test
    void testGetTodayLoggedInStudents() {
        assertEquals(20, homeAdminResponse.getTodayLoggedInStudents());
    }

    @Test
    void testGetTodayLoggedInUsers() {
        assertEquals(30, homeAdminResponse.getTodayLoggedInUsers());
    }

    @Test
    void testGetTotalTeachingMaterials() {
        assertEquals(150, homeAdminResponse.getTotalTeachingMaterials());
    }

    @Test
    void testGetTotalExercises() {
        assertEquals(500, homeAdminResponse.getTotalExercises());
    }

    @Test
    void testGetTotalCommunityTopics() {
        assertEquals(100, homeAdminResponse.getTotalCommunityTopics());
    }

    @Test
    void testGetTotalStudentQuestions() {
        assertEquals(300, homeAdminResponse.getTotalStudentQuestions());
    }
}