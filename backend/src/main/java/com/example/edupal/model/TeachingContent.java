package com.example.edupal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TeachingContent {
    @Id
    @Column(length = 8)
    private String teaching_content_id;

    @Column(length = 8, nullable = false)
    private String subject;

    @Column(length = 10)
    private String teacher_id;

    @Lob
    private String teaching_content_content;

    @Column(length = 4)
    private String class_id;

    @Column(length = 50)
    private String name;

    private LocalDateTime upload_time;

    @Column(length = 255)
    private String description;

    // Getters and Setters
    public String getTeaching_content_id() {
        return teaching_content_id;
    }

    public void setTeaching_content_id(String teaching_content_id) {
        this.teaching_content_id = teaching_content_id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(String teacher_id) {
        this.teacher_id = teacher_id;
    }

    public String getTeaching_content_content() {
        return teaching_content_content;
    }

    public void setTeaching_content_content(String teaching_content_content) {
        this.teaching_content_content = teaching_content_content;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getUpload_time() {
        return upload_time;
    }

    public void setUpload_time(LocalDateTime upload_time) {
        this.upload_time = upload_time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}