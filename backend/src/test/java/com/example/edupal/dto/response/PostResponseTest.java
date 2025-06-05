package com.example.edupal.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

class PostResponseTest {

    private PostResponse postResponse;

    @BeforeEach
    void setUp() {
        // 初始化 PostResponse 对象
        List<ReplyResponse> replies = Arrays.asList(
                new ReplyResponse("R001", "author123", "Author Name", "Reply content", LocalDateTime.now(), "http://example.com/file")
        );
        postResponse = new PostResponse("P001", "Post Title", "Post Content", "Author ID", "Author Name",
                LocalDateTime.now(), false, "http://example.com/attached", replies);
    }

    @Test
    void testGetId() {
        assertEquals("P001", postResponse.getId());
    }

    @Test
    void testGetTitle() {
        assertEquals("Post Title", postResponse.getTitle());
    }

    @Test
    void testGetContent() {
        assertEquals("Post Content", postResponse.getContent());
    }

    @Test
    void testGetAuthorId() {
        assertEquals("Author ID", postResponse.getAuthorId());
    }

    @Test
    void testGetAuthorName() {
        assertEquals("Author Name", postResponse.getAuthorName());
    }

    @Test
    void testGetPublishTime() {
        assertNotNull(postResponse.getPublishTime());
    }

    @Test
    void testGetIsCollected() {
        assertFalse(postResponse.getIsCollected());
    }

    @Test
    void testGetAttachedFileUrl() {
        assertEquals("http://example.com/attached", postResponse.getAttachedFileUrl());
    }

    @Test
    void testGetReplies() {
        List<ReplyResponse> replies = postResponse.getReplies();
        assertNotNull(replies);
        assertEquals(1, replies.size()); // 假设我们预期只有一个回复

        ReplyResponse reply = replies.get(0);
        assertEquals("R001", reply.getId()); // 检查回复的ID是否正确
        assertEquals("author123", reply.getAuthorId()); // 检查回复的作者ID是否正确
        assertEquals("Author Name", reply.getAuthorName()); // 检查回复的作者名称是否正确
        assertEquals("Reply content", reply.getContent()); // 检查回复内容是否正确
        assertNotNull(reply.getPublishTime()); // 检查发布时间是否已设置
        // 以下断言用于检查发布时间是否在当前时间的合理范围内
        LocalDateTime now = LocalDateTime.now();
        assertTrue(reply.getPublishTime().isBefore(now.plusMinutes(1))); // 检查发布时间是否早于当前时间加1分钟
        assertTrue(reply.getPublishTime().isAfter(now.minusMinutes(1))); // 检查发布时间是否晚于当前时间减1分钟

        // 检查回复的附件文件URL是否正确
        String expectedFileUrl = "http://example.com/file";
        assertEquals(expectedFileUrl, reply.getAttachedFileUrl());
    }
}