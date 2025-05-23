package com.example.edupal.controller;

import com.example.edupal.dto.request.LoginRequest;
import com.example.edupal.dto.request.RegisterRequest;
import com.example.edupal.dto.response.LoginResponse;
import com.example.edupal.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.edupal.common.Result;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        Result result = authService.registerUser(request.getUserId(), request.getPassword(), request.getPhoneNum(), request.getUserType());
        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getMessage());
        } else {
            return ResponseEntity.badRequest().body(result.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        Result result;
        if (request.getType() == 0) {
            result = authService.loginUserByUserId(request.getUserId(), request.getPassword());
        } else {
            result = authService.loginUserByPhoneNum(request.getUserId(), request.getPassword());
        }

        if (result.isSuccess()) {
            LoginResponse response = new LoginResponse(result.getMessage(), true);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(result.getMessage());
        }
    }
}
