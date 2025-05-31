package com.example.edupal.service;

import com.example.edupal.dto.response.QuizResponse;

import java.util.List;

public interface QuizService {
    List<QuizResponse> getAllQuizzes();
}
