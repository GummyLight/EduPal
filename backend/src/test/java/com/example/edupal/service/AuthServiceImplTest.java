package com.example.edupal.service;
import com.example.edupal.common.Result;
/*
* 单元测试:测试 UserServiceImpl 类的 registerUser 方法
* 单元测试：测试 UserServiceImpl 类的 loginUser 方法
* 单元测试：测试 UserServiceImpl 类的 registerUser 方法的异常情况
* 单元测试：测试 UserServiceImpl 类的 loginUser 方法的异常情况
* */
import com.example.edupal.model.User;
import com.example.edupal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

//Mockito 允许你在不连接实际数据库的情况下测试代码逻辑，通过模拟对象的行为来验证 UserServiceImpl 类的 registerUser 方法是否按预期工作。
@SpringBootTest
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl userService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUserId("testUserId");
        user.setUserPassword("encodedPassword");
        user.setUserPhoneNum("12345678901");
        user.setUserType(1);
    }

    @Test
    public void testRegisterUser_Success() {
        when(userRepository.existsByUserId("testUserId")).thenReturn(false);
        when(userRepository.existsByUserPhoneNum("12345678901")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        Result result = userService.registerUser("testUserId", "password123", "12345678901", 1);
        assertEquals(true, result.isSuccess());
        assertEquals("注册成功", result.getMessage());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testRegisterUser_UsernameExists() {
        when(userRepository.existsByUserId("testUserId")).thenReturn(true);

        Result result = userService.registerUser("testUserId", "password123", "12345678901", 1);
        assertEquals(false, result.isSuccess());
        assertEquals("用户名已存在", result.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testRegisterUser_PhoneNumberExists() {
        when(userRepository.existsByUserId("testUserId")).thenReturn(false);
        when(userRepository.existsByUserPhoneNum("12345678901")).thenReturn(true);

        Result result = userService.registerUser("testUserId", "password123", "12345678901", 1);
        assertEquals(false, result.isSuccess());
        assertEquals("手机号已存在", result.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testRegisterUser_IncompleteInformation() {
        Result result = userService.registerUser(null, "password123", "12345678901", 1);
        assertEquals(false, result.isSuccess());
        assertEquals("未填写完整信息", result.getMessage());

        verify(userRepository, never()).save(any(User.class));

        result = userService.registerUser("testUserId", null, "12345678901", 1);
        assertEquals(false, result.isSuccess());
        assertEquals("未填写完整信息", result.getMessage());

        verify(userRepository, never()).save(any(User.class));

        result = userService.registerUser("testUserId", "password123", null, 1);
        assertEquals(false, result.isSuccess());
        assertEquals("未填写完整信息", result.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testRegisterUser_PasswordTooShort() {
        when(userRepository.existsByUserId("testUserId")).thenReturn(false);
        when(userRepository.existsByUserPhoneNum("12345678901")).thenReturn(false);

        Result result = userService.registerUser("testUserId", "123", "12345678901", 1);
        assertEquals(false, result.isSuccess());
        assertEquals("密码长度不能小于6", result.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testLoginUserByUserId_Success() {
        when(userRepository.findById("testUserId")).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        Result result = userService.loginUserByUserId("testUserId", "password123");
        assertEquals(true, result.isSuccess());
        assertEquals("登录成功", result.getMessage());
    }

    @Test
    public void testLoginUserByUserId_UserNotFound() {
        when(userRepository.findById("testUserId")).thenReturn(java.util.Optional.empty());

        Result result = userService.loginUserByUserId("testUserId", "password123");
        assertEquals(false, result.isSuccess());
        assertEquals("用户不存在", result.getMessage());
    }

    @Test
    public void testLoginUserByUserId_PasswordMismatch() {
        when(userRepository.findById("testUserId")).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        Result result = userService.loginUserByUserId("testUserId", "wrongPassword");
        assertEquals(false, result.isSuccess());
        assertEquals("密码错误", result.getMessage());
    }

    @Test
    public void testLoginUserByPhoneNum_Success() {
        when(userRepository.findByUserPhoneNum("12345678901")).thenReturn(user);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        Result result = userService.loginUserByPhoneNum("12345678901", "password123");
        assertEquals(true, result.isSuccess());
        assertEquals("登录成功", result.getMessage());
    }

    @Test
    public void testLoginUserByPhoneNum_UserNotFound() {
        when(userRepository.findByUserPhoneNum("12345678901")).thenReturn(null);

        Result result = userService.loginUserByPhoneNum("12345678901", "password123");
        assertEquals(false, result.isSuccess());
        assertEquals("用户不存在", result.getMessage());
    }

    @Test
    public void testLoginUserByPhoneNum_PasswordMismatch() {
        when(userRepository.findByUserPhoneNum("12345678901")).thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        Result result = userService.loginUserByPhoneNum("12345678901", "wrongPassword");
        assertEquals(false, result.isSuccess());
        assertEquals("密码错误", result.getMessage());
    }
}