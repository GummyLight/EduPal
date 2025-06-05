package com.example.edupal.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

class GetTeacherQuizResponseTest {

    private GetTeacherQuizResponse getTeacherQuizResponse;

    @BeforeEach
    void setUp() {
        // 初始化 GetTeacherQuizResponse 对象
        List<GetTeacherQuizResponse.quizDetail> quizzes = Arrays.asList(
                new GetTeacherQuizResponse.quizDetail(1, "Test Quiz 1", "Mathematics", "Multiple Choice", "Easy",
                        "Algebra", "Description of quiz 1", "T001", "John Doe",
                        new Date(), new Date(), new Date(), 10, 2, 5),
                new GetTeacherQuizResponse.quizDetail(2, "Test Quiz 2", "Science", "True/False", "Medium",
                        "physics", "Description of quiz 2", "T002", "Jane Doe",
                        new Date(), new Date(), new Date(), 15, 3, 12)
        );
        getTeacherQuizResponse = new GetTeacherQuizResponse("success", "Quizzes retrieved successfully", "T001", 2, quizzes);
    }

    @Test
    void testGetStatus() {
        assertEquals("success", getTeacherQuizResponse.getStatus());
    }

    @Test
    void testGetMessage() {
        assertEquals("Quizzes retrieved successfully", getTeacherQuizResponse.getMessage());
    }

    @Test
    void testGetTeacherId() {
        assertEquals("T001", getTeacherQuizResponse.getTeacherId());
    }

    @Test
    void testGetQuizNum() {
        assertEquals(2, getTeacherQuizResponse.getQuizNum().intValue());
    }

    @Test
    void testGetQuizzes() {
        List<GetTeacherQuizResponse.quizDetail> quizzes = getTeacherQuizResponse.getQuizzes();
        assertNotNull(quizzes);
        assertEquals(2, quizzes.size());

        GetTeacherQuizResponse.quizDetail quizDetail1 = quizzes.get(0);
        assertEquals(1, quizDetail1.getQuizId().intValue());
        assertEquals("Test Quiz 1", quizDetail1.getTitle());
        assertEquals("Mathematics", quizDetail1.getSubject());
        // 更多条测试省略...
        assertEquals(10, quizDetail1.getSubmitNum().intValue());
        assertEquals(2, quizDetail1.getUnSubmitNum().intValue());
        assertEquals(5, quizDetail1.getGradedNum().intValue());

        GetTeacherQuizResponse.quizDetail quizDetail2 = quizzes.get(1);
        assertEquals(2, quizDetail2.getQuizId().intValue());
        assertEquals("Test Quiz 2", quizDetail2.getTitle());
        assertEquals("Science", quizDetail2.getSubject());
        // 更多条测试省略...
        assertEquals(15, quizDetail2.getSubmitNum().intValue());
        assertEquals(3, quizDetail2.getUnSubmitNum().intValue());
        assertEquals(12, quizDetail2.getGradedNum().intValue());
    }
}