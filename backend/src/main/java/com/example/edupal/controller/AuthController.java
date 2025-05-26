package com.example.edupal.controller;

import com.example.edupal.dto.request.LoginRequest;
import com.example.edupal.dto.request.RegisterRequest;
import com.example.edupal.dto.response.LoginResponse;
import com.example.edupal.service.AuthService;
import com.example.edupal.common.ApiResponse;
import com.example.edupal.common.Result;
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

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        Result result = authService.registerUser(request.getUserId(), request.getPassword(), request.getPhoneNum(), request.getUserType());
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
            result = authService.loginUserByUserId(request.getUserId(), request.getPassword());
        } else {
            result = authService.loginUserByPhoneNum(request.getUserId(), request.getPassword());
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
            return ResponseEntity.ok(new ApiResponse<>(200, "Login successful", response));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, result.getMessage()));
        }
    }
}