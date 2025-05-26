package com.example.edupal.service;

import com.example.edupal.model.User;
import com.example.edupal.common.Result;
public interface AuthService {
    Result registerUser(String userId, String password, String phoneNum, Integer userType);
    Result loginUserByUserId(String userId, String password);
    Result loginUserByPhoneNum(String phoneNum, String password);
    User getUserById(String userId); // 新增方法，根据用户ID获取用户信息
}