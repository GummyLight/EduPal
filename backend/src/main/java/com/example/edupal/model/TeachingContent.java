package com.example.edupal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
public class TeachingContent {
    // Getters and Setters
    @Id
    @Column(length = 36)
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

}