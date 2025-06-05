package com.example.edupal.dto.request;

import lombok.Data;

import java.util.Date;
@Data
public class ModifyQuizRequest {
    private String quizId; // 测验ID
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

    public ModifyQuizRequest(String quizId,String title, String subject, String contentType, String difficulty,
                             String knowledgePoints, String description, String teacherId, Date createTime,Date deadline) {
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
    }
}
