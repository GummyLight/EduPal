package com.example.edupal.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Data
@Entity
@Table(name = "teacher_answer")
@NoArgsConstructor
@AllArgsConstructor
public class TeacherAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, unique = true)
    private Integer id;

    @Column(name = "teacher_id", length = 10, nullable = false)
    private String teacherId;

    @Column(name = "student_id", length = 10, nullable = false)
    private String studentId;

    @Column(name = "question_id", length = 36, nullable = false)
    private String questionId;

    @Column(name = "answer_id", length = 36)
    private String answerId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "trans_time", nullable = false)
    private Date transTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "answer_time")
    private Date answerTime;
}