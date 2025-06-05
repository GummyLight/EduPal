package com.example.edupal.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class GetQuizStudentResponse {
    private String status;
    private String message;
    private Integer quizId;
    private Integer answerNum; // 答题人数
    List<answerDetail> answers;

    public GetQuizStudentResponse(String status, String message, Integer quizId, Integer answerNum, List<answerDetail> answers) {
        this.status = status;
        this.message = message;
        this.quizId = quizId;
        this.answerNum = answerNum;
        this.answers = answers;
    }

    @Data
    static public class answerDetail {
        private String studentId; // 学生ID
        private String userName; // 学生姓名
        private Integer status; // 答题状态
        private Integer score; // 分数
        private String answerTime; // 答题时间
        private Integer answerId; // 答题内容
        private String answerContent; // 答题内容

        public answerDetail(String studentId, String userName, Integer status,Integer score, String answerTime,Integer answerId, String answerContent) {
            this.studentId = studentId;
            this.userName = userName;
            this.status = status;
            this.score = score;
            this.answerTime = answerTime;
            this.answerId = answerId; // 生成唯一的答题ID
            this.answerContent = answerContent;
        }
    }
}
