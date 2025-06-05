package com.example.edupal.dto.response;

import com.example.edupal.model.Answer;
import com.example.edupal.model.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

class HistoryResponseTest {

    private HistoryResponse historyResponse;

    @BeforeEach
    void setUp() {
        // 初始化 HistoryResponse 对象
        Question question = new Question();
        question.setQuestionId("Q001");
        question.setQuestionContent("What is your name?");
        question.setQuestionSubject("General");
        question.setQuestionTime(new Date());

        Answer answer1 = new Answer();
        answer1.setAnswerId("A001");
        answer1.setAnswerContent("John Doe");
        answer1.setAnswerType(1);
        answer1.setTeacherId("T001");
        answer1.setAnswerTime(new Date());

        Answer answer2 = new Answer();
        answer2.setAnswerId("A002");
        answer2.setAnswerContent("Jane Doe");
        answer2.setAnswerType(2);
        answer2.setTeacherId("T002");
        answer2.setAnswerTime(new Date());

        List<Answer> answers = Arrays.asList(answer1, answer2);

        HistoryResponse.QA qa = new HistoryResponse.QA(question, answers);

        List<HistoryResponse.QA> questionSet = Arrays.asList(qa);

        historyResponse = new HistoryResponse("success", "History retrieved successfully", "S001", questionSet);
    }

    @Test
    void testGetStatus() {
        assertEquals("success", historyResponse.getStatus());
    }

    @Test
    void testGetMessage() {
        assertEquals("History retrieved successfully", historyResponse.getMessage());
    }

    @Test
    void testGetStudentId() {
        assertEquals("S001", historyResponse.getStudentId());
    }

    @Test
    void testGetQuestionNum() {
        assertEquals(1, historyResponse.getQuestionNum().intValue());
    }

    @Test
    void testGetQuestionSet() {
        List<HistoryResponse.QA> questionSet = historyResponse.getQuestionSet();
        assertNotNull(questionSet);
        assertEquals(1, questionSet.size());

        HistoryResponse.QA qa = questionSet.get(0);
        assertEquals("Q001", qa.getQuestionId());
        assertEquals("What is your name?", qa.getQuestionContent());
        assertEquals("General", qa.getQuestionSubject());
        assertNotNull(qa.getQuestionTime());
        assertEquals(2, qa.getAnswerNum());

        List<HistoryResponse.QA.AnswerDetail> answers = qa.getAnswers();
        assertNotNull(answers);
        assertEquals(2, answers.size());

        HistoryResponse.QA.AnswerDetail answerDetail1 = answers.get(0);
        assertEquals("A001", answerDetail1.getAnswerId());
        assertEquals("John Doe", answerDetail1.getAnswerContent());
        assertEquals(1, answerDetail1.getAnswerType().intValue());
        assertEquals("T001", answerDetail1.getTeacherId());
        assertNotNull(answerDetail1.getAnswerTime());

        HistoryResponse.QA.AnswerDetail answerDetail2 = answers.get(1);
        assertEquals("A002", answerDetail2.getAnswerId());
        assertEquals("Jane Doe", answerDetail2.getAnswerContent());
        assertEquals(2, answerDetail2.getAnswerType().intValue());
        assertEquals("T002", answerDetail2.getTeacherId());
        assertNotNull(answerDetail2.getAnswerTime());
    }
}