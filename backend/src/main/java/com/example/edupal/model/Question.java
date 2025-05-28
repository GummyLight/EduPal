package com.example.edupal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "question")
public class Question {
    @Id
    @Column(name="question_id", length = 36, nullable = false)
    private String questionId;

    @PrePersist
    private void generateUUID() {
        if (this.questionId == null) {
            this.questionId = java.util.UUID.randomUUID().toString();
        }
    }

    @Lob
    @Column(name="question_content", nullable = false)
    private String questionContent;

    @Column(name="question_subject", length = 8, nullable = false)
    private String questionSubject;

    @Column(name="question_type",nullable = false)
    private Integer questionType;

    @Column(name="is_answered",nullable = false)
    private Integer isAnswered;

    @Column(name="student_id",length = 10)
    private String studentId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "question_time")
    private Date questionTime;

}
