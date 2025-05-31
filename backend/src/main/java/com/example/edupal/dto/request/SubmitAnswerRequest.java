package com.example.edupal.dto.request;

import lombok.Data;

@Data
public class SubmitAnswerRequest {
    private String quizId;
    private String studentId;
    private String answerFileUrl;
}
