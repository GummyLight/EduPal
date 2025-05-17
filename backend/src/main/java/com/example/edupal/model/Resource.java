package com.example.edupal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;

@Entity
public class Resource {
    @Id
    @Column(length = 8)
    private String resource_id;

    @Column(length = 8, nullable = false)
    private String subject;

    @Column(length = 10)
    private String teacher_id;

    @Lob
    private String resource_content;

    @Column(length = 4)
    private String class_id;

    // Getters and Setters
    public String getResource_id() {
        return resource_id;
    }
    public void setResource_id(String resource_id) {
        this.resource_id = resource_id;
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

    public String getResource_content() {
        return resource_content;
    }
    public void setResource_content(String resource_content) {
        this.resource_content = resource_content;
    }

    public String getClass_id() {
        return class_id;
    }
    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }
}
