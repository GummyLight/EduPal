package com.example.edupal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;

@Entity
public class Quiz {
    @Id
    @Column(length = 8)
    private String quiz_id;

    @Column(length = 8, nullable = false)
    private String quiz_subject;

    @Lob
    private String quiz_content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date publish_time;

    @Temporal(TemporalType.TIMESTAMP)
    private Date deadline;

    // Getters and Setters
    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getQuiz_subject() {
        return quiz_subject;
    }

    public void setQuiz_subject(String quiz_subject) {
        this.quiz_subject = quiz_subject;
    }

    public String getQuiz_content() {
        return quiz_content;
    }

    public void setQuiz_content(String quiz_content) {
        this.quiz_content = quiz_content;
    }

    public Date getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(Date publish_time) {
        this.publish_time = publish_time;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
}