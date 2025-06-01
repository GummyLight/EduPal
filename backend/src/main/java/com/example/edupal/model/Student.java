package com.example.edupal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="student")
public class Student {
    // Getters and Setters
    @Id
    @Column(name="student_id",length = 10)
    private String studentId;

    @Column(name="student_name",length = 10)
    private String studentName;

    @Column(name="student_gender")
    private Integer studentGender;

    @Column(name="student_class",length = 4)
    private String studentClass;

}
