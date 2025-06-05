package com.example.edupal.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

class ListUserResponseTest {

    private ListUserResponse listUserResponse;

    @BeforeEach
    void setUp() {
        // 初始化 ListUserResponse 对象
        List<ListUserResponse.UserDetail> users = Arrays.asList(
                new ListUserResponse.UserDetail("U001", "user1@example.com", 1, "John Doe", new Date(), new Date()),
                new ListUserResponse.UserDetail("U002", "user2@example.com", 2, "Jane Doe", new Date(), new Date())
        );
        listUserResponse = new ListUserResponse("success", "Users retrieved successfully", 2, users);
    }

    @Test
    void testGetStatus() {
        assertEquals("success", listUserResponse.getStatus());
    }

    @Test
    void testGetMessage() {
        assertEquals("Users retrieved successfully", listUserResponse.getMessage());
    }

    @Test
    void testGetUserNum() {
        assertEquals(2, listUserResponse.getUserNum().intValue());
    }

    @Test
    void testGetUsers() {
        List<ListUserResponse.UserDetail> users = listUserResponse.getUsers();
        assertNotNull(users);
        assertEquals(2, users.size());

        ListUserResponse.UserDetail user1 = users.get(0);
        assertEquals("U001", user1.getUserId());
        assertEquals("user1@example.com", user1.getUserEmail());
        assertEquals(1, user1.getUserType().intValue());
        assertEquals("John Doe", user1.getUserName());
        assertNotNull(user1.getCreateTime());
        assertNotNull(user1.getLoginTime());

        ListUserResponse.UserDetail user2 = users.get(1);
        assertEquals("U002", user2.getUserId());
        assertEquals("user2@example.com", user2.getUserEmail());
        assertEquals(2, user2.getUserType().intValue());
        assertEquals("Jane Doe", user2.getUserName());
        assertNotNull(user2.getCreateTime());
        assertNotNull(user2.getLoginTime());
    }
}