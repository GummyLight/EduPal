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
    public Result registerUser(String userId, String password, String email, Integer userType) {
        if (userRepository.existsByUserId(userId)) {
            return new Result(false, "用户名已存在");
        }
        if (userRepository.existsByUserEmail(email)) {
            return new Result(false, "邮箱已存在");
        }
        //任何一项为空都要报错
        if (userId == null || password == null || email == null) {
            return new Result(false, "未填写完整信息");
        }
        if(password.length() < 6){
            return new Result(false, "密码长度不能小于6");
        }
        User user = new User();
        user.setUserId(userId);
        user.setUserPassword(passwordEncoder.encode(password));
        user.setUserEmail(email);
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
    public Result loginUserByEmail(String email, String password) {
        User user = userRepository.findByUserEmail(email);

        if (user == null) {
            return new Result(false, "用户不存在");
        }
        if (!passwordEncoder.matches(password, user.getUserPassword())) {
            return new Result(false, "密码错误");
        }
        return new Result(true, "登录成功");
    }
}