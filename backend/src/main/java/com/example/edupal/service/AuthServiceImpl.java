package com.example.edupal.service;

import cn.hutool.core.util.RandomUtil;
import com.example.edupal.model.User;
import com.example.edupal.model.Validation;
import com.example.edupal.repository.UserRepository;
import com.example.edupal.repository.ValidationRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.edupal.common.Result;
import com.example.edupal.service.EmailService;

import java.sql.Time;
import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationRepository validationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

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
        return new Result(true, "登录成功", user);
    }

    @Override
    public Result sendVerificationCode(String email) throws MessagingException {
        if (email == null || email.isEmpty()) {
            return new Result(false, "邮箱不能为空");
        }
        User user = userRepository.findByUserEmail(email);
        if (user == null) {
            return new Result(false, "用户不存在，请先注册");
        }
        //用当前的email在ValidationRepository中查找，查看有没有没有过期的验证码
        //如果有，直接返回验证码已发送
        //有可能查到很多记录
        Validation existingValidation = validationRepository.findByEmail(email);
        if (existingValidation != null) {
            if (!isExpired(existingValidation.getTime())) {
                return new Result(false, "验证码已发送，请检查您的邮箱");
            } else {
                // 如果查到的验证码过期了，删除它
                validationRepository.delete(existingValidation);
            }
        }

// 生成验证码
        String code = RandomUtil.randomNumbers(6); // 随机一个 6位长度的验证码

// 发送验证码到邮箱
        emailService.sendVerificationCodeToEmail(email, code);

// 保存验证码到数据库
        saveVerificationCode(email, code);

        return new Result(true, "验证码已发送，请检查您的邮箱");
    }

    @Override
    public Result resetPassword(String email, String code, String newPassword) {
        if (email == null || code == null || newPassword == null) {
            return new Result(false, "未填写完整信息");
        }
        if (newPassword.length() < 6) {
            return new Result(false, "密码长度不能小于6");
        }

        String verificationResult = verifyCode(email, code);
        if (!verificationResult.equals("验证码验证成功")) {
            return new Result(false, verificationResult);
        }

        // 验证成功，更新密码
        updateUserPassword(email, passwordEncoder.encode(newPassword));
        return new Result(true, "密码重置成功，请重新登录");
    }

    private String verifyCode(String email, String code) {
        // 从数据库验证邮箱和验证码是否匹配
        //用email在ValidationRepository中查找
        Validation validation = validationRepository.findByEmail(email);
        if (validation == null) {
            return "验证码不存在，请先获取验证码";
        }
        else if (isExpired(validation.getTime())) {
            //在Validation中删除过期的项目
            validationRepository.delete(validation);
            return "验证码已过期,请重新获取";
        }
        else if (!validation.getCode().equals(code)) {
            return "验证码错误，请重新输入";
        }

        return "验证码验证成功，请重新登录"; // 假设验证成功
    }

    private boolean isExpired(Date time) {
        // 检查Validation的time有没有早于现在的时间（Date类）
        Date now = new Date();
        return time.before(now); // 如果时间早于现在，视为过期
    }

    private void saveVerificationCode(String email, String code) {
        Validation validation = new Validation();
        validation.setEmail(email);
        validation.setCode(code);
        //将Validation的时间设置为当前时间+5分钟
        validation.setTime(new Date(System.currentTimeMillis() + 5 * 60 * 1000)); // 5分钟后过期
        validationRepository.save(validation);
    }

    private void updateUserPassword(String email, String newPassword) {
        User user = userRepository.findByUserEmail(email);
        if (user != null) {
            user.setUserPassword(newPassword);
            userRepository.save(user);
        } else {
            throw new RuntimeException("用户不存在");
        }
    }


}