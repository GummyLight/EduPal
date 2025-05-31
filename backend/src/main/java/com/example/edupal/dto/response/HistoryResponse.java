package com.example.edupal.dto.response;

import com.example.edupal.model.Answer;
import com.example.edupal.model.Question;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class HistoryResponse {
    private String status;
    private String message;
    private String studentId;
    private Integer questionNum;
    private List<QA> questionSet;

    public HistoryResponse(String status, String message, String studentId, List<QA> questionSet) {
        this.status = status;
        this.message = message;
        this.studentId = studentId;
        this.questionNum = questionSet.size();
        this.questionSet = questionSet;
    }

    @Data
    public static class QA {
        private String questionId;       // 问题ID
        private String questionContent;
        private String questionSubject;
        private Date questionTime;
        private int answerNum;
        private List<AnswerDetail> answers;

        public QA(Question question, List<Answer> answers) {
            this.questionId = question.getQuestionId();
            this.questionContent = question.getQuestionContent();
            this.questionSubject = question.getQuestionSubject();
            this.questionTime = question.getQuestionTime();
            this.answerNum = answers.size();
            this.answers = answers.stream().map(AnswerDetail::new).toList();
        }

        @Data
        public static class AnswerDetail {
            private String answerId;
            private String answerContent;
            private Integer answerType;
            private String teacherId;
            private Date answerTime;

            public AnswerDetail(Answer answer) {
                this.answerId = answer.getAnswerId();
                this.answerContent = answer.getAnswerContent();
                this.answerType = answer.getAnswerType();
                this.teacherId = answer.getTeacherId();
                this.answerTime = answer.getAnswerTime();
            }
        }
    }
}