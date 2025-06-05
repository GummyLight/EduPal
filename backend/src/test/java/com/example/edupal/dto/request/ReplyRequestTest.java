package com.example.edupal.dto.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReplyRequestTest {

    private ReplyRequest replyRequest;

    @BeforeEach
    void setUp() {
        // 初始化 ReplyRequest 对象
        replyRequest = new ReplyRequest();
        replyRequest.setContent("This is a reply content.");
        replyRequest.setAuthorId("author123");
        replyRequest.setAttachedFileUrl("http://example.com/attached_file.txt");
    }

    @Test
    void testGetContent() {
        assertEquals("This is a reply content.", replyRequest.getContent());
    }

    @Test
    void testGetAuthorId() {
        assertEquals("author123", replyRequest.getAuthorId());
    }

    @Test
    void testGetAttachedFileUrl() {
        assertEquals("http://example.com/attached_file.txt", replyRequest.getAttachedFileUrl());
    }

    @Test
    void testSetContent() {
        replyRequest.setContent("New reply content.");
        assertEquals("New reply content.", replyRequest.getContent());
    }

    @Test
    void testSetAuthorId() {
        replyRequest.setAuthorId("author456");
        assertEquals("author456", replyRequest.getAuthorId());
    }

    @Test
    void testSetAttachedFileUrl() {
        replyRequest.setAttachedFileUrl("http://example.com/new_attached_file.txt");
        assertEquals("http://example.com/new_attached_file.txt", replyRequest.getAttachedFileUrl());
    }
}