package com.example.edupal.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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

    @Column(name = "teacherId", length = 10, nullable = false)
    private String teacherId;

    @Column(name = "studentId", length = 10, nullable = false)
    private String studentId;

    @Column(name = "QuestionId", length = 36, nullable = false)
    private String questionId;

    @Column(name = "AnswerId", length = 36)
    private String answerId;
}