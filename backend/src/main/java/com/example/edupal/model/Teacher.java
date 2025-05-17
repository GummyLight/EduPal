package com.example.edupal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class Teacher {
    @Id
    @Column(length = 10)
    private String teacher_id;

    @Column(length = 10)
    private String teacher_name;

    @Column(length = 8)
    private String teaching_subject;

    @Column(length = 4)
    private String class_1;

    @Column(length = 4)
    private String class_2;

    // Getters and Setters
    public String getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(String teacher_id) {
        this.teacher_id = teacher_id;
    }

    public String getTeacher_name() {
        return teacher_name;
    }

    public void setTeacher_name(String teacher_name) {
        this.teacher_name = teacher_name;
    }

    public String getTeaching_subject() {
        return teaching_subject;
    }

    public void setTeaching_subject(String teaching_subject) {
        this.teaching_subject = teaching_subject;
    }

    public String getClass_1() {
        return class_1;
    }

    public void setClass_1(String class_1) {
        this.class_1 = class_1;
    }

    public String getClass_2() {
        return class_2;
    }

    public void setClass_2(String class_2) {
        this.class_2 = class_2;
    }
}