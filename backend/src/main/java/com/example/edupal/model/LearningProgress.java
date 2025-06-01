package com.example.edupal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "learning_progress")
public class LearningProgress {
    @Id
    @Column(name = "progress_id", length = 10)
    private String progressId;

    @Column(name = "finished_lecture", nullable = false)
    private Integer finishedLecture;

    @Column(name = "unfinished_lecture", nullable = false)
    private Integer unfinishedLecture;

    @Column(name = "finished_quiz", nullable = false)
    private Integer finishedQuiz;

    @Column(name = "unfinished_quiz", nullable = false)
    private Integer unfinishedQuiz;

    @Column(name = "grade",length = 4)
    private String grade;

    @Column(name="student_id", length = 10, nullable = false)
    private String studentId;
}