package com.example.edupal.controller;

import com.example.edupal.dto.request.QuestionRequest;
import com.example.edupal.dto.response.AnswerResponse;
import com.example.edupal.dto.response.HistoryResponse;
import com.example.edupal.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private AIService aIService;
    //如果有报错：Could not autowire. No beans of 'AIService' type found，是因为AIService的Impl没有标记@Service to make it a Spring-managed bean

    @PostMapping("/ask")
    public AnswerResponse askQuestion(@RequestBody QuestionRequest questionRequest) {
        try {
            return aIService.askQuestion(questionRequest);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e);
        }
    }

    @PostMapping("/history")
    public HistoryResponse getHistory(@RequestParam("userId") String userId) {
        try {
            return aIService.getHistory(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e);
        }
    }
}