package com.example.edupal.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AnswerResponse {
    private String answerId; //回答编号
    private String questionId; //对应的问题编号
    private String answerContent; //回答的内容
}