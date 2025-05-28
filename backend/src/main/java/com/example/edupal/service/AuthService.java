package com.example.edupal.service;

import com.example.edupal.common.Result;
public interface AuthService {
    Result registerUser(String userId, String password, String phoneNum, Integer userType);
    Result loginUserByUserId(String userId, String password);
    Result loginUserByEmail(String email, String password);
}