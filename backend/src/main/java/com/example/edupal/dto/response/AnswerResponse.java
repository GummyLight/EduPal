package com.example.edupal.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AnswerResponse {
    private String answerId;
    private String questionId;
    private String answerContent;

    // Getters and Setters
}