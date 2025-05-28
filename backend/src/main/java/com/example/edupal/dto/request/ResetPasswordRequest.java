package com.example.edupal.dto.request;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResetPasswordRequest {
    private String email;
    private String code;
    private String newPassword;
}
//curl -X POST http://localhost:8080/auth/reset-password  -H "Content-Type: application/json"  -d "{\"email\":\"1323858543@qq.com\", \"code\":\"043923\", \"newPassword\":\"password123\"}"