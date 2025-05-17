package com.example.edupal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class LearningProgress {
    @Id
    @Column(length = 10)
    private String progress_id;

    @Column(nullable = false)
    private Integer finished_lecture;

    @Column(nullable = false)
    private Integer unfinished_lecture;

    @Column(nullable = false)
    private Integer finished_quiz;

    @Column(nullable = false)
    private Integer unfinished_quiz;

    @Column
    private Integer grade;

    // Getters and Setters
    public String getProgress_id() {
        return progress_id;
    }

    public void setProgress_id(String progress_id) {
        this.progress_id = progress_id;
    }

    public Integer getFinished_lecture() {
        return finished_lecture;
    }

    public void setFinished_lecture(Integer finished_lecture) {
        this.finished_lecture = finished_lecture;
    }

    public Integer getUnfinished_lecture() {
        return unfinished_lecture;
    }

    public void setUnfinished_lecture(Integer unfinished_lecture) {
        this.unfinished_lecture = unfinished_lecture;
    }

    public Integer getFinished_quiz() {
        return finished_quiz;
    }

    public void setFinished_quiz(Integer finished_quiz) {
        this.finished_quiz = finished_quiz;
    }

    public Integer getUnfinished_quiz() {
        return unfinished_quiz;
    }

    public void setUnfinished_quiz(Integer unfinished_quiz) {
        this.unfinished_quiz = unfinished_quiz;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }
}