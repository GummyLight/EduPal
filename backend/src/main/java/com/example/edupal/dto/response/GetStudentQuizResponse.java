package com.example.edupal.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class GetStudentQuizResponse {
    private String status;
    private String message;
    private String studentId;
    private Integer quizNum;
    private List<quizDetail> quizDetails;

    public GetStudentQuizResponse(String status, String message, String studentId, Integer quizNum, List<quizDetail> quizDetails) {
        this.status = status;
        this.message = message;
        this.studentId = studentId;
        this.quizNum = quizNum;
        this.quizDetails = quizDetails;
    }

    @Data
    static public class quizDetail {
        private String quizId; // 测验ID
        private String title;
        private String subject;
        private String contentType;
        private String difficulty;
        private String knowledgePoints;
        private String description;
        private String teacherId;
        private String teacherName;
        private String createTime;
        private String updatedTime;
        private String deadline;
        private Integer quizStatus; // 测验状态
        private Integer score; // 测验分数

        public quizDetail(String quizId, String title, String subject, String contentType, String difficulty,
                          String knowledgePoints, String description, String teacherId, String teacherName,
                          String createTime, String updatedTime, String deadline,Integer quizStates, Integer score) {
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
            this.quizStatus=quizStates;
            this.score=score;
        }

    }


}
