package com.example.edupal.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

class ReplyResponseTest {

    private ReplyResponse replyResponse;

    @BeforeEach
    void setUp() {
        // 初始化 ReplyResponse 对象
        replyResponse = new ReplyResponse("R001", "author123", "Author Name", "Reply content", LocalDateTime.now(), "http://example.com/file");
    }

    @Test
    void testGetId() {
        assertEquals("R001", replyResponse.getId());
    }

    @Test
    void testGetAuthorId() {
        assertEquals("author123", replyResponse.getAuthorId());
    }

    @Test
    void testGetAuthorName() {
        assertEquals("Author Name", replyResponse.getAuthorName());
    }

    @Test
    void testGetContent() {
        assertEquals("Reply content", replyResponse.getContent());
    }

    @Test
    void testGetPublishTime() {
        assertNotNull(replyResponse.getPublishTime());
    }

    @Test
    void testGetAttachedFileUrl() {
        assertEquals("http://example.com/file", replyResponse.getAttachedFileUrl());
    }
}