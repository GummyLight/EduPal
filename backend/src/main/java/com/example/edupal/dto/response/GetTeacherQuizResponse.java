package com.example.edupal.dto.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GetTeacherQuizResponse {
    private String status;
    private String message;
    private String teacherId;
    private Integer quizNum;
    private List<quizDetail> quizzes;

    public GetTeacherQuizResponse(String success, String getYourQuiz, String userId, int size, List<quizDetail> quizzes) {
        this.status = success;
        this.message = getYourQuiz;
        this.teacherId = userId;
        this.quizNum = size;
        this.quizzes = quizzes;
    }

    @Data
    static public class quizDetail {
        private Integer quizId; // 测验ID
        private String title;
        private String subject;
        private String contentType;
        private String difficulty;
        private String knowledgePoints;
        private String description;
        private String teacherId;
        private String teacherName;
        private Date createTime;
        private Date updatedTime;
        private Date deadline;
        private Integer quizStatus; // 测验状态
        private Integer score; // 测验分数
        private Integer submitNum; // 提交人数
        private Integer unSubmitNum; // 未提交人数
        private Integer gradedNum; // 已评分人数

        public quizDetail(Integer quizId, String title, String subject, String contentType, String difficulty,
                          String knowledgePoints, String description, String teacherId, String teacherName,
                          Date createTime, Date updatedTime, Date deadline,Integer submitNum, Integer unSubmitNum, Integer gradedNum) {

            this.quizId = quizId;
            this.title = title;
            this.subject = subject;
            this.contentType = contentType;
            this.difficulty = difficulty;
            this.knowledgePoints = knowledgePoints;
            this.description = description;
            this.teacherId = teacherId;
            this.teacherName = teacherName;
            this.createTime = createTime;
            this.updatedTime = updatedTime;
            this.deadline = deadline;
            this.submitNum= submitNum;
            this.unSubmitNum= unSubmitNum;
            this.gradedNum= gradedNum;
        }

    }
}
