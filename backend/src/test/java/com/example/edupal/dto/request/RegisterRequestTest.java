package com.example.edupal.dto.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        // 初始化 RegisterRequest 对象
        String[] teacherClasses = {"Class A", "Class B"};
        registerRequest = new RegisterRequest(
                "John Doe",
                "user123",
                "password123",
                "john.doe@example.com",
                1,
                teacherClasses,
                "Mathematics",
                "Class A",
                0
        );
    }

    @Test
    void testGetUserName() {
        assertEquals("John Doe", registerRequest.getUserName());
    }

    @Test
    void testGetUserId() {
        assertEquals("user123", registerRequest.getUserId());
    }

    @Test
    void testGetPassword() {
        assertEquals("password123", registerRequest.getPassword());
    }

    @Test
    void testGetEmail() {
        assertEquals("john.doe@example.com", registerRequest.getEmail());
    }

    @Test
    void testGetUserType() {
        assertEquals(1, registerRequest.getUserType().intValue());
    }

    @Test
    void testGetTeacherClass() {
        assertArrayEquals(new String[]{"Class A", "Class B"}, registerRequest.getTeacherClass());
    }

    @Test
    void testGetTeachingSubject() {
        assertEquals("Mathematics", registerRequest.getTeachingSubject());
    }

    @Test
    void testGetStudentClass() {
        assertEquals("Class A", registerRequest.getStudentClass());
    }

    @Test
    void testGetStudentGender() {
        assertEquals(0, registerRequest.getStudentGender().intValue());
    }
}