package com.example.edupal.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String userName;
    private String userId;
    private String password;
    private String email;
    private Integer userType;
    private String[] teacherClass;
    private String teachingSubject;
    private String studentClass;
    private Integer studentGender;

    public RegisterRequest(String userName, String userId, String password, String email,
                           Integer userType, String[] teacherClass,
                           String teachingSubject, String studentClass, Integer studentGender) {
        this.userName = userName;
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.userType = userType;
        this.teacherClass = teacherClass;
        this.teachingSubject = teachingSubject;
        this.studentClass = studentClass;
        this.studentGender = studentGender;

    }
}