package com.example.edupal.service;

import com.example.edupal.common.ApiResponse;
import com.example.edupal.common.Result;
import com.example.edupal.dto.request.QuestionRequest;
import com.example.edupal.dto.response.AnswerResponse;
import com.example.edupal.dto.response.GetMyTeacherResponse;
import com.example.edupal.dto.response.HistoryResponse;
import com.example.edupal.dto.response.ViewQuestionResponse;
import org.springframework.stereotype.Service;

@Service
public interface AIService {
    AnswerResponse askQuestion(QuestionRequest questionRequest);

    HistoryResponse getHistory(String userId);

    Result deleteHistory(String userId, String questionId);

    Result transTeacher(String userId, String questionId,String teacherId);

    ViewQuestionResponse viewQuestion(String teacherId);

    Result teacherAnswer(String teacherId,String questionId,String answerContent);

    GetMyTeacherResponse getMyTeacher(String userId);
}