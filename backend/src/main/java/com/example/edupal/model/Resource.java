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
    private int resourceId;


    @Column(length = 8, nullable = false)
    private String subject;

    @Column(length = 10)
    private String teacherId;

    @Lob
    private String resourceContent;

    @Column(length = 4)
    private String classId;

    @Column(length = 50)
    private String name;

    private LocalDateTime uploadTime;

    @Column(length = 255)
    private String description;

}
