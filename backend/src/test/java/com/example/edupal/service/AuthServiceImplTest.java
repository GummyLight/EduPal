package com.example.edupal.service;

import com.example.edupal.common.Result;
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

@SpringBootTest
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl userService;



    @BeforeEach
    public void setUp() {
        User mockUser= new User();
        mockUser.setUserId("testUserId");
        mockUser.setUserEmail("test@example.com");
        mockUser.setUserPassword("encodedPassword123");

        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);
        when(userRepository.findById("testUserId")).thenReturn(java.util.Optional.of(mockUser));
        when(userRepository.findByUserEmail("test@example.com")).thenReturn(mockUser);
    }

    @Test
    public void loginUserByUserId_ReturnsError_WhenPasswordIsNull() {
        Result result = userService.loginUserByUserId("testUserId", null);
        assertEquals(false, result.isSuccess());
        assertEquals("密码错误", result.getMessage());
    }

    @Test
    public void loginUserByEmail_ReturnsError_WhenEmailIsNull() {
        Result result = userService.loginUserByEmail(null, "password123");
        assertEquals(false, result.isSuccess());
        assertEquals("用户不存在", result.getMessage());
    }

    @Test
    public void loginUserByUserIdSuccess() {
        Result result = userService.loginUserByUserId("testUserId", "password123");
        assertEquals(true, result.isSuccess());
        assertEquals("登录成功", result.getMessage());
    }

    @Test
    public void loginUserByEmailSuccess() {
        Result result = userService.loginUserByEmail("test@example.com", "password123");
        assertEquals(true, result.isSuccess());
        assertEquals("登录成功", result.getMessage());
    }
}