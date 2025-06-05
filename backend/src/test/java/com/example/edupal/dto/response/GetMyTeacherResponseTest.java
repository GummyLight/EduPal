package com.example.edupal.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

class GetMyTeacherResponseTest {

    private GetMyTeacherResponse getMyTeacherResponse;

    @BeforeEach
    void setUp() {
        // 初始化 GetMyTeacherResponse 对象
        List<GetMyTeacherResponse.TItem> teachers = Arrays.asList(
                new GetMyTeacherResponse.TItem("T001", "John Doe", "Mathematics"),
                new GetMyTeacherResponse.TItem("T002", "Jane Doe", "Science")
        );
        getMyTeacherResponse = new GetMyTeacherResponse("success", "获取教师列表成功", teachers);
    }

    @Test
    void testGetStatus() {
        assertEquals("success", getMyTeacherResponse.getStatus());
    }

    @Test
    void testGetMessage() {
        assertEquals("获取教师列表成功", getMyTeacherResponse.getMessage());
    }

    @Test
    void testGetTeacherNum() {
        assertEquals(2, getMyTeacherResponse.getTeacherNum().intValue());
    }

    @Test
    void testGetTeachers() {
        List<GetMyTeacherResponse.TItem> teachers = getMyTeacherResponse.getTeachers();
        assertNotNull(teachers);
        assertEquals(2, teachers.size());

        GetMyTeacherResponse.TItem teacher1 = teachers.get(0);
        assertEquals("T001", teacher1.getTeacherId());
        assertEquals("John Doe", teacher1.getTeacherName());
        assertEquals("Mathematics", teacher1.getTeacherSubject());

        GetMyTeacherResponse.TItem teacher2 = teachers.get(1);
        assertEquals("T002", teacher2.getTeacherId());
        assertEquals("Jane Doe", teacher2.getTeacherName());
        assertEquals("Science", teacher2.getTeacherSubject());
    }
}