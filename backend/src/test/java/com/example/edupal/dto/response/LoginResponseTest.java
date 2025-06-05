package com.example.edupal.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginResponseTest {

    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        // 初始化 LoginResponse 对象
        loginResponse = new LoginResponse("Login successful", true, "user123", "John Doe", 1);
    }

    @Test
    void testGetMessage() {
        assertEquals("Login successful", loginResponse.getMessage());
    }

    @Test
    void testGetSuccess() {
        assertTrue(loginResponse.isSuccess());
    }

    @Test
    void testGetUserId() {
        assertEquals("user123", loginResponse.getUserId());
    }

    @Test
    void testGetUserName() {
        assertEquals("John Doe", loginResponse.getUserName());
    }

    @Test
    void testGetUserType() {
        assertEquals(1, loginResponse.getUserType().intValue());
    }

    @Test
    void testConstructor_withFailure() {
        LoginResponse failedResponse = new LoginResponse("Login failed", false, null, null, null);
        assertFalse(failedResponse.isSuccess());
        assertNull(failedResponse.getUserId());
        assertNull(failedResponse.getUserName());
        assertNull(failedResponse.getUserType());
    }
}