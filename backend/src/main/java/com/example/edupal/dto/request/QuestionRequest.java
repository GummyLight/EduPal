package com.example.edupal.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QuestionRequest {
    private String studentId;
    private String questionContent;
    private Integer questionType;

}