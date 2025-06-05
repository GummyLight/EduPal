package com.example.edupal.service;

import com.example.edupal.common.Result;
import com.example.edupal.dto.request.CreateQuizRequest;
import com.example.edupal.dto.request.ModifyQuizRequest;
import com.example.edupal.dto.response.GetMyQuizResponse;
import com.example.edupal.dto.response.GetQuizStudentRepsonse;
import com.example.edupal.dto.response.GetStudentQuizResponse;
import com.example.edupal.dto.response.GetTeacherQuizResponse;

public interface QuizService {
    GetStudentQuizResponse getStudentQuiz(String userId) throws Exception;
    GetTeacherQuizResponse getTeacherQuiz(String userId) throws Exception;
    Result createQuiz(CreateQuizRequest quizRequest) throws Exception;
    Result modifyQuiz(ModifyQuizRequest quizRequest) throws Exception;
    Result deleteQuiz(String quizId) throws Exception;
    GetQuizStudentRepsonse getQuizStudent(String quizId) throws Exception;
    GetMyQuizResponse getMyQuiz(String userId,String quizId) throws Exception;

}
