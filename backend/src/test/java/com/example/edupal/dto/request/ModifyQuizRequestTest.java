package com.example.edupal.dto.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

class ModifyQuizRequestTest {

    private ModifyQuizRequest modifyQuizRequest;

    @BeforeEach
    void setUp() {
        // 初始化 ModifyQuizRequest 对象
        Date now = new Date();
        modifyQuizRequest = new ModifyQuizRequest(
                1,
                "Updated Test Title",
                "Mathematics",
                "Multiple Choice",
                "Easy",
                "Algebra",
                "Updated description of the quiz",
                "T001",
                now,
                now,
                "Class A",
                "Class B"
        );
    }

    @Test
    void testGetQuizId() {
        assertEquals(1, modifyQuizRequest.getQuizId().intValue());
    }

    @Test
    void testGetTitle() {
        assertEquals("Updated Test Title", modifyQuizRequest.getTitle());
    }

    @Test
    void testGetSubject() {
        assertEquals("Mathematics", modifyQuizRequest.getSubject());
    }

    @Test
    void testGetContentType() {
        assertEquals("Multiple Choice", modifyQuizRequest.getContentType());
    }

    @Test
    void testGetDifficulty() {
        assertEquals("Easy", modifyQuizRequest.getDifficulty());
    }

    @Test
    void testGetKnowledgePoints() {
        assertEquals("Algebra", modifyQuizRequest.getKnowledgePoints());
    }

    @Test
    void testGetDescription() {
        assertEquals("Updated description of the quiz", modifyQuizRequest.getDescription());
    }

    @Test
    void testGetTeacherId() {
        assertEquals("T001", modifyQuizRequest.getTeacherId());
    }

    @Test
    void testGetCreateTime() {
        assertNotNull(modifyQuizRequest.getCreateTime());
    }

    @Test
    void testGetDeadline() {
        assertNotNull(modifyQuizRequest.getDeadline());
    }

    @Test
    void testGetClass1() {
        assertEquals("Class A", modifyQuizRequest.getClass1());
    }

    @Test
    void testGetClass2() {
        assertEquals("Class B", modifyQuizRequest.getClass2());
    }

    @Test
    void testConstructor() {
        Date now = new Date();
        ModifyQuizRequest request = new ModifyQuizRequest(
                1,
                "Updated Test Title",
                "Mathematics",
                "Multiple Choice",
                "Easy",
                "Algebra",
                "Updated description of the quiz",
                "T001",
                now,
                now,
                "Class A",
                "Class B"
        );
        assertEquals(1, request.getQuizId().intValue());
        assertEquals("Updated Test Title", request.getTitle());
        assertEquals("Mathematics", request.getSubject());
        assertEquals("Multiple Choice", request.getContentType());
        assertEquals("Easy", request.getDifficulty());
        assertEquals("Algebra", request.getKnowledgePoints());
        assertEquals("Updated description of the quiz", request.getDescription());
        assertEquals("T001", request.getTeacherId());
        assertEquals(now, request.getCreateTime());
        assertEquals(now, request.getDeadline());
        assertEquals("Class A", request.getClass1());
        assertEquals("Class B", request.getClass2());
    }
}