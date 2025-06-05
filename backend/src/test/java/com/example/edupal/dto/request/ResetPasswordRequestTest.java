package com.example.edupal.dto.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResetPasswordRequestTest {

    private ResetPasswordRequest resetPasswordRequest;

    @BeforeEach
    void setUp() {
        // 初始化 ResetPasswordRequest 对象
        resetPasswordRequest = new ResetPasswordRequest();
        resetPasswordRequest.setEmail("user@example.com");
        resetPasswordRequest.setCode("123456");
        resetPasswordRequest.setNewPassword("newPassword123");
    }

    @Test
    void testGetEmail() {
        assertEquals("user@example.com", resetPasswordRequest.getEmail());
    }

    @Test
    void testGetCode() {
        assertEquals("123456", resetPasswordRequest.getCode());
    }

    @Test
    void testGetNewPassword() {
        assertEquals("newPassword123", resetPasswordRequest.getNewPassword());
    }

    @Test
    void testSetEmail() {
        resetPasswordRequest.setEmail("newuser@example.com");
        assertEquals("newuser@example.com", resetPasswordRequest.getEmail());
    }

    @Test
    void testSetCode() {
        resetPasswordRequest.setCode("654321");
        assertEquals("654321", resetPasswordRequest.getCode());
    }

    @Test
    void testSetNewPassword() {
        resetPasswordRequest.setNewPassword("newNewPassword123");
        assertEquals("newNewPassword123", resetPasswordRequest.getNewPassword());
    }
}