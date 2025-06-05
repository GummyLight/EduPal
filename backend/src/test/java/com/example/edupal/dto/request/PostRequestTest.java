package com.example.edupal.dto.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PostRequestTest {

    private PostRequest postRequest;

    @BeforeEach
    void setUp() {
        // 初始化 PostRequest 对象
        postRequest = new PostRequest();
        postRequest.setAuthorId("author123");
        postRequest.setTitle("Test Title");
        postRequest.setContent("This is the content of the post.");
        postRequest.setAttachedFileUrl("http://example.com/file.txt");
    }

    @Test
    void testGetAuthorId() {
        assertEquals("author123", postRequest.getAuthorId());
    }

    @Test
    void testGetTitle() {
        assertEquals("Test Title", postRequest.getTitle());
    }

    @Test
    void testGetContent() {
        assertEquals("This is the content of the post.", postRequest.getContent());
    }

    @Test
    void testGetAttachedFileUrl() {
        assertEquals("http://example.com/file.txt", postRequest.getAttachedFileUrl());
    }

    @Test
    void testSetAuthorId() {
        postRequest.setAuthorId("author456");
        assertEquals("author456", postRequest.getAuthorId());
    }

    @Test
    void testSetTitle() {
        postRequest.setTitle("New Title");
        assertEquals("New Title", postRequest.getTitle());
    }

    @Test
    void testSetContent() {
        postRequest.setContent("New content for the post.");
        assertEquals("New content for the post.", postRequest.getContent());
    }

    @Test
    void testSetAttachedFileUrl() {
        postRequest.setAttachedFileUrl("http://example.com/new_file.txt");
        assertEquals("http://example.com/new_file.txt", postRequest.getAttachedFileUrl());
    }
}