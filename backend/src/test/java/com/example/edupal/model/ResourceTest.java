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
        resource.setResourceId(1);
        resource.setSubject("Mathematics");
        resource.setTeacherId("T001");
        resource.setResourceContent("Content of the resource.");
        resource.setClassId("Class A");
        resource.setName("Resource Name");
        resource.setUploadTime(LocalDateTime.now());
        resource.setDescription("This is a description of the resource.");
    }

    @Test
    void testGetResourceId() {
        assertEquals(1, resource.getResourceId());
    }

    @Test
    void testGetSubject() {
        assertEquals("Mathematics", resource.getSubject());
    }

    @Test
    void testGetTeacherId() {
        assertEquals("T001", resource.getTeacherId());
    }

    @Test
    void testGetResourceContent() {
        assertEquals("Content of the resource.", resource.getResourceContent());
    }

    @Test
    void testGetClassId() {
        assertEquals("Class A", resource.getClassId());
    }

    @Test
    void testGetName() {
        assertEquals("Resource Name", resource.getName());
    }

    @Test
    void testGetUploadTime() {
        assertNotNull(resource.getUploadTime());
    }

    @Test
    void testGetDescription() {
        assertEquals("This is a description of the resource.", resource.getDescription());
    }

    @Test
    void testSetResourceId() {
        resource.setResourceId(2);
        assertEquals(2, resource.getResourceId());
    }

    @Test
    void testSetSubject() {
        resource.setSubject("Science");
        assertEquals("Science", resource.getSubject());
    }

    @Test
    void testSetTeacherId() {
        resource.setTeacherId("T002");
        assertEquals("T002", resource.getTeacherId());
    }

    @Test
    void testSetResourceContent() {
        resource.setResourceContent("New content of the resource.");
        assertEquals("New content of the resource.", resource.getResourceContent());
    }

    @Test
    void testSetClassId() {
        resource.setClassId("Class B");
        assertEquals("Class B", resource.getClassId());
    }

    @Test
    void testSetName() {
        resource.setName("New Resource Name");
        assertEquals("New Resource Name", resource.getName());
    }

    @Test
    void testSetUploadTime() {
        LocalDateTime newUploadTime = LocalDateTime.now().plusHours(1);
        resource.setUploadTime(newUploadTime);
        assertEquals(newUploadTime, resource.getUploadTime());
    }

    @Test
    void testSetDescription() {
        resource.setDescription("New description of the resource.");
        assertEquals("New description of the resource.", resource.getDescription());
    }
}