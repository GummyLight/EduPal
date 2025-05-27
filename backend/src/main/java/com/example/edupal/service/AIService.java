package com.example.edupal.service;

import com.example.edupal.dto.request.QuestionRequest;
import com.example.edupal.dto.response.AnswerResponse;

public interface AIService {
    AnswerResponse askQuestion(QuestionRequest questionRequest);
}