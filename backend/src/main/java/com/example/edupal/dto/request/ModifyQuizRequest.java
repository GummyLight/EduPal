package com.example.edupal.dto.request;

import lombok.Data;

import java.util.Date;
@Data
public class ModifyQuizRequest {
    private Integer quizId; // 测验ID
    private String title; // 测验标题
    private String subject; // 科目
    private String contentType; // 内容类型
    private String difficulty; // 难度
    private String knowledgePoints; // 知识点
    private String description; // 描述
    private String teacherId; // 教师ID

    private String teacherName; // 教师姓名

    private Date createTime; // 创建时间
    private Date deadline; // 截止时间
    private String class1; // 班级1
    private String class2; // 班级2

    public ModifyQuizRequest(Integer quizId,String title, String subject, String contentType, String difficulty,
                             String knowledgePoints, String description, String teacherId, Date createTime,Date deadline, String class1, String class2) {
        this.quizId = quizId;
        this.title = title;
        this.subject = subject;
        this.contentType = contentType;
        this.difficulty = difficulty;
        this.knowledgePoints = knowledgePoints;
        this.description = description;
        this.teacherId = teacherId;
        this.createTime = createTime;
        this.deadline = deadline;
        this.class1 = class1;
        this.class2 = class2;
    }
}
