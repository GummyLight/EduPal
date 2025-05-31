package com.example.edupal.service;

import com.example.edupal.common.ApiResponse;
import com.example.edupal.common.Result;
import com.example.edupal.dto.request.QuestionRequest;
import com.example.edupal.dto.response.AnswerResponse;
import com.example.edupal.dto.response.HistoryResponse;

public interface AIService {
    AnswerResponse askQuestion(QuestionRequest questionRequest);

    HistoryResponse getHistory(String userId);

    Result deleteHistory(String userId, String questionId);
}