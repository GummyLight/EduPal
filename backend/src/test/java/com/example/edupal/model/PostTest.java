package com.example.edupal.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class PostTest {

    private Post post;

    @BeforeEach
    void setUp() {
        // 初始化 Post 对象
        post = new Post();
        post.setId("1234567890abcdef");
        post.setTitle("Test Title");
        post.setContent("This is the content of the post.");
        post.setAuthorId("author123");
        post.setAuthorName("Author Name");
        post.setPublishTime(LocalDateTime.now());
        post.setAttachedFileName("example.txt");
        post.setAttachedFileUrl("http://example.com/file.txt");
    }

    @Test
    void testGetId() {
        assertEquals("1234567890abcdef", post.getId());
    }

    @Test
    void testGetTitle() {
        assertEquals("Test Title", post.getTitle());
    }

    @Test
    void testGetContent() {
        assertEquals("This is the content of the post.", post.getContent());
    }

    @Test
    void testGetAuthorId() {
        assertEquals("author123", post.getAuthorId());
    }

    @Test
    void testGetAuthorName() {
        assertEquals("Author Name", post.getAuthorName());
    }

    @Test
    void testGetPublishTime() {
        assertNotNull(post.getPublishTime());
    }

    @Test
    void testGetAttachedFileName() {
        assertEquals("example.txt", post.getAttachedFileName());
    }

    @Test
    void testGetAttachedFileUrl() {
        assertEquals("http://example.com/file.txt", post.getAttachedFileUrl());
    }

    @Test
    void testGetCreatedAt() {
        assertNotNull(post.getCreatedAt());
    }

    @Test
    void testGetUpdatedAt() {
        assertNotNull(post.getUpdatedAt());
    }

    @Test
    void testSetId() {
        post.setId("fedcba9876543210");
        assertEquals("fedcba9876543210", post.getId());
    }

    @Test
    void testSetTitle() {
        post.setTitle("New Title");
        assertEquals("New Title", post.getTitle());
    }

    @Test
    void testSetContent() {
        post.setContent("New content for the post.");
        assertEquals("New content for the post.", post.getContent());
    }

    @Test
    void testSetAuthorId() {
        post.setAuthorId("newAuthor123");
        assertEquals("newAuthor123", post.getAuthorId());
    }

    @Test
    void testSetAuthorName() {
        post.setAuthorName("New Author Name");
        assertEquals("New Author Name", post.getAuthorName());
    }

    @Test
    void testSetPublishTime() {
        LocalDateTime newPublishTime = LocalDateTime.now().plusHours(1);
        post.setPublishTime(newPublishTime);
        assertEquals(newPublishTime, post.getPublishTime());
    }

    @Test
    void testSetAttachedFileName() {
        post.setAttachedFileName("new_example.txt");
        assertEquals("new_example.txt", post.getAttachedFileName());
    }

    @Test
    void testSetAttachedFileUrl() {
        post.setAttachedFileUrl("http://example.com/new_file.txt");
        assertEquals("http://example.com/new_file.txt", post.getAttachedFileUrl());
    }
}