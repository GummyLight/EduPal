package com.example.edupal.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PostCollectionTest {

    private PostCollection postCollection;

    @BeforeEach
    void setUp() {
        // 初始化 PostCollection 对象
        postCollection = new PostCollection();
        postCollection.setUserId("user123");
        postCollection.setPostId("post456");
    }

    @Test
    void testGetUserId() {
        assertEquals("user123", postCollection.getUserId());
    }

    @Test
    void testGetPostId() {
        assertEquals("post456", postCollection.getPostId());
    }

    @Test
    void testSetUserId() {
        postCollection.setUserId("user789");
        assertEquals("user789", postCollection.getUserId());
    }

    @Test
    void testSetPostId() {
        postCollection.setPostId("post101112");
        assertEquals("post101112", postCollection.getPostId());
    }

    @Test
    void testPostCollectionId() {
        PostCollection.PostCollectionId id = new PostCollection.PostCollectionId();
        id.setUserId("user123");
        id.setPostId("post456");
        assertEquals("user123", id.getUserId());
        assertEquals("post456", id.getPostId());
    }

    // 测试与 Post 实体的关联
    @Test
    void testGetPost() {
        Post post = new Post();
        postCollection.setPost(post);
        assertSame(post, postCollection.getPost());
    }

    @Test
    void testSetPost() {
        Post post = new Post();
        postCollection.setPost(post);
        assertSame(post, postCollection.getPost());
    }
}