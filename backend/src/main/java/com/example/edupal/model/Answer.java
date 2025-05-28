package com.example.edupal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "answer")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="answer_id",length = 36,nullable = false)
    private String answerId;

    @Column(name="question_id", length = 36, nullable = false)
    private String questionId;

    @Lob
    @Column(name="answer_content", nullable = false)
    private String answerContent;

    @Lob
    @Column(name="related_question")
    private String relatedQuestion;

    @Column(name="answer_type", nullable = false)
    private Integer answerType;

    @Column(name="teacher_id")
    private String teacherId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "answer_time")
    private Date answerTime;
}