package com.example.edupal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 可选，表示自动增长
    private int resource_id;


    @Column(length = 8, nullable = false)
    private String subject;

    @Column(length = 10)
    private String teacher_id;

    @Lob
    private String resource_content;

    @Column(length = 4)
    private String class_id;

    @Column(length = 50)
    private String name;

    private LocalDateTime upload_time;

    @Column(length = 255)
    private String description;

    // Getters and Setters
    public int getResource_id() {
        return resource_id;
    }

    public void setResource_id(int resource_id) {
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
