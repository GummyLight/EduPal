package com.example.edupal.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailCodeRequest {
    private String email;
}

//curl -X POST http://localhost:8080/auth/send-code -H "Content-Type: application/json"  -d "{\"email\":\"1323858543@qq.com\"}"