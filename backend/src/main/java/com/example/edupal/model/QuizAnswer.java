package com.example.edupal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Setter
@Getter
@Table(name = "quiz_answer")
public class QuizAnswer {
    @Id
    @Column(name = "answer_id", length = 36)
    private String answerId;

    @Column(name = "quiz_id", length = 255)
    private String quizId;

    @Column(name = "student_id", length = 255)
    private String studentId;

    @Lob
    @Column(name = "answer_content")
    private String answerContent;

    @Column(name = "score")
    private Integer score;

    @Lob
    @Column(name = "feedback")
    private String feedback;

    @Column(name = "is_graded")
    private Integer isGraded;

    @Temporal(TemporalType.DATE)
    @Column(name = "submit_time")
    private Date submitTime;

    @Temporal(TemporalType.DATE)
    @Column(name = "grade_time")
    private Date gradeTime;

    @Column(name = "is_deleted", columnDefinition = "int default 0")
    private Integer isDeleted;
}