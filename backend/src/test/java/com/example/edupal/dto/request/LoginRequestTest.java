package com.example.edupal.dto.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // 初始化 LoginRequest 对象
        loginRequest = new LoginRequest("user123", "password123", 0);
    }

    @Test
    void testGetUserInput() {
        assertEquals("user123", loginRequest.getUserInput());
    }

    @Test
    void testGetPassword() {
        assertEquals("password123", loginRequest.getPassword());
    }

    @Test
    void testGetType() {
        assertEquals(0, loginRequest.getType().intValue());
    }

    @Test
    void testConstructor_Username() {
        loginRequest = new LoginRequest("user123", "password123", 0);
        assertEquals("user123", loginRequest.getUserInput());
        assertEquals("password123", loginRequest.getPassword());
        assertEquals(0, loginRequest.getType().intValue());
    }

    @Test
    void testConstructor_Phone() {
        loginRequest = new LoginRequest("1234567890", "password123", 1);
        assertEquals("1234567890", loginRequest.getUserInput());
        assertEquals("password123", loginRequest.getPassword());
        assertEquals(1, loginRequest.getType().intValue());
    }
}