package com.example.edupal.controller;

import com.example.edupal.common.ApiResponse;
import com.example.edupal.common.Result;
import com.example.edupal.dto.request.QuestionRequest;
import com.example.edupal.dto.response.AnswerResponse;
import com.example.edupal.dto.response.GetMyTeacherResponse;
import com.example.edupal.dto.response.HistoryResponse;
import com.example.edupal.dto.response.ViewQuestionResponse;
import com.example.edupal.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/delete")
    public ResponseEntity<?> deleteHistory(@RequestParam("userId") String userId, @RequestParam("questionId") String questionId) {
        if (userId == null || userId.isEmpty() || questionId == null || questionId.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, "用户名或问题为空"));
        }
        Result result = aIService.deleteHistory(userId, questionId);
        if (result.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(200, result.getMessage()));
        } else {
            return ResponseEntity.status(400).body(new ApiResponse<>(400,result.getMessage()));
        }
    }

    @PostMapping("/transTeacher")
    public ResponseEntity<?> transTeacher(@RequestParam("userId") String userId,@RequestParam("questionId") String questionId, @RequestParam("teacherId") String teacherId) {
        if (userId == null || userId.isEmpty() || questionId == null || questionId.isEmpty() || teacherId == null || teacherId.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, "用户名、问题ID或教师账号不能为空"));
        }
        Result result = aIService.transTeacher(userId, questionId,teacherId);
        if (result.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(200, result.getMessage()));
        } else {
            return ResponseEntity.status(400).body(new ApiResponse<>(400,result.getMessage()));
        }
    }

//    @PostMapping("/askTeacher")
    @GetMapping("/teacherView")
    public ViewQuestionResponse teacherView(@RequestParam("teacherId") String teacherId) {
        try {
            return aIService.viewQuestion(teacherId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e);
        }
    }
//
//
    @PostMapping("/teacherAnswer")
    public ResponseEntity<?> teacherAnswer(@RequestParam("teacherId") String teacherId, @RequestParam("questionId") String questionId, @RequestParam("answerContent") String answerContent) {
        Result result= aIService.teacherAnswer(teacherId, questionId, answerContent);
        if (result.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(200, result.getMessage()));
        } else {
            return ResponseEntity.status(400).body(new ApiResponse<>(400,result.getMessage()));
        }
    }

    @PostMapping("/getMyTeacher")
    public GetMyTeacherResponse getMyTeacher(@RequestParam("userId") String userId) {
        try {
            return aIService.getMyTeacher(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e);
        }
    }
}