package com.example.edupal.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

class GetStudentQuizResponseTest {

    private GetStudentQuizResponse getStudentQuizResponse;

    @BeforeEach
    void setUp() {
        // 初始化 GetStudentQuizResponse 对象
        List<GetStudentQuizResponse.quizDetail> quizDetails = Arrays.asList(
                new GetStudentQuizResponse.quizDetail(1, "Test Quiz 1", "Math", "Multiple Choice", "Easy",
                        "Algebra", "Description of quiz 1", "T001", "John Doe",
                        new Date(), new Date(), new Date(), 1, 95),
                new GetStudentQuizResponse.quizDetail(2, "Test Quiz 2", "Science", "True/False", "Medium",
                        "physics", "Description of quiz 2", "T002", "Jane Doe",
                        new Date(), new Date(), new Date(), 2, 85)
        );
        getStudentQuizResponse = new GetStudentQuizResponse("success", "Quizzes retrieved successfully", "S001", 2, quizDetails);
    }

    @Test
    void testGetStatus() {
        assertEquals("success", getStudentQuizResponse.getStatus());
    }

    @Test
    void testGetMessage() {
        assertEquals("Quizzes retrieved successfully", getStudentQuizResponse.getMessage());
    }

    @Test
    void testGetStudentId() {
        assertEquals("S001", getStudentQuizResponse.getStudentId());
    }

    @Test
    void testGetQuizNum() {
        assertEquals(2, getStudentQuizResponse.getQuizNum().intValue());
    }

    @Test
    void testGetQuizDetails() {
        List<GetStudentQuizResponse.quizDetail> quizDetails = getStudentQuizResponse.getQuizDetails();
        assertNotNull(quizDetails);
        assertEquals(2, quizDetails.size());

        GetStudentQuizResponse.quizDetail quizDetail1 = quizDetails.get(0);
        assertEquals(1, quizDetail1.getQuizId().intValue());
        assertEquals("Test Quiz 1", quizDetail1.getTitle());
        assertEquals("Math", quizDetail1.getSubject());
        // 更多条测试省略...
        assertEquals(95, quizDetail1.getScore().intValue());

        GetStudentQuizResponse.quizDetail quizDetail2 = quizDetails.get(1);
        assertEquals(2, quizDetail2.getQuizId().intValue());
        assertEquals("Test Quiz 2", quizDetail2.getTitle());
        assertEquals("Science", quizDetail2.getSubject());
        // 更多条测试省略...
        assertEquals(85, quizDetail2.getScore().intValue());
    }
}