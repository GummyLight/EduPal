package com.example.edupal.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QuestionRequest {
    private String studentId; //账号
    private String questionContent; //提问内容
    private Integer questionType; //问AI 0，问老师 1
    private String questionSubject; //问题所属的科目
    private String questionId; //可选：如果提供，则在现有问题下继续对话；如果为空，则创建新问题

}