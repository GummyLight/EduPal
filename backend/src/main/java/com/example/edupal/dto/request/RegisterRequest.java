package com.example.edupal.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String userId;
    private String password;
    private String email;
    private Integer userType;

    public RegisterRequest(String userId, String password, String email, Integer userType) {
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.userType = userType;
    }
}