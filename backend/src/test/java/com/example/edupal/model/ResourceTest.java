package com.example.edupal.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class ResourceTest {

    private Resource resource;

    @BeforeEach
    void setUp() {
        // 初始化 Resource 对象
        resource = new Resource();
        resource.setResource_id(1);
        resource.setSubject("Mathematics");
        resource.setTeacher_id("T001");
        resource.setResource_content("Content of the resource.");
        resource.setClass_id("Class A");
        resource.setName("Resource Name");
        resource.setUpload_time(LocalDateTime.now());
        resource.setDescription("This is a description of the resource.");
    }

    @Test
    void testGetResourceId() {
        assertEquals(1, resource.getResource_id());
    }

    @Test
    void testGetSubject() {
        assertEquals("Mathematics", resource.getSubject());
    }

    @Test
    void testGetTeacherId() {
        assertEquals("T001", resource.getTeacher_id());
    }

    @Test
    void testGetResourceContent() {
        assertEquals("Content of the resource.", resource.getResource_content());
    }

    @Test
    void testGetClassId() {
        assertEquals("Class A", resource.getClass_id());
    }

    @Test
    void testGetName() {
        assertEquals("Resource Name", resource.getName());
    }

    @Test
    void testGetUploadTime() {
        assertNotNull(resource.getUpload_time());
    }

    @Test
    void testGetDescription() {
        assertEquals("This is a description of the resource.", resource.getDescription());
    }

    @Test
    void testSetResourceId() {
        resource.setResource_id(2);
        assertEquals(2, resource.getResource_id());
    }

    @Test
    void testSetSubject() {
        resource.setSubject("Science");
        assertEquals("Science", resource.getSubject());
    }

    @Test
    void testSetTeacherId() {
        resource.setTeacher_id("T002");
        assertEquals("T002", resource.getTeacher_id());
    }

    @Test
    void testSetResourceContent() {
        resource.setResource_content("New content of the resource.");
        assertEquals("New content of the resource.", resource.getResource_content());
    }

    @Test
    void testSetClassId() {
        resource.setClass_id("Class B");
        assertEquals("Class B", resource.getClass_id());
    }

    @Test
    void testSetName() {
        resource.setName("New Resource Name");
        assertEquals("New Resource Name", resource.getName());
    }

    @Test
    void testSetUploadTime() {
        LocalDateTime newUploadTime = LocalDateTime.now().plusHours(1);
        resource.setUpload_time(newUploadTime);
        assertEquals(newUploadTime, resource.getUpload_time());
    }

    @Test
    void testSetDescription() {
        resource.setDescription("New description of the resource.");
        assertEquals("New description of the resource.", resource.getDescription());
    }
}