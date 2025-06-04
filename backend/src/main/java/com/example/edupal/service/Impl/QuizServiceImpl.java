package com.example.edupal.service.Impl;

import com.example.edupal.model.Quiz;
import com.example.edupal.repository.QuizRepository;
import com.example.edupal.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizServiceImpl implements QuizService {

    @Autowired
    private QuizRepository quizRepository;


}
