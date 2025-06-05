package com.example.edupal.service;

import com.example.edupal.common.Result;
import com.example.edupal.dto.request.CreateQuizRequest;
import com.example.edupal.dto.request.ModifyQuizRequest;
import com.example.edupal.dto.response.GetMyQuizResponse;
import com.example.edupal.dto.response.GetQuizStudentResponse;
import com.example.edupal.dto.response.GetStudentQuizResponse;
import com.example.edupal.dto.response.GetTeacherQuizResponse;

public interface QuizService {
    GetStudentQuizResponse getStudentQuiz(String userId) throws Exception;
    GetTeacherQuizResponse getTeacherQuiz(String userId) throws Exception;
    Result createQuiz(CreateQuizRequest quizRequest) throws Exception;
    Result modifyQuiz(ModifyQuizRequest quizRequest) throws Exception;
    Result deleteQuiz(Integer quizId) throws Exception;

    GetQuizStudentResponse getQuizStudent(Integer quizId) throws Exception;
    GetMyQuizResponse getMyQuiz(String userId,Integer quizId) throws Exception;
    Result gradeQuiz(Integer answerId,Integer score,String feedback) throws Exception;
    Integer getMaxAnswerId();
    Integer getMaxQuizId();
    Result submitQuiz(Integer quizId, String userId, String answerContent) throws Exception;
    Result getTeacherClass(String userId) throws Exception;

}
