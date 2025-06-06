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

    public Student(String s1, String class1) {
        this.studentId = s1;
        this.studentName = "";
        this.studentGender = 0; // 默认值
        this.studentClass = class1;

    }
    public Student() {
        // 默认构造函数
    }
}
