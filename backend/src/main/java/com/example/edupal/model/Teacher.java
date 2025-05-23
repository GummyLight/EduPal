package com.example.edupal.model;

import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "teacher")
public class Teacher {
    @Id
    @Column(length = 10)
    private String teacherId;

    @Column(length = 10)
    private String teacherName;

    @Column(length = 8)
    private String teachingSubject;

    @Column(length = 4)
    private String class1;

    @Column(length = 4)
    private String class2;
}