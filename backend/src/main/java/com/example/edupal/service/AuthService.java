package com.example.edupal.service;

import com.example.edupal.common.Result;
import jakarta.mail.MessagingException;

public interface AuthService {
    Result registerUser(String userId, String password, String phoneNum, Integer userType);
    Result loginUserByUserId(String userId, String password);
    Result loginUserByEmail(String email, String password);
    Result sendVerificationCode(String mail) throws MessagingException;
    Result resetPassword(String email, String code, String newPassword);

}