package com.example.edupal.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

class ValidationTest {

    private Validation validation;

    @BeforeEach
    void setUp() {
        // 初始化 Validation 对象
        validation = new Validation();
        validation.setId(1L);
        validation.setEmail("test@example.com");
        validation.setCode("123456");
        validation.setTime(new Date());
    }

    @Test
    void testGetId() {
        assertEquals(1L, validation.getId().longValue());
    }

    @Test
    void testGetEmail() {
        assertEquals("test@example.com", validation.getEmail());
    }

    @Test
    void testGetCode() {
        assertEquals("123456", validation.getCode());
    }

    @Test
    void testGetTime() {
        assertNotNull(validation.getTime());
    }

    @Test
    void testSetId() {
        validation.setId(2L);
        assertEquals(2L, validation.getId().longValue());
    }

    @Test
    void testSetEmail() {
        validation.setEmail("new@example.com");
        assertEquals("new@example.com", validation.getEmail());
    }

    @Test
    void testSetCode() {
        validation.setCode("654321");
        assertEquals("654321", validation.getCode());
    }

    @Test
    void testSetTime() {
        Date newTime = new Date();
        validation.setTime(newTime);
        assertEquals(newTime, validation.getTime());
    }
}