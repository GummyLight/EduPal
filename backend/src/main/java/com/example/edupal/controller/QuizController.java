package com.example.edupal.controller;

import com.example.edupal.common.ApiResponse;
import com.example.edupal.common.Result;
import com.example.edupal.dto.request.CreateQuizRequest;
import com.example.edupal.dto.request.ModifyQuizRequest;
import com.example.edupal.dto.response.GetMyQuizResponse;
import com.example.edupal.dto.response.GetStudentQuizResponse;
import com.example.edupal.dto.response.GetTeacherQuizResponse;
import com.example.edupal.dto.response.GetQuizStudentRepsonse;
import com.example.edupal.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    /*
    * 给学生用的：
    * getStudentQuiz：获取学生的测验列表
    * submitQuiz：提交测验
    *
    * */

    @GetMapping("/getStudentQuiz")
    public GetStudentQuizResponse getStudentQuiz(@RequestParam("userId") String userId) {
        // 调用QuizService获取用户的测验列表
        try{
            return quizService.getStudentQuiz(userId);
        }catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e);
        }
    }

    @GetMapping("/max-answer-id")
    public ResponseEntity<?> getMaxResourceId() {
        Integer maxId = quizService.getMaxAnswerId();
        if (maxId == null) {
            maxId = 0;  // 表为空时默认0
        }
        return ResponseEntity.ok(new ApiResponse<>(200, "最大 resource_id 获取成功", maxId));
    }

    @GetMapping("/submitQuiz")
    public ResponseEntity<?> submitQuiz(@RequestParam("quizId") String quizId,
                                                @RequestParam("userId") String userId,
                                                @RequestParam("answerContent") String answerContent) {
        // 调用QuizService提交测验
        try {
            Result result = quizService.submitQuiz(quizId, userId, answerContent);
            if (result.isSuccess()) {
                return ResponseEntity.ok(new ApiResponse<>(200, result.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, result.getMessage()));
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e);
        }
    }

    @GetMapping("/getMyQuiz")
    public GetMyQuizResponse getMyQuiz(@RequestParam("userId") String userId,
                                       @RequestParam("quizId") String quizId) {
        // 调用QuizService获取用户的测验列表
        try {
            return quizService.getMyQuiz(userId,quizId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e);
        }
    }

    /*
    * 给教师用的：
    * getTeacherQuiz：获取教师布置的的测验列表
    * createQuiz：创建测验
    * ModifyQuiz：修改测验
    * deleteQuiz：删除测验
    * getQuizStudent：获取测验的学生答题情况
    * gradeQuiz：批改测验
    * */

    @GetMapping("/getTeacherQuiz")
    public GetTeacherQuizResponse getTeacherQuiz(@RequestParam("userId") String userId) {
        // 调用QuizService获取教师的测验列表
        try {
            return quizService.getTeacherQuiz(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e);
        }
    }

    @GetMapping("/max-quiz-id")
    public ResponseEntity<?> getMaxQuizId() {
        Integer maxId = quizService.getMaxQuizId();
        if (maxId == null) {
            maxId = 0;  // 表为空时默认0
        }
        return ResponseEntity.ok(new ApiResponse<>(200, "最大 quiz_id 获取成功", maxId));
    }

    @PostMapping("/createQuiz")
    public ResponseEntity<?> createQuiz(@RequestBody CreateQuizRequest quizRequest) {
        // 调用QuizService创建测验
        try {
            Result result = quizService.createQuiz(quizRequest);
            if (result.isSuccess()) {
                return ResponseEntity.ok(new ApiResponse<>(200, result.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, result.getMessage()));
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e);
        }
    }


    @PostMapping("/modifyQuiz")
    public ResponseEntity<?> modifyQuiz(@RequestBody ModifyQuizRequest quizRequest) {
        // 调用QuizService修改测验
        try {
            Result result = quizService.modifyQuiz(quizRequest);
            if (result.isSuccess()) {
                return ResponseEntity.ok(new ApiResponse<>(200, result.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, result.getMessage()));
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e);
        }
    }

    @PostMapping("/deleteQuiz")
    public ResponseEntity<?> deleteQuiz(@RequestParam("quizId") String quizId) {
        // 调用QuizService删除测验
        try {
            Result result = quizService.deleteQuiz(quizId);
            if (result.isSuccess()) {
                return ResponseEntity.ok(new ApiResponse<>(200, result.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, result.getMessage()));
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e);
        }
    }

    @GetMapping("/getQuizStudent")
    public GetQuizStudentRepsonse getQuizStudent(@RequestParam("quizId") String quizId) {
        // 调用QuizService获取学生的测验列表
        try {
            return quizService.getQuizStudent(quizId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e);
        }
    }

    @PostMapping("/gradeQuiz")
    public ResponseEntity<?> gradeQuiz(@RequestParam("answerId") String quizId,
                                       @RequestParam("score") Integer score,
                                       @RequestParam("feedback") String feedback) {
        // 调用QuizService批改测验
        try {
            Result result = quizService.gradeQuiz(quizId, score, feedback);
            if (result.isSuccess()) {
                return ResponseEntity.ok(new ApiResponse<>(200, result.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, result.getMessage()));
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e);
        }
    }


}
