package com.example.edupal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
public class Resource {
    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 可选，表示自动增长
    private int resource_id;


    @Column(length = 10, nullable = false)
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

}
