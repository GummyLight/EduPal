package com.example.edupal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Setter
@Getter
@Table(name = "quiz")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id", length = 8)
    private Integer quizId;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "subject", length = 255)
    private String subject;

    @Column(name = "content_type", length = 255)
    private String contentType;

    @Column(name = "difficulty", length = 255)
    private String difficulty;

    @Column(name = "knowledge_points", length = 255)
    private String knowledgePoints;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "teacher_id", length = 255)
    private String teacherId;

    @Column(name = "class1", length = 255)
    private String class1;

    @Column(name = "class2", length = 255)
    private String class2;

    @Temporal(TemporalType.DATE)
    @Column(name = "create_time")
    private Date createTime;

    @Temporal(TemporalType.DATE)
    @Column(name = "updated_time")
    private Date updatedTime;

    @Temporal(TemporalType.DATE)
    @Column(name = "deadline")
    private Date deadline;

    @Column(name = "teacher_name", length = 255)
    private String teacherName;
}