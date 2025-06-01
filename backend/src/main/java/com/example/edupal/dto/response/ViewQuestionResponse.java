package com.example.edupal.dto.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ViewQuestionResponse {
    private String status;
    private String message;
    private String teacherId; // 教师ID
    private Integer qaNum;    // 问题数量
    private List<QA> questionSet; // 问题集合

    public ViewQuestionResponse(String status, String message, String teacherId, Integer qaNum, List<QA> questionSet) {
        this.status = status;
        this.message = message;
        this.teacherId = teacherId;
        this.qaNum = qaNum;
        this.questionSet = questionSet;
    }

    @Data
    public static class QA {
        private String studentName;       // 提问学生姓名
        private String studentId;         // 提问学生ID
        private String studentClass;      // 提问学生班级
        private String questionContent;   // 提问内容
        private Date transferTime;        // 问题转交的时间
        private List<AnswerDetail> teacherAnswers; // 教师回答集合

        public QA(String studentName, String studentId, String studentClass, String questionContent,
                  Date transferTime, List<AnswerDetail> teacherAnswers) {
            this.studentName = studentName;
            this.studentId = studentId;
            this.studentClass = studentClass;
            this.questionContent = questionContent;
            this.transferTime = transferTime;
            this.teacherAnswers = teacherAnswers;
        }

        @Data
        public static class AnswerDetail {
            private Integer answerType;    // 回答类型
            private String answerContent;  // 回答内容
            private Date answerTime;       // 回答时间

            public AnswerDetail(Integer answerType, String answerContent, Date answerTime) {
                this.answerType = answerType;
                this.answerContent = answerContent;
                this.answerTime = answerTime;
            }
        }
    }
}