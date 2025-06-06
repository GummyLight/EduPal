package com.example.edupal.dto.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

class CreateQuizRequestTest {

    private CreateQuizRequest createQuizRequest;

    @BeforeEach
    void setUp() {
        // 初始化 CreateQuizRequest 对象
        Date now = new Date();
        createQuizRequest = new CreateQuizRequest(
                "Test Title",
                "Mathematics",
                "Multiple Choice",
                "Easy",
                "Algebra",
                "Description of the quiz",
                "T001",
                now,
                now,
                "Class A",
                "Class B"
        );
    }

    @Test
    void testTitle() {
        assertEquals("Test Title", createQuizRequest.getTitle());
    }

    @Test
    void testSubject() {
        assertEquals("Mathematics", createQuizRequest.getSubject());
    }

    @Test
    void testContentType() {
        assertEquals("Multiple Choice", createQuizRequest.getContentType());
    }

    @Test
    void testDifficulty() {
        assertEquals("Easy", createQuizRequest.getDifficulty());
    }

    @Test
    void testKnowledgePoints() {
        assertEquals("Algebra", createQuizRequest.getKnowledgePoints());
    }

    @Test
    void testDescription() {
        assertEquals("Description of the quiz", createQuizRequest.getDescription());
    }

    @Test
    void testTeacherId() {
        assertEquals("T001", createQuizRequest.getTeacherId());
    }

    @Test
    void testCreateTime() {
        assertNotNull(createQuizRequest.getCreateTime());
    }

    @Test
    void testDeadline() {
        assertNotNull(createQuizRequest.getDeadline());
    }

    @Test
    void testClass1() {
        assertEquals("Class A", createQuizRequest.getClass1());
    }

    @Test
    void testClass2() {
        assertEquals("Class B", createQuizRequest.getClass2());
    }
}