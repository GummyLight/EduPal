package com.example.edupal.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class GetMyQuizResponse {
    private String status;
    private String message;
    private String quizId; // 测验ID
    private String title; // 测验标题
    private String subject; // 科目
    private String difficulty; // 难度
    private Date createTime; // 创建时间
    private Date deadline; // 截止时间
    private String teacherName; // 教师姓名
    private String teacherId;
    private Integer QuizStatus;
    private Integer score;
    private String feedback; // 教师反馈
    private String answerId; // 答题内容ID

    public GetMyQuizResponse(String status, String message, String quizId, String title, String subject,
                             String difficulty, Date createTime, Date deadline, String teacherName,
                             String teacherId, Integer quizStatus, Integer score, String feedback,String answerId) {
        this.status = status == null ? "" : status;
        this.message = message == null ? "" : message;
        this.quizId = quizId == null ? "" : quizId;
        this.title = title == null ? "" : title;
        this.subject = subject == null ? "" : subject;
        this.difficulty = difficulty == null ? "" : difficulty;
        this.createTime = createTime;
        this.deadline = deadline;
        this.teacherName = teacherName == null ? "" : teacherName;
        this.teacherId = teacherId == null ? "" : teacherId;
        this.QuizStatus = quizStatus == null ? 0 : quizStatus;
        this.score = score == null ? 0 : score;
        this.feedback = feedback == null ? "" : feedback;
        this.answerId = answerId == null ? "" : answerId;
    }
}