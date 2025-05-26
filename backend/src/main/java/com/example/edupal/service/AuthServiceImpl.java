package com.example.edupal.service;

import com.example.edupal.model.User;
import com.example.edupal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.edupal.common.Result;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Result registerUser(String userId, String password, String phoneNum, Integer userType) {
        if (userRepository.existsByUserId(userId)) {
            return new Result(false, "用户名已存在");
        }
        if (userRepository.existsByUserPhoneNum(phoneNum)) {
            return new Result(false, "手机号已存在");
        }
        //任何一项为空都要报错
        if (userId == null || password == null || phoneNum == null) {
            return new Result(false, "未填写完整信息");
        }
        if(password.length() < 6){
            return new Result(false, "密码长度不能小于6");
        }
        User user = new User();
        user.setUserId(userId);
        user.setUserPassword(passwordEncoder.encode(password));
        user.setUserPhoneNum(phoneNum);
        user.setUserType(userType);
        userRepository.save(user);
        return new Result(true, "注册成功");
    }

    @Override
    public Result loginUserByUserId(String userId, String password) {
        User user = userRepository.findById(userId)
                .orElse(null);

        if (user == null) {
            return new Result(false, "用户不存在");
        }
        if (!passwordEncoder.matches(password, user.getUserPassword())) {
            return new Result(false, "密码错误");
        }
        return new Result(true, "登录成功", user);
    }

    @Override
    public Result loginUserByPhoneNum(String phoneNum, String password) {
        User user = userRepository.findByUserPhoneNum(phoneNum);

        if (user == null) {
            return new Result(false, "用户不存在");
        }
        if (!passwordEncoder.matches(password, user.getUserPassword())) {
            return new Result(false, "密码错误");
        }
        return new Result(true, "登录成功");
    }

    @Override
    public User getUserById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }
}