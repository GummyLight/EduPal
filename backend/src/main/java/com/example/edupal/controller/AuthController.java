package com.example.edupal.controller;

import com.example.edupal.dto.request.LoginRequest;
import com.example.edupal.dto.request.RegisterRequest;
import com.example.edupal.dto.response.LoginResponse;
import com.example.edupal.dto.request.ResetPasswordRequest;
import com.example.edupal.dto.request.EmailCodeRequest;
import com.example.edupal.service.AuthService;
import com.example.edupal.common.ApiResponse;
import com.example.edupal.common.Result;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.edupal.model.User;

@RestController
@RequestMapping("/auth")
public class AuthController {

        @Autowired
        private AuthService authService;
    //Spring Boot 会自动处理 @RequestBody 注解的参数，将其从 JSON 格式反序列化为 Java 对象。同样，ResponseEntity 中的对象会自动被序列化为 JSON 格式返回给客户端。
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        Result result = authService.registerUser(request.getUserId(), request.getPassword(), request.getEmail(), request.getUserType());
        if (result.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(200, result.getMessage()));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, result.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Invalid request"));
        }

        Result result;
        if (request.getType() == 0) {
            result = authService.loginUserByUserId(request.getUserInput(), request.getPassword());
        } else {
            result = authService.loginUserByEmail(request.getUserInput(), request.getPassword());
        }

        if (result == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(500, "Internal Server Error"));
        }

        if (result.isSuccess()) {
            Object data = result.getData();
            if (!(data instanceof User user)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(500, "Invalid user data"));
            }

            if (user.getUserId() == null || user.getUserName() == null || user.getUserType() == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(500, "Incomplete user data"));
            }

            LoginResponse response = new LoginResponse(result.getMessage(), true, user.getUserId(), user.getUserName(), user.getUserType());
            return ResponseEntity.ok(new ApiResponse<>(200, "登录成功", response));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, result.getMessage()));
        }
    }

    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestBody EmailCodeRequest emailCodeRequest) throws MessagingException {
        if (emailCodeRequest.getEmail() == null || emailCodeRequest.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Email is required"));
        }

        Result result = authService.sendVerificationCode(emailCodeRequest.getEmail());
        if (result.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(200, result.getMessage()));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, result.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        if (resetPasswordRequest.getEmail() == null || resetPasswordRequest.getEmail().isEmpty() ||
                resetPasswordRequest.getCode() == null || resetPasswordRequest.getCode().isEmpty() ||
                resetPasswordRequest.getNewPassword() == null || resetPasswordRequest.getNewPassword().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Email, code, and new password are required"));
        }

        Result result = authService.resetPassword(resetPasswordRequest.getEmail(), resetPasswordRequest.getCode(), resetPasswordRequest.getNewPassword());
        if (result.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(200, result.getMessage()));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, result.getMessage()));
        }
    }

}