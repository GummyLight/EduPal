package com.example.edupal.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        // 初始化用户对象
        user = new User("testUser", "test@example.com", "password123", 1, new Date(), null, "Test User");
    }

    @Test
    void testGetUserId() {
        assertEquals("testUser", user.getUserId());
    }

    @Test
    void testGetUserEmail() {
        assertEquals("test@example.com", user.getUserEmail());
    }

    @Test
    void testGetUserPassword() {
        assertEquals("password123", user.getUserPassword());
    }

    @Test
    void testGetUserType() {
        assertEquals(1, user.getUserType());
    }

    @Test
    void testGetCreateTime() {
        assertNotNull(user.getCreateTime());
    }

    @Test
    void testGetLoginTime() {
        assertNull(user.getLoginTime());
    }

    @Test
    void testGetUserName() {
        assertEquals("Test User", user.getUserName());
    }

    @Test
    void testSetUserId() {
        user.setUserId("newUserId");
        assertEquals("newUserId", user.getUserId());
    }

    @Test
    void testSetUserEmail() {
        user.setUserEmail("new@example.com");
        assertEquals("new@example.com", user.getUserEmail());
    }

    @Test
    void testSetUserPassword() {
        user.setUserPassword("newPassword123");
        assertEquals("newPassword123", user.getUserPassword());
    }

    @Test
    void testSetUserType() {
        user.setUserType(2);
        assertEquals(2, user.getUserType());
    }

    @Test
    void testSetCreateTime() {
        Date newCreateTime = new Date();
        user.setCreateTime(newCreateTime);
        assertEquals(newCreateTime, user.getCreateTime());
    }

    @Test
    void testSetLoginTime() {
        Date newLoginTime = new Date();
        user.setLoginTime(newLoginTime);
        assertEquals(newLoginTime, user.getLoginTime());
    }

    @Test
    void testSetUserName() {
        user.setUserName("New User");
        assertEquals("New User", user.getUserName());
    }
}