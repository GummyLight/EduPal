package com.example.edupal.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    private String userId;
    private String password;
    private Integer type; // 0 表示用户名，1 表示手机号

    public LoginRequest(String userId, String password, Integer type) {
        this.userId = userId;
        this.password = password;
        this.type = type;
    }

}