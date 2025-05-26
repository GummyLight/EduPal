package com.example.edupal.dto.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String message;
    private boolean success;
    private String userId;
    private String userName;
    private Integer userType;

    public LoginResponse(String message, boolean success, String userId, String userName, Integer userType) {
        this.message = message;
        this.success = success;
        this.userId = userId;
        this.userName = userName;
        this.userType = userType;
    }
}