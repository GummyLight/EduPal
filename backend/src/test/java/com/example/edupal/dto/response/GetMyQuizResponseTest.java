package com.example.edupal.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

class GetMyQuizResponseTest {

    private GetMyQuizResponse getMyQuizResponse;

    @BeforeEach
    void setUp() {
        // 初始化 GetMyQuizResponse 对象
        Date now = new Date();
        getMyQuizResponse = new GetMyQuizResponse("success", "Quiz retrieved successfully", 1, "Test Quiz", "Mathematics",
                "Easy", now, now, "John Doe", "T001", 1, 100, "Great job!", 1);
    }

    @Test
    void testGetStatus() {
        assertEquals("success", getMyQuizResponse.getStatus());
    }

    @Test
    void testGetMessage() {
        assertEquals("Quiz retrieved successfully", getMyQuizResponse.getMessage());
    }

    @Test
    void testGetQuizId() {
        assertEquals(1, getMyQuizResponse.getQuizId().intValue());
    }

    @Test
    void testGetTitle() {
        assertEquals("Test Quiz", getMyQuizResponse.getTitle());
    }

    @Test
    void testGetSubject() {
        assertEquals("Mathematics", getMyQuizResponse.getSubject());
    }

    @Test
    void testGetDifficulty() {
        assertEquals("Easy", getMyQuizResponse.getDifficulty());
    }

    @Test
    void testGetCreateTime() {
        assertNotNull(getMyQuizResponse.getCreateTime());
    }

    @Test
    void testGetDeadline() {
        assertNotNull(getMyQuizResponse.getDeadline());
    }

    @Test
    void testGetTeacherName() {
        assertEquals("John Doe", getMyQuizResponse.getTeacherName());
    }

    @Test
    void testGetTeacherId() {
        assertEquals("T001", getMyQuizResponse.getTeacherId());
    }

    @Test
    void testGetQuizStatus() {
        assertEquals(1, getMyQuizResponse.getQuizStatus().intValue());
    }

    @Test
    void testGetScore() {
        assertEquals(100, getMyQuizResponse.getScore().intValue());
    }

    @Test
    void testGetFeedback() {
        assertEquals("Great job!", getMyQuizResponse.getFeedback());
    }

    @Test
    void testGetAnswerId() {
        assertEquals(1, getMyQuizResponse.getAnswerId().intValue());
    }
}