package com.example.edupal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;

@Entity
public class Question {
    @Id
    @Column(length = 8)
    private String question_id;

    @Column(length = 8)
    private String answer_id;

    @Lob
    private String question_content;

    @Column(length = 8, nullable = false)
    private String question_subject;

    @Column(nullable = false)
    private Integer question_type;

    @Column(nullable = false)
    private Integer is_answered;

    @Column(length = 8)
    private String related_answer;

    @Column(length = 10)
    private String student_id;

    // Getters and Setters
    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getAnswer_id() {
        return answer_id;
    }

    public void setAnswer_id(String answer_id) {
        this.answer_id = answer_id;
    }

    public String getQuestion_content() {
        return question_content;
    }

    public void setQuestion_content(String question_content) {
        this.question_content = question_content;
    }

    public String getQuestion_subject() {
        return question_subject;
    }

    public void setQuestion_subject(String question_subject) {
        this.question_subject = question_subject;
    }

    public Integer getQuestion_type() {
        return question_type;
    }

    public void setQuestion_type(Integer question_type) {
        this.question_type = question_type;
    }

    public Integer getIs_answered() {
        return is_answered;
    }

    public void setIs_answered(Integer is_answered) {
        this.is_answered = is_answered;
    }

    public String getRelated_answer() {
        return related_answer;
    }

    public void setRelated_answer(String related_answer) {
        this.related_answer = related_answer;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }
}
