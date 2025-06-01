package com.example.edupal.controller;

import com.example.edupal.dto.response.QuizResponse;
import com.example.edupal.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/list")
    public List<QuizResponse> getAllQuizzes() {
        return quizService.getAllQuizzes();
    }
}
