package com.example.edupal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class Student {
    @Id
    @Column(length = 10)
    private String student_id;

    @Column(length = 10)
    private String student_name;

    @Column
    private Integer student_gender;

    @Column(length = 4)
    private String student_class;

    // Getters and Setters
    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public Integer getStudent_gender() {
        return student_gender;
    }

    public void setStudent_gender(Integer student_gender) {
        this.student_gender = student_gender;
    }

    public String getStudent_class() {
        return student_class;
    }

    public void setStudent_class(String student_class) {
        this.student_class = student_class;
    }
}
