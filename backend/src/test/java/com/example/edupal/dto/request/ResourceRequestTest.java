package com.example.edupal.dto.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class ResourceRequestTest {

    private ResourceRequest resourceRequest;

    @BeforeEach
    void setUp() {
        // 初始化 ResourceRequest 对象
        resourceRequest = new ResourceRequest(1, "Mathematics", "T001", "Content of the resource", "Class A",
                "Resource Name", LocalDateTime.now(), "Description of the resource");
    }

    @Test
    void testGetResourceId() {
        assertEquals(1, resourceRequest.getResource_id());
    }

    @Test
    void testGetSubject() {
        assertEquals("Mathematics", resourceRequest.getSubject());
    }

    @Test
    void testGetTeacherId() {
        assertEquals("T001", resourceRequest.getTeacher_id());
    }

    @Test
    void testGetResourceContent() {
        assertEquals("Content of the resource", resourceRequest.getResource_content());
    }

    @Test
    void testGetClassId() {
        assertEquals("Class A", resourceRequest.getClass_id());
    }

    @Test
    void testGetName() {
        assertEquals("Resource Name", resourceRequest.getName());
    }

    @Test
    void testGetUploadTime() {
        assertNotNull(resourceRequest.getUpload_time());
    }

    @Test
    void testGetDescription() {
        assertEquals("Description of the resource", resourceRequest.getDescription());
    }

    @Test
    void testSetResourceId() {
        resourceRequest.setResource_id(2);
        assertEquals(2, resourceRequest.getResource_id());
    }

    @Test
    void testSetSubject() {
        resourceRequest.setSubject("Science");
        assertEquals("Science", resourceRequest.getSubject());
    }

    @Test
    void testSetTeacherId() {
        resourceRequest.setTeacher_id("T002");
        assertEquals("T002", resourceRequest.getTeacher_id());
    }

    @Test
    void testSetResourceContent() {
        resourceRequest.setResource_content("New content of the resource");
        assertEquals("New content of the resource", resourceRequest.getResource_content());
    }

    @Test
    void testSetClassId() {
        resourceRequest.setClass_id("Class B");
        assertEquals("Class B", resourceRequest.getClass_id());
    }

    @Test
    void testSetName() {
        resourceRequest.setName("New Resource Name");
        assertEquals("New Resource Name", resourceRequest.getName());
    }

    @Test
    void testSetUploadTime() {
        LocalDateTime newUploadTime = LocalDateTime.now().plusHours(1);
        resourceRequest.setUpload_time(newUploadTime);
        assertEquals(newUploadTime, resourceRequest.getUpload_time());
    }

    @Test
    void testSetDescription() {
        resourceRequest.setDescription("New description of the resource");
        assertEquals("New description of the resource", resourceRequest.getDescription());
    }
}