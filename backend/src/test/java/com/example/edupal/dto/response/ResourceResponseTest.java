package com.example.edupal.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

class ResourceResponseTest {

    private ResourceResponse resourceResponse;

    @BeforeEach
    void setUp() {
        // 初始化 ResourceResponse 对象
        LocalDateTime now = LocalDateTime.now();
        resourceResponse = new ResourceResponse(1, "Mathematics", "T001", "Content of the resource", "Class A", "Resource Name", now, "Description of the resource");
    }

    @Test
    void testGetResourceId() {
        assertEquals(1, resourceResponse.getResourceId());
    }

    @Test
    void testGetSubject() {
        assertEquals("Mathematics", resourceResponse.getSubject());
    }

    @Test
    void testGetTeacherId() {
        assertEquals("T001", resourceResponse.getTeacherId());
    }

    @Test
    void testGetResourceContent() {
        assertEquals("Content of the resource", resourceResponse.getResourceContent());
    }

    @Test
    void testGetClassId() {
        assertEquals("Class A", resourceResponse.getClassId());
    }

    @Test
    void testGetName() {
        assertEquals("Resource Name", resourceResponse.getName());
    }

    @Test
    void testGetUploadTime() {
        assertNotNull(resourceResponse.getUploadTime());
    }

    @Test
    void testGetDescription() {
        assertEquals("Description of the resource", resourceResponse.getDescription());
    }

    @Test
    void testConstructor() {
        ResourceResponse resource = new ResourceResponse(2, "Science", "T002", "Different content", "Class B", "Another Resource Name", LocalDateTime.now(), "Different description");
        assertEquals(2, resource.getResourceId());
        assertEquals("Science", resource.getSubject());
        assertEquals("T002", resource.getTeacherId());
        assertEquals("Different content", resource.getResourceContent());
        assertEquals("Class B", resource.getClassId());
        assertEquals("Another Resource Name", resource.getName());
        assertNotNull(resource.getUploadTime());
        assertEquals("Different description", resource.getDescription());
    }
}