package com.example.edupal.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

class ViewQuestionResponseTest {

    private ViewQuestionResponse viewQuestionResponse;

    @BeforeEach
    void setUp() {
        // 初始化 ViewQuestionResponse 对象
        List<ViewQuestionResponse.QA> questionSet = Arrays.asList(
                new ViewQuestionResponse.QA("Q001", "John Doe", "S001", "Class A", "What is your name?", new Date(), Arrays.asList(
                        new ViewQuestionResponse.QA.AnswerDetail(1, "Hello, my name is John.", new Date())
                )
                )
        );
        viewQuestionResponse = new ViewQuestionResponse("success", "Questions retrieved successfully", "T001", 1, questionSet);
    }

    @Test
    void testGetStatus() {
        assertEquals("success", viewQuestionResponse.getStatus());
    }

    @Test
    void testGetMessage() {
        assertEquals("Questions retrieved successfully", viewQuestionResponse.getMessage());
    }

    @Test
    void testGetTeacherId() {
        assertEquals("T001", viewQuestionResponse.getTeacherId());
    }

    @Test
    void testGetQaNum() {
        assertEquals(1, viewQuestionResponse.getQaNum().intValue());
    }

    @Test
    void testGetQuestionSet() {
        List<ViewQuestionResponse.QA> questionSet = viewQuestionResponse.getQuestionSet();
        assertNotNull(questionSet);
        assertEquals(1, questionSet.size());

        ViewQuestionResponse.QA qa = questionSet.get(0);
        assertEquals("Q001", qa.getQuestionId());
        assertEquals("John Doe", qa.getStudentName());
        assertEquals("S001", qa.getStudentId());
        assertEquals("Class A", qa.getStudentClass());
        assertEquals("What is your name?", qa.getQuestionContent());
        assertNotNull(qa.getTransferTime());
        assertEquals(1, qa.getTeacherAnswers().size());

        ViewQuestionResponse.QA.AnswerDetail answerDetail = qa.getTeacherAnswers().get(0);
        assertEquals(1, answerDetail.getAnswerType().intValue());
        assertEquals("Hello, my name is John.", answerDetail.getAnswerContent());
        assertNotNull(answerDetail.getAnswerTime());
    }
}