package com.example.edupal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;

@Entity
public class Answer {
    @Id
    @Column(length = 8)
    private String answer_id;

    @Column(length = 8)
    private String question_id;

    @Lob
    private String answer_content;

    @Column(length = 8, nullable = false)
    private String related_question;

    @Column(nullable = false)
    private Integer answer_type;

    @Column(length = 10)
    private String teacher_id;

    // Getters and Setters
    public String getAnswer_id() {
        return answer_id;
    }

    public void setAnswer_id(String answer_id) {
        this.answer_id = answer_id;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getAnswer_content() {
        return answer_content;
    }

    public void setAnswer_content(String answer_content) {
        this.answer_content = answer_content;
    }

    public String getRelated_question() {
        return related_question;
    }

    public void setRelated_question(String related_question) {
        this.related_question = related_question;
    }

    public Integer getAnswer_type() {
        return answer_type;
    }

    public void setAnswer_type(Integer answer_type) {
        this.answer_type = answer_type;
    }

    public String getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(String teacher_id) {
        this.teacher_id = teacher_id;
    }
}