package com.example.edupal.dto.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String message;
    private boolean success;

    public LoginResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
}