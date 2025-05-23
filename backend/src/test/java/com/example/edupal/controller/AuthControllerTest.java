package com.example.edupal.controller;

import com.example.edupal.common.Result;
import com.example.edupal.dto.request.LoginRequest;
import com.example.edupal.dto.request.RegisterRequest;
import com.example.edupal.dto.response.LoginResponse;
import com.example.edupal.service.AuthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    }

    @Test
    public void testRegisterUser_Success() {
        RegisterRequest request = new RegisterRequest("s20221234", "password123", "12345678903", 1);
        when(authService.registerUser("s20221234", "password123", "12345678903", 1))
                .thenReturn(new Result(true, "注册成功"));

        ResponseEntity<?> response = authController.registerUser(request);
        Assertions.assertEquals("注册成功", response.getBody());
    }

    @Test
    public void testRegisterUser_UserAlreadyExists() {
        RegisterRequest request = new RegisterRequest("s20221234", "password123", "12345678903", 1);
        when(authService.registerUser("s20221234", "password123", "12345678903", 1))
                .thenReturn(new Result(false, "用户名已存在"));

        ResponseEntity<?> response = authController.registerUser(request);
        Assertions.assertEquals("用户名已存在", response.getBody());
    }

    @Test
    public void testRegisterUser_PhoneNumberAlreadyExists() {
        RegisterRequest request = new RegisterRequest("s20221234", "password123", "12345678903", 1);
        when(authService.registerUser("s20221234", "password123", "12345678903", 1))
                .thenReturn(new Result(false, "手机号已存在"));

        ResponseEntity<?> response = authController.registerUser(request);
        Assertions.assertEquals("手机号已存在", response.getBody());
    }

    @Test
    public void testRegisterUser_IncompleteInformation() {
        RegisterRequest request = new RegisterRequest("", "password123", "12345678903", 1);
        when(authService.registerUser("", "password123", "12345678903", 1))
                .thenReturn(new Result(false, "未填写完整信息"));

        ResponseEntity<?> response = authController.registerUser(request);
        Assertions.assertEquals("未填写完整信息", response.getBody());

        request = new RegisterRequest("s20221234", "", "12345678903", 1);
        when(authService.registerUser("s20221234", "", "12345678903", 1))
                .thenReturn(new Result(false, "未填写完整信息"));

        response = authController.registerUser(request);
        Assertions.assertEquals("未填写完整信息", response.getBody());

        request = new RegisterRequest("s20221234", "password123", "", 1);
        when(authService.registerUser("s20221234", "password123", "", 1))
                .thenReturn(new Result(false, "未填写完整信息"));

        response = authController.registerUser(request);
        Assertions.assertEquals("未填写完整信息", response.getBody());
    }

    @Test
    public void testRegisterUser_PasswordTooShort() {
        RegisterRequest request = new RegisterRequest("s20221234", "123", "12345678903", 1);
        when(authService.registerUser("s20221234", "123", "12345678903", 1))
                .thenReturn(new Result(false, "密码长度不能小于6"));

        ResponseEntity<?> response = authController.registerUser(request);
        Assertions.assertEquals("密码长度不能小于6", response.getBody());
    }

    @Test
    public void testLoginUser_Success() {
        LoginRequest request = new LoginRequest("s20221212", "password123", 0);
        when(authService.loginUserByUserId("s20221212", "password123"))
                .thenReturn(new Result(true, "登录成功"));

        ResponseEntity<?> response = authController.loginUser(request);
        LoginResponse loginResponse = (LoginResponse) response.getBody();
        Assertions.assertEquals("登录成功", loginResponse.getMessage());
    }

    @Test
    public void testLoginUser_UserNotFound() {
        LoginRequest request = new LoginRequest("s20221212", "password123", 0);
        when(authService.loginUserByUserId("s20221212", "password123"))
                .thenReturn(new Result(false, "用户不存在"));

        ResponseEntity<?> response = authController.loginUser(request);
        Assertions.assertEquals("用户不存在", response.getBody());
    }

    @Test
    public void testLoginUser_InvalidPassword() {
        LoginRequest request = new LoginRequest("s20221212", "wrongPassword", 0);
        when(authService.loginUserByUserId("s20221212", "wrongPassword"))
                .thenReturn(new Result(false, "密码错误"));

        ResponseEntity<?> response = authController.loginUser(request);
        Assertions.assertEquals("密码错误", response.getBody());
    }

    @Test
    public void testLoginUserByPhoneNum_Success() {
        LoginRequest request = new LoginRequest("12345678901", "password123", 1);
        when(authService.loginUserByPhoneNum("12345678901", "password123"))
                .thenReturn(new Result(true, "登录成功"));

        ResponseEntity<?> response = authController.loginUser(request);
        LoginResponse loginResponse = (LoginResponse) response.getBody();
        Assertions.assertEquals("登录成功", loginResponse.getMessage());
    }

    @Test
    public void testLoginUserByPhoneNum_UserNotFound() {
        LoginRequest request = new LoginRequest("12345678901", "password123", 1);
        when(authService.loginUserByPhoneNum("12345678901", "password123"))
                .thenReturn(new Result(false, "用户不存在"));

        ResponseEntity<?> response = authController.loginUser(request);
        Assertions.assertEquals("用户不存在", response.getBody());
    }

    @Test
    public void testLoginUserByPhoneNum_InvalidPassword() {
        LoginRequest request = new LoginRequest("12345678901", "wrongPassword", 1);
        when(authService.loginUserByPhoneNum("12345678901", "wrongPassword"))
                .thenReturn(new Result(false, "密码错误"));

        ResponseEntity<?> response = authController.loginUser(request);
        Assertions.assertEquals("密码错误", response.getBody());
    }

    @Test
    public void testRegisterUser_WithEmptyUsername() {
        RegisterRequest request = new RegisterRequest("", "password123", "12345678903", 1);
        when(authService.registerUser("", "password123", "12345678903", 1))
                .thenReturn(new Result(false, "未填写完整信息"));

        ResponseEntity<?> response = authController.registerUser(request);
        Assertions.assertEquals("未填写完整信息", response.getBody());
    }

    @Test
    public void testLoginUser_WithEmptyUsername() {
        LoginRequest request = new LoginRequest("", "password123", 0);
        when(authService.loginUserByUserId("", "password123"))
                .thenReturn(new Result(false, "用户不存在"));

        ResponseEntity<?> response = authController.loginUser(request);
        Assertions.assertEquals("用户不存在", response.getBody());
    }

    @Test
    public void testRegisterUser_WithEmptyPassword() {
        RegisterRequest request = new RegisterRequest("s20221234", "", "12345678903", 1);
        when(authService.registerUser("s20221234", "", "12345678903", 1))
                .thenReturn(new Result(false, "未填写完整信息"));

        ResponseEntity<?> response = authController.registerUser(request);
        Assertions.assertEquals("未填写完整信息", response.getBody());
    }

    @Test
    public void testLoginUser_WithEmptyPassword() {
        LoginRequest request = new LoginRequest("s20221212", "", 0);
        when(authService.loginUserByUserId("s20221212", ""))
                .thenReturn(new Result(false, "密码错误"));

        ResponseEntity<?> response = authController.loginUser(request);
        Assertions.assertEquals("密码错误", response.getBody());
    }
}