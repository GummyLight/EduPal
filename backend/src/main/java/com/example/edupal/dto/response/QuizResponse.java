package com.example.edupal.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class QuizResponse {
    private String quizId;
    private String quizSubject;
    private String quizContent;
    private Date publishTime;
    private Date deadline;
}
