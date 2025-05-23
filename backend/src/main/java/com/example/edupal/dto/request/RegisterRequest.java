package com.example.edupal.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String userId;
    private String password;
    private String phoneNum;
    private Integer userType;

    public RegisterRequest(String userId, String password, String phoneNum, Integer userType) {
        this.userId = userId;
        this.password = password;
        this.phoneNum = phoneNum;
        this.userType = userType;
    }
}