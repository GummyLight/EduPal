package com.example.edupal.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

class HomeStudentResponseTest {

    private HomeStudentResponse homeStudentResponse;

    @BeforeEach
    void setUp() {
        // 初始化 HomeStudentResponse 对象
        List<String> todayTasks = Arrays.asList("Task 1", "Task 2");
        List<String> notifications = Arrays.asList("Notification 1", "Notification 2");
        homeStudentResponse = new HomeStudentResponse(
                "success", "Data retrieved successfully", "username", "student", "userId123",
                5, 10, "A", 3, todayTasks, notifications
        );
    }

    @Test
    void testGetStatus() {
        assertEquals("success", homeStudentResponse.getStatus());
    }

    @Test
    void testGetMessage() {
        assertEquals("Data retrieved successfully", homeStudentResponse.getMessage());
    }

    @Test
    void testGetUsername() {
        assertEquals("username", homeStudentResponse.getUsername());
    }

    @Test
    void testGetUserType() {
        assertEquals("student", homeStudentResponse.getUserType());
    }

    @Test
    void testGetUserId() {
        assertEquals("userId123", homeStudentResponse.getUserId());
    }

    @Test
    void testGetFinishedLectures() {
        assertEquals(5, homeStudentResponse.getFinishedLectures());
    }

    @Test
    void testGetFinishedExercises() {
        assertEquals(10, homeStudentResponse.getFinishedExercises());
    }

    @Test
    void testGetGradeLevel() {
        assertEquals("A", homeStudentResponse.getGradeLevel());
    }

    @Test
    void testGetAnswerNum() {
        assertEquals(3, homeStudentResponse.getAnswerNum());
    }

    @Test
    void testGetTodayTasks() {
        List<String> todayTasks = homeStudentResponse.getTodayTasks();
        assertNotNull(todayTasks);
        assertEquals(2, todayTasks.size());
        assertTrue(todayTasks.contains("Task 1"));
        assertTrue(todayTasks.contains("Task 2"));
    }

    @Test
    void testGetNotifications() {
        List<String> notifications = homeStudentResponse.getNotifications();
        assertNotNull(notifications);
        assertEquals(2, notifications.size());
        assertTrue(notifications.contains("Notification 1"));
        assertTrue(notifications.contains("Notification 2"));
    }
}