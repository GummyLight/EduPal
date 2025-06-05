package com.example.edupal.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

class GetQuizStudentResponseTest {

    private GetQuizStudentResponse getQuizStudentResponse;

    @BeforeEach
    void setUp() {
        // 初始化 GetQuizStudentResponse 对象
        List<GetQuizStudentResponse.answerDetail> answers = Arrays.asList(
                new GetQuizStudentResponse.answerDetail("S001", "John Doe", 1, 100, "2023-01-01 12:00:00", 1, "Correct answer"),
                new GetQuizStudentResponse.answerDetail("S002", "Jane Doe", 0, 50, "2023-01-02 13:00:00", 2, "Incorrect answer")
        );
        getQuizStudentResponse = new GetQuizStudentResponse("success", "Quiz answers retrieved successfully", 1, 2, answers);
    }

    @Test
    void testGetStatus() {
        assertEquals("success", getQuizStudentResponse.getStatus());
    }

    @Test
    void testGetMessage() {
        assertEquals("Quiz answers retrieved successfully", getQuizStudentResponse.getMessage());
    }

    @Test
    void testGetQuizId() {
        assertEquals(1, getQuizStudentResponse.getQuizId().intValue());
    }

    @Test
    void testGetAnswerNum() {
        assertEquals(2, getQuizStudentResponse.getAnswerNum().intValue());
    }

    @Test
    void testGetAnswers() {
        List<GetQuizStudentResponse.answerDetail> answers = getQuizStudentResponse.getAnswers();
        assertNotNull(answers);
        assertEquals(2, answers.size());

        GetQuizStudentResponse.answerDetail answer1 = answers.get(0);
        assertEquals("S001", answer1.getStudentId());
        assertEquals("John Doe", answer1.getUserName());
        assertEquals(1, answer1.getStatus().intValue());
        assertEquals(100, answer1.getScore().intValue());
        assertEquals("2023-01-01 12:00:00", answer1.getAnswerTime());
        assertEquals(1, answer1.getAnswerId().intValue());
        assertEquals("Correct answer", answer1.getAnswerContent());

        GetQuizStudentResponse.answerDetail answer2 = answers.get(1);
        assertEquals("S002", answer2.getStudentId());
        assertEquals("Jane Doe", answer2.getUserName());
        assertEquals(0, answer2.getStatus().intValue());
        assertEquals(50, answer2.getScore().intValue());
        assertEquals("2023-01-02 13:00:00", answer2.getAnswerTime());
        assertEquals(2, answer2.getAnswerId().intValue());
        assertEquals("Incorrect answer", answer2.getAnswerContent());
    }
}