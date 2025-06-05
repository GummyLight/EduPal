package com.example.edupal.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class ReplyTest {

    private Reply reply;

    @BeforeEach
    void setUp() {
        // 初始化 Reply 对象
        reply = new Reply();
        reply.setId("1234567890abcdef");
        reply.setPostId("post123");
        reply.setAuthorId("author123");
        reply.setAuthorName("Author Name");
        reply.setContent("This is the content of the reply.");
        reply.setPublishTime(LocalDateTime.now());
        reply.setAttachedFileName("example.txt");
        reply.setAttachedFileUrl("http://example.com/file.txt");
    }

    @Test
    void testGetId() {
        assertEquals("1234567890abcdef", reply.getId());
    }

    @Test
    void testGetPostId() {
        assertEquals("post123", reply.getPostId());
    }

    @Test
    void testGetAuthorId() {
        assertEquals("author123", reply.getAuthorId());
    }

    @Test
    void testGetAuthorName() {
        assertEquals("Author Name", reply.getAuthorName());
    }

    @Test
    void testGetContent() {
        assertEquals("This is the content of the reply.", reply.getContent());
    }

    @Test
    void testGetPublishTime() {
        assertNotNull(reply.getPublishTime());
    }

    @Test
    void testGetAttachedFileName() {
        assertEquals("example.txt", reply.getAttachedFileName());
    }

    @Test
    void testGetAttachedFileUrl() {
        assertEquals("http://example.com/file.txt", reply.getAttachedFileUrl());
    }

    @Test
    void testGetCreatedAt() {
        assertNotNull(reply.getCreatedAt());
    }

    @Test
    void testGetUpdatedAt() {
        assertNotNull(reply.getUpdatedAt());
    }

    @Test
    void testSetId() {
        reply.setId("fedcba9876543210");
        assertEquals("fedcba9876543210", reply.getId());
    }

    @Test
    void testSetPostId() {
        reply.setPostId("post456");
        assertEquals("post456", reply.getPostId());
    }

    @Test
    void testSetAuthorId() {
        reply.setAuthorId("author456");
        assertEquals("author456", reply.getAuthorId());
    }

    @Test
    void testSetAuthorName() {
        reply.setAuthorName("New Author Name");
        assertEquals("New Author Name", reply.getAuthorName());
    }

    @Test
    void testSetContent() {
        reply.setContent("New content for the reply.");
        assertEquals("New content for the reply.", reply.getContent());
    }

    @Test
    void testSetPublishTime() {
        LocalDateTime newPublishTime = LocalDateTime.now().plusHours(1);
        reply.setPublishTime(newPublishTime);
        assertEquals(newPublishTime, reply.getPublishTime());
    }

    @Test
    void testSetAttachedFileName() {
        reply.setAttachedFileName("new_example.txt");
        assertEquals("new_example.txt", reply.getAttachedFileName());
    }

    @Test
    void testSetAttachedFileUrl() {
        reply.setAttachedFileUrl("http://example.com/new_file.txt");
        assertEquals("http://example.com/new_file.txt", reply.getAttachedFileUrl());
    }
}